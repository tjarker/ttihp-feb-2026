# SPDX-FileCopyrightText: © 2024 Tiny Tapeout
# SPDX-License-Identifier: Apache-2.0

import cocotb
from cocotb.clock import Clock
from cocotb.triggers import ClockCycles
from cocotb.triggers import Timer
from cocotb.triggers import Edge

async def arbtx(dut, req0, req1):
    dut.ui_in.value = (req0 & 1) | ((req1 & 1) << 1) | (1 << 2)
    await ClockCycles(dut.clk, 2)
    dut.ui_in.value = (req0 & 1) | ((req1 & 1) << 1) | (0 << 2)
    await ClockCycles(dut.clk, 2)

@cocotb.test()
async def test_project(dut):

    def setPin(i, v):
        dut.ui_in.value = (dut.ui_in.value.to_unsigned() & ~(1 << i)) | ((v & 1) << i)

    async def awaitPin(i, v):
        while True:
            await dut.ui_in.value_change
            if dut.ui_in.value[i] == v:
                break

    dut._log.info("Start")

    # Set the clock period to 10 us (100 KHz)
    clock = Clock(dut.clk, 10, unit="us")
    cocotb.start_soon(clock.start())

    # Reset
    dut._log.info("Reset")
    dut.ena.value = 1
    dut.ui_in.value = 0
    dut.uio_in.value = 0
    dut.rst_n.value = 0
    await ClockCycles(dut.clk, 10)
    dut.rst_n.value = 1

    await arbtx(dut, 1, 0)
    await arbtx(dut, 0, 0)

    dut._log.info("Test project behavior")

    # c element tests
    #assert (int(dut.uo_out.value) & 0x7) == 0
    await ClockCycles(dut.clk, 2)
    dut.ui_in.value = 1
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 0
    dut.ui_in.value = 3
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 7
    dut.ui_in.value = 2
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 7
    dut.ui_in.value = 0
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 0
    await ClockCycles(dut.clk, 2)
    dut.ui_in.value = 2
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 0
    dut.ui_in.value = 3
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 7
    dut.ui_in.value = 1
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 7
    dut.ui_in.value = 0
    await ClockCycles(dut.clk, 2)
    #assert (int(dut.uo_out.value) & 0x7) == 0

    


    #arbiter test
    await arbtx(dut, 0, 0)
    await arbtx(dut, 0, 1)
    await arbtx(dut, 1, 0)
    await arbtx(dut, 1, 1)
    await arbtx(dut, 0, 0)

    await ClockCycles(dut.clk, 10)


    setPin(3, 1)
    await Timer(100, unit="ns")
    setPin(5, 1)
    await Timer(100, unit="ns")
    setPin(4,1)
    setPin(3,0)
    await Timer(100, unit="ns")
    setPin(5,0)
    await Timer(100, unit="ns")
    

    # The following assersion is just an example of how to check the output values.
    # Change it to match the actual expected output of your module:
    ##assert dut.uo_out.value == 50

    # Keep testing the module by changing the input values, waiting for
    # one or more clock cycles, and #asserting the expected output values.
