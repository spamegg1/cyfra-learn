import io.computenode.cyfra.core.GBufferRegion
import io.computenode.cyfra.runtime.VkCyfraRuntime
import io.computenode.cyfra.dsl.{*, given}

@main
def run = VkCyfraRuntime.using:
  val size      = 256
  val inputData = (0 until size).map(_.toFloat).toArray
  val results   = Array.ofDim[Float](size)

  val region = GBufferRegion
    .allocate[DoubleLayout]
    .map: layout =>
      doubleProgram.execute(size, layout)

  region.runUnsafe(
    // Init values on start of the pipeline
    init = DoubleLayout(
      input = GBuffer(inputData),
      output = GBuffer[Float32](size)
    ),
    // Read results when done
    onDone = layout => layout.output.readArray(results)
  )

  println(s"Results: ${results.take(10).mkString(", ")}...")
  // Results: 0.0, 2.0, 4.0, 6.0, 8.0...
