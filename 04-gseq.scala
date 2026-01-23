import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.foton.GFunction
import io.computenode.cyfra.runtime.VkCyfraRuntime

// Create from a known list of elements
val (red, green, blue) = (1f, 2f, 3f)
val colors             = GSeq.of(List(red, green, blue))

// Generate integers: 0, 1, 2, 3, ...
val integers = GSeq.gen[Int32](0, n => n + 1)

// Generate Fibonacci-like: (0,1), (1,1), (1,2), (2,3), ...
val fibonacci = GSeq.gen[Vec2[Float32]]((0f, 1f), pair => (pair.y, pair.x + pair.y))

// Mandelbrot iteration: z = z² + c
val (cx, cy)   = (0.4f, 0.3f)
val mandelbrot = GSeq.gen(
  vec2(0.0f, 0.0f),
  z => vec2(z.x * z.x - z.y * z.y + cx, 2.0f * z.x * z.y + cy)
)

// Map: transform each element
val doubled = GSeq.gen[Int32](0, _ + 1).limit(100).map(_ * 2)

// Filter: keep only matching elements
val evens = GSeq.gen[Int32](0, _ + 1).limit(100).filter(n => n.mod(2) === 0)

// TakeWhile: stop when condition becomes false
val underTen = GSeq.gen[Int32](0, _ + 1).limit(100).takeWhile(_ < 10)

// Julia set iteration: iterate until escape or limit
val uv         = (1f, 2f)
val v          = (1f, 2f)
val const      = (1f, 2f)
val iterations = GSeq
  .gen(uv, v => ((v.x * v.x) - (v.y * v.y), 2.0f * v.x * v.y) + const)
  .limit(1000)
  .map(length)         // Transform to magnitude
  .takeWhile(_ < 2.0f) // Stop when magnitude exceeds 2

// Count: number of elements that passed through
val iterationCount: Int32 = GSeq
  .gen(vec2(0f, 0f), z => vec2(z.x * z.x - z.y * z.y + cx, 2f * z.x * z.y + cy))
  .limit(256)
  .takeWhile(z => z.x * z.x + z.y * z.y < 4.0f)
  .count

// Fold: reduce with accumulator
val sum: Int32 = GSeq.gen[Int32](1, _ + 1).limit(10).fold(0, _ + _)

// LastOr: get final element (or default if empty)
val finalValue: Int32 = GSeq.gen[Int32](0, _ + 1).limit(10).lastOr(0)

@main
def runGSeq(): Unit = VkCyfraRuntime.using:
  println(finalValue) // TODO: how to print results from a GSeq
