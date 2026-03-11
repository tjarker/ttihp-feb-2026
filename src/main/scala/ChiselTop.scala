import chisel3._

/**
 * Example design in Chisel.
 * A redesign of the Tiny Tapeout example.
 */
class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    val uio_out = Output(UInt(8.W))   // IOs: Output path
    val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
  })

  val latch = Latch(io.ui_in, io.uio_in(0)) // Use the first bit of uio_in as the gate signal for the latch
  io.uo_out := latch // Connect the output of the latch to the dedicated output
  io.uio_out := 0.U
  io.uio_oe := 0xFF.U // Set all IOs to input mode (0)
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}