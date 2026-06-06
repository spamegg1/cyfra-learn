package parprog
package blur

object Runner:
  import org.scalameter.*

  val standardConfig = config(
    Key.exec.minWarmupRuns := 5,
    Key.exec.maxWarmupRuns := 10,
    Key.exec.benchRuns     := 10,
    Key.verbose            := false
  ).withWarmer(Warmer.Default())

  @main
  def runCpuBlur: Unit =
    val radius   = 3
    val numTasks = 16
    val src      = Img.load
    val dst1     = Img(src.width, src.height)
    val dst2     = Img(src.width, src.height)

    val seqtime = standardConfig.measure:
      CpuBlur.blur(src, dst1, 0, src.height, radius)
    val partime = standardConfig.measure:
      CpuBlur.parBlur(src, dst2, numTasks, radius)

    println(s"sequential blur time: $seqtime")
    println(s"number of tasks: $numTasks")
    println(s"fork/join blur time: $partime")
    println(s"speedup: ${seqtime.value / partime.value}")

    Img.save(dst2)
  end runCpuBlur

  @main
  def runGpuBlur =
    val size = 256
    val r    = 3
    val src  = Img.load
    val w    = src.width
    val h    = src.height
    val dst1 = Img(w, h)
    val dst2 = Img(w, h)

    val seqtime = standardConfig.measure:
      CpuBlur.blur(src, dst1, 0, h, r)
    val partime = standardConfig.measure:
      GpuBlur.parBlur(???, size, r, w, h)

    println(s"sequential blur time: $seqtime")
    println(s"Gpu parallel blur time: $partime")
    println(s"speedup: ${seqtime.value / partime.value}")

    Img.save(dst2)
  end runGpuBlur
end Runner
