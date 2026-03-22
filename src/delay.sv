module Delay #(parameter WIDTH = 1, parameter DELAY = 1) (
  input logic [WIDTH-1:0] d,
  output logic [WIDTH-1:0] q
);
`ifdef TEST
  assign #1 q = d;
`else
  assign q = d;
`endif
endmodule