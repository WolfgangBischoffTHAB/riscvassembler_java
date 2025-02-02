package com.mycompany.cpu;

import com.mycompany.data.AsmLine;

public class PipelinedCPU implements CPU {

    public int cycle_counter = 1;

    public int pc;

    public byte[] memory = new byte[256];

    public int[] registerFile = new int[32];

    public PipelinedCPUInstructionFetchStage instr_fetch = new PipelinedCPUInstructionFetchStage();

    public IF_DE if_de = new IF_DE();

    public PipelinedCPUInstructionDecodeStage instr_decode = new PipelinedCPUInstructionDecodeStage();

    public DE_EX de_ex = new DE_EX();

    public PipelinedCPUInstructionExecuteStage instr_execute = new PipelinedCPUInstructionExecuteStage();

    public EX_MEM ex_mem = new EX_MEM();

    public PipelinedCPUInstructionMemoryStage instr_memory = new PipelinedCPUInstructionMemoryStage();

    public MEM_WB mem_wb = new MEM_WB();

    public PipelinedCPUInstructionWriteBackStage instr_writeback = new PipelinedCPUInstructionWriteBackStage();

    public Interlock interlock = new Interlock();

    @Override
    public void step() {

        System.out.println("\n\nCycle: " + cycle_counter + " Instruction: " + pc / 4);
        cycle_counter++;

        // WB - write back - read
        instr_writeback.step_read(this, mem_wb);

        // MEM - memory access - read
        instr_memory.step_read(this, ex_mem, mem_wb);

        AsmLine asm_line = null;
        int instruction = 0;

        interlock.checkForStall(if_de, de_ex, ex_mem, mem_wb);

        if (interlock.isStall()) {

            // nop

        } else {

            // IE - Execute - read
            instr_execute.step_read(this, de_ex.getAsmLine(), de_ex);

            // ID - Decode - read
            asm_line = instr_decode.step_read(this, if_de.instruction, de_ex);

            // IF - Fetch - read
            instruction = instr_fetch.step_read(pc, memory);

            // if_de.instruction = instruction;

        }

        int result = 0;

        //interlock.checkForStall(if_de, de_ex, ex_mem, mem_wb);

        if (interlock.isStall()) {

            System.out.println("[FETCH] stall");

            System.out.println("[DECOD] stall");

            System.out.println("[EXEC ] stall");

        } else {

            // IF - Fetch - write
            instr_fetch.step_write(pc, memory);

            // ID - Decode - write
            instr_decode.step_write(this, if_de.instruction, de_ex);

            // IE - Execute - write
            result = instr_execute.step_write(this, if_de, de_ex, ex_mem, mem_wb);
            //System.out.println("EX result: " + result);

        }

        // MEM - memory access - write
        instr_memory.step_write(this, ex_mem, mem_wb);

        // WB - write back - write
        instr_writeback.step_write(this, mem_wb);

        mem_wb.asmLine = ex_mem.asmLine;
        mem_wb.memoryAddress = ex_mem.memoryAddress;

        if (interlock.isStall()) {

            // during stalls, PC is not allowed to advance!
            pc += 0;

            ex_mem.asmLine = null;
            ex_mem.value = 0;

        } else {

            ex_mem.asmLine = de_ex.getAsmLine();
            ex_mem.value = result;

            if (de_ex.flush) {

                de_ex.instruction = 0;
                de_ex.setAsmLine(null);
                de_ex.flush = false;

                if_de.instruction = 0;

            } else {

                de_ex.instruction = if_de.instruction;
                de_ex.setAsmLine(asm_line);

                if_de.instruction = instruction;

                // without flush and without stall the PC is incremented
                pc += 4;
            }





        }

    }

    // /**
    // * Computer Organisation And Design - RISC V edition, page 529
    // *
    // * <ul>
    // * <li>Shading on the right half of the register file or memory means the
    // * element is read in that stage,</li>
    // * <li>and shading of the left half means it is written in that stage.</li>
    // * </ul>
    // */
    // @Override
    // public void step() {

    // // IE
    // instr_execute.step(this, de_ex.asm_line, de_ex);

    // // ID
    // AsmLine asm_line = instr_decode.step(this, if_de.instruction, de_ex);

    // // IF
    // int instruction = instr_fetch.step(pc, memory);

    // pc += 4;

    // de_ex.instruction = if_de.instruction;

    // if_de.instruction = instruction;
    // // if_de.asm_line = asm_line;

    // de_ex.asm_line = asm_line;

    // }

}
