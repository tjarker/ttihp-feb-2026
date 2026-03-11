
import chisel3._
import chisel3.util._
import chisel3.internal.firrtl.Width

class Latch(w: Int) extends BlackBox(Map(
  "WIDTH" -> w
)) with HasBlackBoxPath {
  val io = IO(new Bundle {
    val gate = Input(Bool())
    val d = Input(UInt(w.W))
    val q = Output(UInt(w.W))
  })
  //addPath("src/latch.sv")
}

object Latch {
  def apply(d: UInt, gate: Bool): UInt = {
    val latch = Module(new Latch(d.getWidth))
    latch.io.gate := gate
    latch.io.d := d
    latch.io.q
  }
}