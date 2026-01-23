import io.computenode.cyfra.core.{GBufferRegion, GProgram}
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

// Define a struct for uniform parameters
case class MulParams(factor: Float32) extends GStruct[MulParams]

// Define the program layout
case class MulLayout(
    input: GBuffer[Float32],
    output: GBuffer[Float32],
    params: GUniform[MulParams]
) derives Layout

// Create the program
val mulProgram: GProgram[Int, MulLayout] = GProgram.static[Int, MulLayout](
  layout = size =>
    MulLayout(
      input = GBuffer[Float32](size),
      output = GBuffer[Float32](size),
      params = GUniform[MulParams]()
    ),
  dispatchSize = size => size
): layout =>
  val idx = GIO.invocationId
  GIO.when(idx < 256):
    val value  = layout.input.read(idx)
    val factor = layout.params.read.factor
    layout.output.write(idx, value * factor)

@main
def runMultiply(): Unit = VkCyfraRuntime.using:
  val size      = 256
  val inputData = (0 until size).map(_.toFloat).toArray
  val results   = Array.ofDim[Float](size)

  val region = GBufferRegion
    .allocate[MulLayout]
    .map: layout =>
      mulProgram.execute(size, layout)

  region.runUnsafe(
    init = MulLayout(
      input = GBuffer(inputData),
      output = GBuffer[Float32](size),
      params = GUniform(MulParams(3.0f)) // Multiply by 3
    ),
    onDone = layout => layout.output.readArray(results)
  )

  println(s"Results: ${results.take(10).mkString(", ")}...")
