module Latch #(parameter WIDTH = 1) (
  input logic gate,
  input logic [WIDTH-1:0] d,
  output logic [WIDTH-1:0] q
);

  always_latch begin
    if (gate) begin
      q = d;
    end
  end
endmodule