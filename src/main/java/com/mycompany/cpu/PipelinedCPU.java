package com.mycompany.cpu;

import com.mycompany.data.AsmLine;

public class PipelinedCPU implements CPU {

    public int pc;

    public byte[] memory;

    public int[] registerFile = new int[32];

    public PipelinedCPUInstructionFetchStage instr_fetch = new PipelinedCPUInstructionFetchStage();

    public IF_DE if_de = new IF_DE();

    public PipelinedCPUInstructionDecodeStage instr_decode = new PipelinedCPUInstructionDecodeStage();

    public DE_EX de_ex = new DE_EX();

    public PipelinedCPUInstructionExecuteStage instr_execute = new PipelinedCPUInstructionExecuteStage();

    @Override
    public void step() {

        // IE
        instr_execute.step(this, de_ex.asm_line, de_ex);

        // ID
        AsmLine asm_line = instr_decode.step(this, if_de.instruction, de_ex);

        // IF
        int instruction = instr_fetch.step(pc, memory);

        pc += 4;

        de_ex.instruction = if_de.instruction;


        if_de.instruction = instruction;
        // if_de.asm_line = asm_line;

        de_ex.asm_line = asm_line;

    }

}
