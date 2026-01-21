import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.foton.GFunction
import io.computenode.cyfra.runtime.VkCyfraRuntime

case class FunctionParam(a: Float32) extends GStruct[FunctionParam]

@main
def multiplyByTwo2(): Unit = VkCyfraRuntime.using:
  val input = (0 until 256).map(_.toFloat).toArray

  val doubleIt: GFunction[FunctionParam, Float32, Float32] = GFunction: (params: FunctionParam, x: Float32) =>
    x * params.a

  val params               = FunctionParam(2.0f)
  val result: Array[Float] = doubleIt.run(input, params)
  println(s"Output: ${result.take(10).mkString(", ")}...")
