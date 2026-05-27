import io.computenode.cyfra.core.{GBufferRegion, GExecution, GProgram}
import io.computenode.cyfra.core.GProgram.StaticDispatch
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

object GpuPipelines:
  // Step 1: Define individual programs
  case class DoubleLayout(input: GBuffer[Float32], output: GBuffer[Float32]) derives Layout

  val doubleProgram: GProgram[Int, DoubleLayout] = GProgram[Int, DoubleLayout](
    layout = size =>
      DoubleLayout(
        input = GBuffer[Float32](size),
        output = GBuffer[Float32](size)
      ),
    dispatch = (_, size) => StaticDispatch(((size + 255) / 256, 1, 1)),
    workgroupSize = (256, 1, 1)
  ): layout =>
    val idx = GIO.invocationId
    GIO.when(idx < 256):
      val value = GIO.read(layout.input, idx)
      GIO.write(layout.output, idx, value * 2.0f)

  case class SumParams(value: Float32) extends GStruct[SumParams]
  case class SumLayout(
      input: GBuffer[Float32],
      output: GBuffer[Float32],
      params: GUniform[SumParams]
  ) derives Layout

  val sumProgram: GProgram[Int, SumLayout] = GProgram[Int, SumLayout](
    layout = size =>
      SumLayout(
        input = GBuffer[Float32](size),
        output = GBuffer[Float32](size),
        params = GUniform[SumParams]()
      ),
    dispatch = (_, size) => StaticDispatch(((size + 255) / 256, 1, 1)),
    workgroupSize = (256, 1, 1)
  ): layout =>
    val idx = GIO.invocationId
    GIO.when(idx < 256):
      val value    = GIO.read(layout.input, idx)
      val addValue = layout.params.read.value
      GIO.write(layout.output, idx, value + addValue)

  // Step 2: Define the combined pipeline layout
  case class PipelineLayout(
      input: GBuffer[Float32],
      doubled: GBuffer[Float32], // Intermediate buffer
      output: GBuffer[Float32],
      sumParams: GUniform[SumParams]
  ) derives Layout

  // Step 3: Compose the pipeline
  val doubleAndAddPipeline: GExecution[Int, PipelineLayout, PipelineLayout] =
    GExecution[Int, PipelineLayout]()
      .addProgram(doubleProgram)(
        size => size,                                        // Map params: pipeline size -> program size
        layout => DoubleLayout(layout.input, layout.doubled) // Map layout
      )
      .addProgram(sumProgram)(
        size => size,
        layout => SumLayout(layout.doubled, layout.output, layout.sumParams)
      )

  @main
  def runDoubleAndSumPipeline(): Unit = VkCyfraRuntime.using:
    val size      = 256
    val inputData = (0 until size).map(_.toFloat).toArray
    val results   = Array.ofDim[Float](size)

    val region = GBufferRegion
      .allocate[PipelineLayout]
      .map: layout =>
        doubleAndAddPipeline.execute(size, layout)

    region.runUnsafe(
      init = PipelineLayout(
        input = GBuffer(inputData),
        doubled = GBuffer[Float32](size),
        output = GBuffer[Float32](size),
        sumParams = GUniform(SumParams(10.0f))
      ),
      onDone = layout => layout.output.readArray(results)
    )

    println(s"Results: ${results.take(10).mkString(", ")}...")
