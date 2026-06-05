package parprog
package blur

/** A simple, trivially parallelizable computation. */
object HorizBlur:
  /** Blurs the rows of the source image `src` into the destination image `dst`, starting with `from` and ending with
    * `end` (non-inclusive). Within each row, `blur` traverses the pixels by going from left to right.
    */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = // TODO
    // implement this method using the `boxBlurKernel` method
    var y: Int = from
    var x: Int = 0

    while y < end do
      while x < src.width do
        dst(x, y) = src.boxBlurKernel(x, y, radius)
        x = x + 1 // go to right pixel
      end while
      y = y + 1 // go to next row
      x = 0     // go back to leftmost
    end while
  end blur

  /** Blurs the rows of the source image in parallel using `numTasks` tasks. Parallelization is done by stripping the
    * source image `src` into `numTasks` separate strips, where each strip is composed of some number of rows.
    */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = // TODO
    // implement using the `task` construct and the `blur` method
    val stripSize: Int = math.max(src.height / numTasks, 1)
    val tasks          =
      for
        from <- 0 until src.height by stripSize
        endPt = math.min(from + stripSize, src.height) // careful!
      yield Parallel.task:
        blur(src, dst, from, endPt, radius)
    tasks.map(_.join) // joining OUTSIDE for-loop necessary for parallelization

  @main
  def mainHorizontal: Unit =
    Runner.runBlur(HorizBlur.blur, HorizBlur.parBlur, horiz = true)
