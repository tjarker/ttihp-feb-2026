`timescale 1ns/10ps
module c_gate_ao (
  input logic rst_n,
  input logic a,
  input logic b,
  output logic c
);

  logic a_or_b;
  logic a_and_b;

  assign a_or_b = a | b;
  assign a_and_b = a & b;

  logic feedback;
  `ifdef TEST
  assign #1 feedback = c;
  `else
  assign feedback = c;
  `endif

  sg13g2_a21o_1 AO (.A1(a_or_b && rst_n), .A2(feedback), .B1(a_and_b && rst_n), .X(c));
endmodule



`timescale 1ns/10ps
module c_gate_mux (
  input logic rst_n,
  input logic a,
  input logic b,
  output logic c
);

logic a_xor_b;
assign a_xor_b = a ^ b;
logic feedback;
`ifdef TEST
assign #1 feedback = c;
`else
assign feedback = c;
`endif
sg13g2_mux2_1 MUX (.A0(a && rst_n), .A1(feedback), .S(a_xor_b && rst_n), .X(c));

endmodule



`timescale 1ns/10ps
module c_gate_sr_nor_latch (
  input logic rst_n,
  input logic a,
  input logic b,
  output logic c
);

logic q, q_bar;

logic a_and_b;
assign a_and_b = a & b;
logic a_nor_b;
assign a_nor_b = ~(a | b);
logic R,S;
assign R = a_nor_b || ~rst_n;
assign S = a_and_b && rst_n;

assign c = q;
logic feedback_q, feedback_q_bar;
`ifdef TEST
assign #1 feedback_q = q;
assign #1 feedback_q_bar = q_bar;
`else
assign feedback_q = q;
assign feedback_q_bar = q_bar;
`endif

sg13g2_nor2_1 NOR1 (.A(R), .B(feedback_q_bar), .Y(q));
sg13g2_nor2_1 NOR2 (.A(S), .B(feedback_q), .Y(q_bar));


endmodule


`timescale 1ns/10ps
module sr_latch_arb (
  input logic req0,
  input logic req1,
  output logic grant0,
  output logic grant1
);

logic grant0_feedback, grant1_feedback;
`ifdef TEST
assign #1 grant0_feedback = unstable_grant0;
assign #5 grant1_feedback = unstable_grant1;
`else
assign grant0_feedback = unstable_grant0;
assign grant1_feedback = unstable_grant1;
`endif

logic unstable_grant0, unstable_grant1;

sg13g2_nand2_1 NAND_Q (.A(~req1), .B(grant1_feedback), .Y(unstable_grant0));
sg13g2_nand2_1 NAND_Q_BAR (.A(~req0), .B(grant0_feedback), .Y(unstable_grant1));

sg13g2_nor4_1 FILTER_GRANT0 (.A(unstable_grant0), .B(unstable_grant0), .C(unstable_grant0), .D(unstable_grant0), .Y(grant0));
sg13g2_nor4_1 FILTER_GRANT1 (.A(unstable_grant1), .B(unstable_grant1), .C(unstable_grant1), .D(unstable_grant1), .Y(grant1));

endmodule

`timescale 1ns/10ps
module sr_latch (
  input logic rst_n,
  input logic s,
  input logic r,
  output logic q
);

logic q_bar;
logic q_feedback, q_bar_feedback;
`ifdef TEST
assign #1 q_feedback = q;
assign #5 q_bar_feedback = q_bar;
`else
assign q_feedback = q;
assign q_bar_feedback = q_bar;
`endif

sg13g2_nand2_1 NAND_Q (.A(~r), .B(q_bar_feedback), .Y(q));
sg13g2_nand2_1 NAND_Q_BAR (.A(~s), .B(q_feedback), .Y(q_bar));

endmodule