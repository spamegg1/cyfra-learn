package parprog
package blur

import io.computenode.cyfra.core.GProgram
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}

object GpuBlur:
  import GpuRgba.*

  case class BlurLay(in: GBuffer[GpuRgba], out: GBuffer[GpuRgba]) derives Layout

  val blurProgram = GProgram.static[Int, BlurLay](
    layout = size => BlurLay(in = GBuffer[GpuRgba](size), out = GBuffer[GpuRgba](size)),
    dispatchSize = size => size
  ): layout =>
    val idx     = GIO.invocationId
    val pixel   = layout.in.read(idx)
    val blurred = ???
    layout.out.write(idx, blurred)
