package com.mycompany.cpu;

public class PipelinedCPUInstructionWriteBackStage {

    private int val;

    public void step_write(final PipelinedCPU cpu, final MEM_WB mem_wb) {

        if (mem_wb.asmLine == null) {
            return;
        }

        // ALU add, sub operation
        cpu.registerFile[mem_wb.asmLine.register_0.getIndex()] = val;

        System.out.println("[WBACK] " + mem_wb.asmLine.register_0 + " <= " + val);

        mem_wb.asmLine = null;
        //mem_wb.value = 0;
    }

    public void step_read(PipelinedCPU pipelinedCPU, MEM_WB mem_wb) {
        if (mem_wb.asmLine == null) {
            return;
        }

        val = mem_wb.rd_value;
    }

}
