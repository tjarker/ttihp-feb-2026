`timescale 1ns/10ps
module Latch #(parameter WIDTH = 1) (
  input logic rst_n,
  input logic hold,
  input logic [WIDTH-1:0] d,
  output logic [WIDTH-1:0] q
);

  always_latch begin
    if (!rst_n) begin
      q = '0;
    end else if (!hold) begin
       q = d;
    end
  end
endmodule

