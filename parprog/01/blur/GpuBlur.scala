package parprog
package blur

import io.computenode.cyfra.core.{GBufferRegion, GProgram, layout}, layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

object GpuBlur:
  import GpuRgba.*

  // Define a struct for uniform parameters
  case class BlurPrm(size: Int32, r: Int32, w: Int32, h: Int32) extends GStruct[BlurPrm]
  case class Param(size: Int, r: Int, w: Int, h: Int)

  case class BlurLay(
      in: GBuffer[Grgba],
      out: GBuffer[Grgba],
      prm: GUniform[BlurPrm]
  ) derives Layout

  val parBlurProg = GProgram.static[Param, BlurLay](
    layout = prm =>
      BlurLay(
        in = GBuffer[Grgba](prm.size),
        out = GBuffer[Grgba](prm.size),
        prm = GUniform[BlurPrm]()
      ),
    dispatchSize = prm => prm.size
  ): layout =>
    val prm             = layout.prm.read
    val (size, r, w, h) = (prm.size, prm.r, prm.w, prm.h)
    val idx             = GIO.invocationId
    val y               = idx / w
    val x               = idx - y * w

    /* declare variables for the 4 averages and neighbor count */
    var rnew, gnew, bnew, anew, count: Int32 = 0

    /* define bounds for the while loops by clamping down on x -+ radius, y -+ radius */
    val xmin = clampGpu(x - r, 0, w - 1)
    val xmax = clampGpu(x + r, 0, w - 1)
    val ymin = clampGpu(y - r, 0, h - 1)
    val ymax = clampGpu(y + r, 0, h - 1)

    GIO
      .repeat(xmax - xmin): i =>
        GIO.repeat(ymax - ymin): j =>
          val neighbor = layout.in.read(j * w + i)
          count += 1
          /* add neighbor's Rgba values to accumulated 4 channels */
          rnew += neighbor.red
          gnew += neighbor.green
          bnew += neighbor.blue
          anew += neighbor.alpha
          ??? // what goes here? () does not work
      .flatMap: _ =>
        val blurred = rgba(rnew / count, gnew / count, bnew / count, anew / count)
        GIO.write(layout.out, idx, blurred)

  def parBlur(in: Array[Int], out: Array[Int], size: Int, r: Int, w: Int, h: Int): Unit =
    VkCyfraRuntime.using:
      val region = GBufferRegion
        .allocate[BlurLay]
        .map: layout =>
          parBlurProg.execute(Param(size, r, w, h), layout)

      region.runUnsafe(
        init = BlurLay(
          in = GBuffer(in),
          out = GBuffer[Int32](size),
          prm = GUniform(BlurPrm(size, r, w, h))
        ),
        onDone = layout => layout.out.readArray(out)
      )
