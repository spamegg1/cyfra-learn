package parprog
package blur

object Runner:
  import org.scalameter.*

  type SeqBlur = (Img, Img, Int, Int, Int) => Unit
  type ParBlur = (Img, Img, Int, Int) => Unit

  val standardConfig = config(
    Key.exec.minWarmupRuns := 5,
    Key.exec.maxWarmupRuns := 10,
    Key.exec.benchRuns     := 10,
    Key.verbose            := false
  ).withWarmer(Warmer.Default())

  def runBlur(seqBlur: SeqBlur, parBlur: ParBlur, horiz: Boolean): Unit =
    val radius   = 4
    val numTasks = 8
    val src      = Img.load
    val dst1     = Img(src.width, src.height)
    val dst2     = Img(src.width, src.height)

    val seqtime = standardConfig.measure:
      val to = if horiz then src.height else src.width
      seqBlur(src, dst1, 0, to, radius)
    val partime = standardConfig.measure:
      parBlur(src, dst2, numTasks, radius)

    println(s"sequential blur time: $seqtime")
    println(s"number of tasks: $numTasks")
    println(s"fork/join blur time: $partime")
    println(s"speedup: ${seqtime.value / partime.value}")

    Img.save(dst2, horiz)
  end runBlur
end Runner
