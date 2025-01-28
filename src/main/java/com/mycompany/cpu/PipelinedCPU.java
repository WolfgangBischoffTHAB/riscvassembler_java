package com.mycompany.cpu;

import com.mycompany.data.AsmLine;

public class PipelinedCPU implements CPU {

    public int cycle_counter;

    public int pc;

    public byte[] memory;

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

    @Override
    public void step() {

        System.out.println("\n\nCycle: " + cycle_counter);
        cycle_counter++;

        // IF - Fetch - write
        instr_fetch.step_write(pc, memory);

        // ID - Decode - write
        instr_decode.step_write(this, if_de.instruction, de_ex);

        // IE - Execute - write
        int result = instr_execute.step_write(this, de_ex, ex_mem);
        System.out.println("EX result: " + result);

        // MEM - memory access - write

        // WB - write back - write
        instr_writeback.step_write(this, mem_wb);






        // IF - Fetch - read
        int instruction = instr_fetch.step_read(pc, memory);

        // ID - Decode - read
        AsmLine asm_line = instr_decode.step_read(this, if_de.instruction, de_ex);

        // IE - Execute - read
        instr_execute.step_read(this, de_ex.asmLine, de_ex);




        pc += 4;

        mem_wb.asmLine = ex_mem.asmLine;
        mem_wb.value = ex_mem.value;

        ex_mem.asmLine = de_ex.asmLine;
        ex_mem.value = result;

        de_ex.instruction = if_de.instruction;
        de_ex.asmLine = asm_line;

        if_de.instruction = instruction;

    }




    // /**
    //  * Computer Organisation And Design - RISC V edition, page 529
    //  *
    //  * <ul>
    //  * <li>Shading on the right half of the register file or memory means the
    //  * element is read in that stage,</li>
    //  * <li>and shading of the left half means it is written in that stage.</li>
    //  * </ul>
    //  */
    // @Override
    // public void step() {

    //     // IE
    //     instr_execute.step(this, de_ex.asm_line, de_ex);

    //     // ID
    //     AsmLine asm_line = instr_decode.step(this, if_de.instruction, de_ex);

    //     // IF
    //     int instruction = instr_fetch.step(pc, memory);

    //     pc += 4;

    //     de_ex.instruction = if_de.instruction;

    //     if_de.instruction = instruction;
    //     // if_de.asm_line = asm_line;

    //     de_ex.asm_line = asm_line;

    // }

}
