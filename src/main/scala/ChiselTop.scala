import chisel3._
import chisel3.util._

/**
 * Example design in Chisel.
 * A redesign of the Tiny Tapeout example.
 */
class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val reset_n = Input(Bool()) // Active low reset
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    val uio_out = Output(UInt(8.W))   // IOs: Output path
    val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
  })

  val a = io.ui_in(0)
  val b = io.ui_in(1)
  val launchArbiter = io.ui_in(2)
  val reqInFifo = io.ui_in(3)
  val dataInFifo = io.ui_in(4)
  val ackInFifo = io.ui_in(5)

  val cGateAO = CGate(new c_gate_ao, a, b, io.reset_n)
  val cGateMux = CGate(new c_gate_mux, a, b, io.reset_n)
  val cGateSRNorLatch = CGate(new c_gate_sr_nor_latch, a, b, io.reset_n)

  val arbtest = Module(new ArbiterTest)
  arbtest.io.reset_n := io.reset_n
  arbtest.io.req0 := a
  arbtest.io.req1 := b
  arbtest.io.launch := launchArbiter


  val mouseTrapFifo = Module(new MouseTrapFifo(UInt(1.W), 4))
  mouseTrapFifo.io.reset_n := io.reset_n
  mouseTrapFifo.io.in.req := reqInFifo
  mouseTrapFifo.io.in.data := dataInFifo
  mouseTrapFifo.io.out.ack := ackInFifo

  
  io.uo_out := Cat(
    mouseTrapFifo.io.in.ack, // 6
    mouseTrapFifo.io.out.req, // 5
    mouseTrapFifo.io.out.data, // 4
    arbtest.io.bad, // 3
    cGateSRNorLatch, // 2
    cGateMux, // 1
    cGateAO, // 0
  )
  io.uio_out := 0.U
  io.uio_oe := 0xFF.U // Set all IOs to input mode (0)
}

class Handshake[T <: Data](dt: T) extends Bundle {
  val req = Output(Bool())
  val ack = Input(Bool())
  val data = Output(dt)
}
class MouseTrap[T <: Data](dt: => T) extends Module {
  val io = IO(new Bundle {
    val reset_n = Input(Bool()) // Active low reset
    val in = Flipped(new Handshake(dt))
    val out = new Handshake(dt)
  })

  val busy = Wire(Bool())
  val reqLatch = Latch(io.in.req, busy, io.reset_n)

  busy := Delay(reqLatch ^ io.out.ack, 1)

  val dataLatch = Latch(io.in.data, busy, io.reset_n)

  io.in.ack := Delay(reqLatch, 1)
  io.out.req := Delay(reqLatch, 5)
  io.out.data := Delay(dataLatch, 1)
}

class MouseTrapFifo[T <: Data](dt: => T, depth: Int) extends Module {
  val io = IO(new Bundle {
    val reset_n = Input(Bool()) // Active low reset
    val in = Flipped(new Handshake(dt))
    val out = new Handshake(dt)
  })

  val traps = Seq.fill(depth)(Module(new MouseTrap(dt)))

  // Connect the traps in a chain
  io.out <> traps.foldLeft(io.in) { (in, trap) =>
    trap.io.reset_n := io.reset_n
    trap.io.in <> in
    trap.io.out
  }
}

class ArbiterTest extends Module {
  val io = IO(new Bundle {
    val reset_n = Input(Bool()) // Active low reset
    val req0 = Input(Bool())
    val req1 = Input(Bool())
    val launch = Input(Bool())
    val bad = Output(Bool())
  })

  val risingLaunch = io.launch && !RegNext(io.launch, 0.B)
  val req0 = RegInit(false.B)
  val req1 = RegInit(false.B)
  when (risingLaunch) {
    req0 := io.req0
    req1 := io.req1
  }
  val arb = Module(new sr_latch_arb)
  arb.io.req0 := req0
  arb.io.req1 := req1

  val badLatch = Module(new sr_latch)
  badLatch.io.rst_n := io.reset_n
  badLatch.io.r := risingLaunch
  badLatch.io.s := arb.io.grant0 && arb.io.grant1
  io.bad := badLatch.io.q
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}


object CGate {
  def apply(fac: => CGate, a: Bool, b: Bool, rst_n: Bool): Bool = {
    val gate = Module(fac)
    gate.io.rst_n := rst_n
    gate.connect(a, b)
  }
}
abstract class CGate(name: String) extends BlackBox {
  override def desiredName: String = name
  val io = IO(new Bundle {
    val rst_n = Input(Bool())
    val a = Input(Bool())
    val b = Input(Bool())
    val c = Output(Bool())
  })

  def connect(a: Bool, b: Bool): Bool = {
    io.a := a
    io.b := b
    io.c
  }
}

class c_gate_ao extends CGate("c_gate_ao")
class c_gate_mux extends CGate("c_gate_mux")
class c_gate_sr_nor_latch extends CGate("c_gate_sr_nor_latch")

class sr_latch_arb extends BlackBox {
  val io = IO(new Bundle {
    val req0 = Input(Bool())
    val req1 = Input(Bool())
    val grant0 = Output(Bool())
    val grant1 = Output(Bool())
  })
}

class sr_latch extends BlackBox {
  val io = IO(new Bundle {
    val rst_n = Input(Bool())
    val r = Input(Bool())
    val s = Input(Bool())
    val q = Output(Bool())
  })
}

object sr_latch {
  def apply(r: Bool, s: Bool, rst_n: Bool): Bool = {
    val latch = Module(new sr_latch)
    latch.io.rst_n := rst_n
    latch.io.r := r
    latch.io.s := s
    latch.io.q
  }
}