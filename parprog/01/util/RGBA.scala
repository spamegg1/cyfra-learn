package parprog
package blur

object RGBA:
  /** The value of every pixel is represented as a 32 bit integer. */
  type RGBA = Int

  /** Used to create an RGBA value from separate components. */
  def rgba(r: Int, g: Int, b: Int, a: Int): RGBA =
    (r << 24) | (g << 16) | (b << 8) | (a << 0)

  extension (c: RGBA)
    def red: Int   = (0xff000000 & c) >>> 24
    def green: Int = (0x00ff0000 & c) >>> 16
    def blue: Int  = (0x0000ff00 & c) >>> 8
    def alpha: Int = (0x000000ff & c) >>> 0
end RGBA
