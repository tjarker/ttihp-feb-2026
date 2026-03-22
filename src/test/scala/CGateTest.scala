import chisel3._
import liftoff._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import liftoff.simulation.verilator.Verilator

class CGateTest extends AnyWordSpec with Matchers {

  val buildDir = "build/chiseltop-model".toDir

  val model = ChiselModel(
    new ChiselTop, 
    buildDir,
    Seq("src/c_gates.sv".toFile, "sg13g2_stdcell.v".toFile),
    Seq(
      Verilator.Arguments.Timing, 
      Verilator.Arguments.CustomFlag("-Wno-TIMESCALEMOD"),
      Verilator.Arguments.CustomFlag("-Wno-UNOPTFLAT"),
      Verilator.Arguments.CustomFlag("-Wno-SPECIFYIGN"),
    ),
    Seq("-fcoroutines")
  )


  "CGates" should {
    "work" in {
      val rundir = "sim/c_gate_test".toDir
      model.simulate(rundir) { dut =>
        dut.reset.poke(1.B)
        dut.io.reset_n.poke(0.B)
        dut.clock.step()
        dut.reset.poke(0.B)
        dut.io.reset_n.poke(1.B)


        dut.clock.step(10)

        dut.io.ui_in.poke(1.U)
        dut.clock.step(10)
        dut.io.ui_in.poke(3.U)
        dut.clock.step(10)
        dut.io.ui_in.poke(2.U)
        dut.clock.step(10)
        dut.io.ui_in.poke(0.U)
        dut.clock.step(10)

      }
    }
  }
  
}
