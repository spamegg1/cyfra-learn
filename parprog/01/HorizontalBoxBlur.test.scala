package parprog
package blur

class HorizontalBoxBlurSuite extends BlurSuite:
  test("HorizontalBoxBlur.blur with radius 1 should correctly blur the entire 3x3 image"):
    val w          = 3
    val h          = 3
    val src        = Img(w, h)
    given dst: Img = Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8

    HorizontalBoxBlur.blur(src, dst, 0, 2, 1)

    check(0, 0, 2); check(1, 0, 2); check(2, 0, 3)
    check(0, 1, 3); check(1, 1, 4); check(2, 1, 4)
    check(0, 2, 0); check(1, 2, 0); check(2, 2, 0)

  test("HorizontalBoxBlur.parBlur with radius 2 should correctly blur the entire 4x3 image"):
    val w          = 4
    val h          = 3
    val src        = Img(w, h)
    given dst: Img = Img(w, h)
    src(0, 0) = 0; src(1, 0) = 1; src(2, 0) = 2; src(3, 0) = 9
    src(0, 1) = 3; src(1, 1) = 4; src(2, 1) = 5; src(3, 1) = 10
    src(0, 2) = 6; src(1, 2) = 7; src(2, 2) = 8; src(3, 2) = 11

    HorizontalBoxBlur.parBlur(src, dst, 3, 2)

    check(0, 0, 4); check(1, 0, 5); check(2, 0, 5); check(3, 0, 6)
    check(0, 1, 4); check(1, 1, 5); check(2, 1, 5); check(3, 1, 6)
    check(0, 2, 4); check(1, 2, 5); check(2, 2, 5); check(3, 2, 6)
