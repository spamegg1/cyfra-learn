import io.computenode.cyfra.core.GProgram
import io.computenode.cyfra.core.layout.Layout
import io.computenode.cyfra.dsl.{*, given}

case class DoubleLayout(
    input: GBuffer[Float32],
    output: GBuffer[Float32]
) derives Layout

val doubleProgram: GProgram[Int, DoubleLayout] = GProgram.static[Int, DoubleLayout](
  layout = size =>
    DoubleLayout(
      input = GBuffer[Float32](size),
      output = GBuffer[Float32](size)
    ),
  dispatchSize = size => size
):
  layout =>
    val idx = GIO.invocationId
    GIO.when(idx < 256):
      val value = layout.input.read(idx)
      layout.output.write(idx, value * 2.0f)

    // using monad with for-yield
    // val in = layout.input.read(idx)
    // for
    //   _ <- layout.outputA.write(idx, value) // Buffer writes return GIOs
    //   _ <- layout.outputB.write(idx, value * 2.0f)
    // yield ()
