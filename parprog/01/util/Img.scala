package parprog
package blur

import Rgba.*

/** Image is a two-dimensional matrix of pixel values. */
class Img(val width: Int, val height: Int, private val data: Array[Rgba]):
  def apply(x: Int, y: Int): Rgba           = data(y * width + x)
  def update(x: Int, y: Int, c: Rgba): Unit = data(y * width + x) = c

  /** Computes the blurred Rgba value of a single pixel of the input image. */
  def boxBlurKernel(x: Int, y: Int, radius: Int): Rgba = // TODO
    // implement using while loops
    /* declare variables for the 4 averages and neighbor count */
    var rnew, gnew, bnew, anew, count = 0

    /* define bounds for the while loops by clamping down on x -+ radius, y -+ radius */
    val xmin = Img.clamp(x - radius, 0, width - 1)
    val xmax = Img.clamp(x + radius, 0, width - 1)
    val ymin = Img.clamp(y - radius, 0, height - 1)
    val ymax = Img.clamp(y + radius, 0, height - 1)

    /* define variables for the while loops */
    var i = xmin
    var j = ymin

    /* get neighbors within clamped borders */
    while i <= xmax do
      while j <= ymax do
        val neighbor = apply(i, j)
        count += 1

        /* add neighbor's Rgba values to accumulated 4 channels */
        rnew = rnew + neighbor.red
        gnew = gnew + neighbor.green
        bnew = bnew + neighbor.blue
        anew = anew + neighbor.alpha
        j += 1
      end while
      i += 1
      j = ymin // back to leftmost
    end while

    /* average the 4 channels and create a new Rgba value out of them */
    Rgba.rgba(rnew / count, gnew / count, bnew / count, anew / count)
  end boxBlurKernel
end Img

object Img:
  import java.awt.image.BufferedImage
  import java.io.{InputStream, OutputStream}
  import javax.imageio.ImageIO

  def apply(w: Int, h: Int) = new Img(w, h, new Array(w * h))

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int =
    if v < min then min else if v > max then max else v

  def save(img: Img, horiz: Boolean): Unit =
    val name   = if horiz then "horiz" else "vert"
    val file   = os.pwd / "parprog" / "01" / "images" / s"cream-blurred-$name.jpg"
    val stream = os.write.outputStream(file)
    try saveImage(stream, img)
    finally stream.close()

  private def saveImage(stream: OutputStream, img: Img): Unit =
    val width  = img.width
    val height = img.height
    val buf    = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    for
      x <- 0 until width
      y <- 0 until height
    do buf.setRGB(x, y, img(x, y))
    ImageIO.write(buf, "jpg", stream)

  def load: Img =
    val file   = os.pwd / "parprog" / "01" / "images" / "cream.jpg"
    val stream = os.read.inputStream(file)
    try loadImage(stream)
    finally stream.close()

  private def loadImage(inputStream: InputStream): Img =
    val buf    = ImageIO.read(inputStream)
    val width  = buf.getWidth
    val height = buf.getHeight
    val img    = Img(width, height)
    for
      x <- 0 until width
      y <- 0 until height
    do img(x, y) = buf.getRGB(x, y)
    img
end Img
