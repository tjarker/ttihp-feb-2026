
import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width

class Latch(w: Int) extends BlackBox(Map(
  "WIDTH" -> w
)) with HasBlackBoxPath {
  val io = IO(new Bundle {
    val rst_n = Input(Bool())
    val hold = Input(Bool())
    val d = Input(UInt(w.W))
    val q = Output(UInt(w.W))
  })
  //addPath("src/latch.sv")
}

object Latch {
  def apply(d: UInt, hold: Bool, rst_n: Bool): UInt = {
    val latch = Module(new Latch(d.getWidth))
    latch.io.rst_n := rst_n
    latch.io.hold := hold
    latch.io.d := d
    latch.io.q
  }
  def apply[T <: Data](d: T, hold: Bool, rst_n: Bool): T = {
    apply(d.asUInt, hold, rst_n).asTypeOf(d)
  }
}


class Delay(w: Int, delay: Int) extends BlackBox(Map(
  "WIDTH" -> w,
  "DELAY" -> delay
)) with HasBlackBoxPath {
  val io = IO(new Bundle {
    val d = Input(UInt(w.W))
    val q = Output(UInt(w.W))
  })
  //addPath("src/delay.sv")
}

object Delay {
  def apply[T <: Data](d: T, delay: Int): T = {
    val delayModule = Module(new Delay(d.getWidth, delay))
    delayModule.io.d := d.asUInt
    delayModule.io.q.asTypeOf(d)
  }
}