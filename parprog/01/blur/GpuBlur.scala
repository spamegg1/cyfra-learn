package parprog
package blur

import io.computenode.cyfra.core.{GBufferRegion, GProgram, layout}, layout.Layout
import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.runtime.VkCyfraRuntime

object GpuBlur:
  import GpuRgba.*

  // Define a struct for uniform parameters
  case class BlurPrm(r: Int32, w: Int32, h: Int32) extends GStruct[BlurPrm]

  case class BlurLay(
      in: GBuffer[Grgba],
      out: GBuffer[Grgba],
      prm: GUniform[BlurPrm]
  ) derives Layout

  val parBlurProg = GProgram.static[Int, BlurLay](
    layout = size =>
      BlurLay(
        in = GBuffer[Grgba](size),
        out = GBuffer[Grgba](size),
        prm = GUniform[BlurPrm]()
      ),
    dispatchSize = size => size
  ): layout =>
    val idx     = GIO.invocationId
    val pixel   = layout.in.read(idx)
    val blurred = ???
    layout.out.write(idx, blurred)

  def parBlur(in: Array[Int], size: Int, r: Int, w: Int, h: Int): Unit =
    VkCyfraRuntime.using:
      val results = Array.ofDim[Int](size)

      val region = GBufferRegion
        .allocate[BlurLay]
        .map: layout =>
          parBlurProg.execute(size, layout)

      region.runUnsafe(
        init = BlurLay(
          in = GBuffer(in),
          out = GBuffer[Int32](size),
          prm = GUniform(BlurPrm(r, w, h))
        ),
        onDone = layout => layout.out.readArray(results)
      )
