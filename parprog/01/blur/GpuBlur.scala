package parprog
package blur

import io.computenode.cyfra.core.GProgram
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}

object GpuBlur:
  import GpuRgba.*

  case class BlurLayout(input: GBuffer[GpuRgba], output: GBuffer[GpuRgba]) derives Layout

  val blurProgram: GProgram[Int, BlurLayout] =
    GProgram.static[Int, BlurLayout](
      layout = size =>
        BlurLayout(
          input = GBuffer[GpuRgba](size),
          output = GBuffer[GpuRgba](size)
        ),
      dispatchSize = size => size
    ): layout =>
      val idx = GIO.invocationId
      GIO.when(idx < 256):
        val value = layout.input.read(idx)
        layout.output.write(idx, value)
