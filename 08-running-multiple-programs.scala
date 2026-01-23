import io.computenode.cyfra.core.{GBufferRegion, GProgram}
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

object RunningMultiplePrograms:
  // Program 1: Double the input
  case class DoubleLayout(input: GBuffer[Float32], output: GBuffer[Float32]) derives Layout

  val doubleProgram = GProgram.static[Int, DoubleLayout](
    layout = size => DoubleLayout(GBuffer[Float32](size), GBuffer[Float32](size)),
    dispatchSize = size => size
  ): layout =>
    val idx = GIO.invocationId
    GIO.when(idx < 256):
      val value = layout.input.read(idx)
      layout.output.write(idx, value * 2.0f)

  // Program 2: Add a constant
  case class AddParams(addend: Float32) extends GStruct[AddParams]
  case class AddLayout(input: GBuffer[Float32], output: GBuffer[Float32], params: GUniform[AddParams]) derives Layout

  val addProgram = GProgram.static[Int, AddLayout](
    layout = size => AddLayout(GBuffer[Float32](size), GBuffer[Float32](size), GUniform[AddParams]()),
    dispatchSize = size => size
  ): layout =>
    val idx = GIO.invocationId
    GIO.when(idx < 256):
      val value  = layout.input.read(idx)
      val addend = layout.params.read.addend
      layout.output.write(idx, value + addend)

  // Combined layout with intermediate buffer
  case class PipelineLayout(
      input: GBuffer[Float32],
      intermediate: GBuffer[Float32], // Output of program 1, input of program 2
      output: GBuffer[Float32],
      addParams: GUniform[AddParams]
  ) derives Layout

  @main
  def runPipeline(): Unit = VkCyfraRuntime.using:
    val size      = 256
    val inputData = (0 until size).map(_.toFloat).toArray
    val results   = Array.ofDim[Float](size)

    val region = GBufferRegion
      .allocate[PipelineLayout]
      .map: layout =>
        // Program 1: input -> intermediate (doubles values)
        val afterDouble = doubleProgram.execute(size, DoubleLayout(layout.input, layout.intermediate))

        // Program 2: use intermediate from afterDouble as input, write to output
        addProgram.execute(size, AddLayout(afterDouble.output, layout.output, layout.addParams))

        // Return the original layout
        layout

    region.runUnsafe(
      init = PipelineLayout(
        input = GBuffer(inputData),
        intermediate = GBuffer[Float32](size),
        output = GBuffer[Float32](size),
        addParams = GUniform(AddParams(10.0f))
      ),
      onDone = layout => layout.output.readArray(results)
    )
