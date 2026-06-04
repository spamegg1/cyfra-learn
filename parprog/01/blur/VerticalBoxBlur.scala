package parprog
package blur

/** A simple, trivially parallelizable computation. */
object VerticalBoxBlur:
  /** Blurs the columns of the source image `src` into the destination image `dst`, starting with `from` and ending with
    * `end` (non-inclusive). Within each column, `blur` traverses the pixels by going from top to bottom.
    */
  def blur(src: Img, dst: Img, from: Int, end: Int, radius: Int): Unit = // TODO
    // implement this method using the `boxBlurKernel` method
    var x: Int = from
    var y: Int = 0

    while x < end do
      while y < src.height do
        dst(x, y) = src.boxBlurKernel(x, y, radius)
        y = y + 1 // go to pixel below
      x = x + 1   // go to next column
      y = 0       // go back to top

  /** Blurs the columns of the source image in parallel using `numTasks` tasks. Parallelization is done by stripping the
    * source image `src` into `numTasks` separate strips, where each strip is composed of some number of columns.
    */
  def parBlur(src: Img, dst: Img, numTasks: Int, radius: Int): Unit = // TODO
    // implement using the `task` construct and the `blur` method
    val stripSize: Int = math.max(src.width / numTasks, 1)
    val tasks          =
      for
        from <- 0 until src.width by stripSize
        endPt = math.min(from + stripSize, src.width) // careful!
      yield Parallel.task:
        blur(src, dst, from, endPt, radius)
    tasks.map(_.join) // joining OUTSIDE for-loop necessary for parallelization

  @main
  def mainVertical: Unit =
    Runner.runBlur(VerticalBoxBlur.blur, VerticalBoxBlur.parBlur, false)
