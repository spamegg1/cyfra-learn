package parprog
package blur

object Rgba:
  /** The value of every pixel is represented as a 32 bit integer. */
  type Rgba = Int

  /** Used to create an Rgba value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): Rgba =
    (r << 24) | (g << 16) | (b << 8) | (a << 0)

  extension (c: Rgba)
    def red: Int   = (0xff000000 & c) >> 24
    def green: Int = (0x00ff0000 & c) >> 16
    def blue: Int  = (0x0000ff00 & c) >> 8
    def alpha: Int = (0x000000ff & c) >> 0
end Rgba
