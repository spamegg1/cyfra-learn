import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.foton.GFunction
import io.computenode.cyfra.runtime.VkCyfraRuntime

@main
def multiplyByTwo1(): Unit = VkCyfraRuntime.using:
  val input = (0 until 256).map(_.toFloat).toArray

  val doubleIt: GFunction[GStruct.Empty, Float32, Float32] = GFunction: x =>
    x * 2.0f

  val result: Array[Float] = doubleIt.run(input)
  println(s"Output: ${result.take(10).mkString(", ")}...")
