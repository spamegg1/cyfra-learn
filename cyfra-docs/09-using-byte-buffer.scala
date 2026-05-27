import io.computenode.cyfra.core.{GBufferRegion, GProgram}
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

import java.nio.{ByteBuffer, ByteOrder}

object UsingByteBuffers:
  case class DoubleLayout(input: GBuffer[Int32], output: GBuffer[Int32]) derives Layout

  val doubleProgram = GProgram.static[Int, DoubleLayout](
    layout = size => DoubleLayout(GBuffer[Int32](size), GBuffer[Int32](size)),
    dispatchSize = size => size
  ): layout =>
    val idx   = GIO.invocationId
    val value = layout.input.read(idx)
    layout.output.write(idx, value * 2)

  @main
  def runByteBuffer(): Unit = VkCyfraRuntime.using:
    val size       = 1024
    val data       = (0 until size).toArray
    val byteBuffer = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder())
    byteBuffer.asIntBuffer().put(data)
    byteBuffer.flip()

    val resultBuffer = ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder())

    val region = GBufferRegion
      .allocate[DoubleLayout]
      .map: layout =>
        val afterDouble = doubleProgram.execute(size, DoubleLayout(layout.input, layout.output))
        layout

    region.runUnsafe(
      init = DoubleLayout(
        input = GBuffer[Int32](byteBuffer), // Initialize from ByteBuffer
        output = GBuffer[Int32](size)
      ),
      onDone = layout =>
        layout.output.read(resultBuffer) // Read into ByteBuffer
        // layout.someBuffer.write(updateBuffer) // write to GPU
    )

    // Access results
    val intView    = resultBuffer.asIntBuffer()
    val firstValue = intView.get(0)
    println(firstValue)
