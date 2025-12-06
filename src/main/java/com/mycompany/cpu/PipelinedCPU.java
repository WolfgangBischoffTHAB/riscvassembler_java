package com.mycompany.cpu;

import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;

public class PipelinedCPU extends AbstractCPU {

    public int cycle_counter = 1;

    public int pc;

    public int executePC;

    public byte[] memory = new byte[256];

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

    public int[] registerFile = new int[32];

    /**
     * Computer Organisation And Design - RISC V edition, page 529
     *
     * <ul>
     * <li>Shading on the right half of the register file or memory means the
     * element is read in that stage,</li>
     * <li>and shading of the left half means it is written in that stage.</li>
     * </ul>
     */
    @Override
    public boolean step() {

        System.out.println("\n\nCycle: " + cycle_counter + " Instruction: " + pc / 4);
        cycle_counter++;

        // WB - write back - read
        instr_writeback.step_read(this, mem_wb);

        // MEM - memory access - read
        instr_memory.step_read(this, ex_mem, mem_wb);

        AsmLine<?> asm_line = null;
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

        // this is the result value computed by the execute stage
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

        return true;
    }

    public int readRegisterFile(int index) {

        // register zero is hardcoded zero
        if (index == 0) {
            return 0;
        }

        // set the value
        return registerFile[index];
    }

    public void writeRegisterFile(final int register_index, final int value) {

        // write to zero register has no effect
        if (register_index == RISCVRegister.REG_ZERO.getIndex()) {
            return;
        }

        // set the value
        registerFile[register_index] = value;
    }

    @Override
    public long[] getRegisterFile() {
        long[] result = new long[32];
        for (int i = 0; i < 31; i++) {
            result[i] = (long) registerFile[i];
        }
        return result;
    }

    @Override
    public void printRegisterFile() {
        long[] registerFile = getRegisterFile();
        for (int i = 0; i < 32; i++) {
            System.out.println("x" + (i) + ": " + registerFile[i]);
        }
    }

}
