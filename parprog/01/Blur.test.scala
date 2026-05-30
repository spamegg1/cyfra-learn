package parprog
package blur

class BlurSuite extends munit.FunSuite:
  def check(x: Int, y: Int, expected: Int)(using dest: Img) =
    assertEquals(dest(x, y), expected, s"(destination($x, $y) should be $expected)")

  test("boxBlurKernel should correctly handle radius 0"):
    val src = Img(5, 5)
    for
      x <- 0 until 5
      y <- 0 until 5
    do src(x, y) = RGBA.rgba(x, y, x + y, math.abs(x - y))
    for
      x <- 0 until 5
      y <- 0 until 5
    do
      assertEquals(
        Img.boxBlurKernel(src, x, y, 0),
        RGBA.rgba(x, y, x + y, math.abs(x - y)),
        "boxBlurKernel(_,_,0) should be identity."
      )

  test("boxBlurKernel should return the correct value on an interior pixel of a 3x4 image with radius 1"):
    val src = Img(3, 4)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8
    src(0, 3) = 50; src(1, 3) = 11; src(2, 3) = 16

    val res = Img.boxBlurKernel(src, 1, 2, 1)

    assertEquals(res, 12, s"(boxBlurKernel(1, 2, 1) should be 12, but it's ${res})")
