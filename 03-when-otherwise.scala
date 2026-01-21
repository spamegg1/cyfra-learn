import io.computenode.cyfra.dsl.{*, given}
import io.computenode.cyfra.foton.GFunction
import io.computenode.cyfra.runtime.VkCyfraRuntime

val multiplyIt: GFunction[FunctionParam, Float32, Float32] = GFunction: (params: FunctionParam, x: Float32) =>
  when(x < 100f):
    x * params.a
  .elseWhen(x < 200f):
    x * params.a * 2f
  .otherwise:
    x * params.a * 4f
