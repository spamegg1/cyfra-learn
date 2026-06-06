package parprog
package blur

object GpuRgba:
  import io.computenode.cyfra.dsl.{*, given}

  /** The value of every pixel is represented as a 32 bit integer. */
  type Grgba = Int32

  /** Used to create an Rgba value from separate components. */
  def Rgba(r: Int32, g: Int32, b: Int32, a: Int32): Grgba =
    (r << 24) | (g << 16) | (b << 8) | (a << 0)

  extension (c: Grgba)
    def red: Int32   = c.&(0xff000000) >> 24
    def green: Int32 = c.&(0x00ff0000) >> 16
    def blue: Int32  = c.&(0x0000ff00) >> 8
    def alpha: Int32 = c.&(0x000000ff) >> 0
end GpuRgba
