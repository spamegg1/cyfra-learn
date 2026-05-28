package parprog
package blur

import RGBA.*

/** Image is a two-dimensional matrix of pixel values. */
class Img(val width: Int, val height: Int, private val data: Array[RGBA]):
  def apply(x: Int, y: Int): RGBA           = data(y * width + x)
  def update(x: Int, y: Int, c: RGBA): Unit = data(y * width + x) = c

object Img:
  def apply(w: Int, h: Int) = new Img(w, h, new Array(w * h))

  /** Restricts the integer into the specified range. */
  def clamp(v: Int, min: Int, max: Int): Int =
    if v < min then min else if v > max then max else v

  /** Computes the blurred RGBA value of a single pixel of the input image. */
  def boxBlurKernel(src: Img, x: Int, y: Int, radius: Int): RGBA = // TODO
    // implement using while loops
    /* declare variables for the 4 averages and neighbor count */
    var rnew, gnew, bnew, anew, nghCount = 0

    /* define bounds for the while loops by clamping down on x -+ radius, y -+ radius */
    val xmin: Int = clamp(x - radius, 0, src.width - 1)
    val xmax: Int = clamp(x + radius, 0, src.width - 1)
    val ymin: Int = clamp(y - radius, 0, src.height - 1)
    val ymax: Int = clamp(y + radius, 0, src.height - 1)

    /* define variables for the while loops */
    var i: Int = xmin
    var j: Int = ymin

    /* get neighbors within clamped borders */
    while i <= xmax do
      while j <= ymax do
        val neighbor: RGBA = src(i, j)
        nghCount = nghCount + 1

        /* add neighbor's RGBA values to accumulated 4 channels */
        rnew = rnew + neighbor.red
        gnew = gnew + neighbor.green
        bnew = bnew + neighbor.blue
        anew = anew + neighbor.alpha
        j = j + 1

      i = i + 1
      j = ymin // back to leftmost
    end while

    /* average the 4 channels and create a new RGBA value out of them */
    RGBA.rgba(rnew / nghCount, gnew / nghCount, bnew / nghCount, anew / nghCount)
  end boxBlurKernel
end Img
