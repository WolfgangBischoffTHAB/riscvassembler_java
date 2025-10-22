package com.mycompany.cpu;

import java.util.List;

import com.mycompany.data.AsmLine;
import com.mycompany.decoder.DelegatingDecoder;

/**
 * According to Figure 7.47 on page 440 (pdf page 462) of
 * Digital Design and Computer Architecture RISC-V Edition (Harris & Harris),
 *
 * <ul>
 * <li>the decode stage decodes the instruction and then knows which registers
 * are used.</li>
 *
 * <li>The decode stage then accesses the register file to retrieve the register
 * values.</li>
 *
 * <li>For branch instructions, the decode stage does also immediately
 * determine if a branch is taken or not.</li>
 * </ul>
 */
public class PipelinedCPUInstructionDecodeStage {

    public AsmLine<?> asmLineForDebugging;

    public DelegatingDecoder delegatingDecoder;

    public AsmLine<?> step_read(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        return step(pipelinedCPU, instruction, de_ex);
    }

    /**
     * no operation
     *
     * @param pipelinedCPU
     * @param instruction
     * @param de_ex
     */
    public void step_write(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        if (asmLineForDebugging == null) {
            return;
        }
        System.out.println("[DECOD] " + asmLineForDebugging);
    }

    public AsmLine<?> step(PipelinedCPU cpu, final int instruction, DE_EX de_ex) {
/*
        // skip
        if (instruction == 0x00) {
            asmLineForDebugging = null;
            return null;
        }

        List<AsmLine<?>> asmLines = delegatingDecoder.decode(instruction);
        asmLineForDebugging = asmLine;

        if (asmLine.mnemonic == null) {
            System.out.println(asmLine);
            throw new RuntimeException("Decoding instruction without mnemonic!");
        }

        //
        // determine branch outcome
        //

        switch (asmLine.mnemonic) {

            case I_BNE:
                // determine the registers used
                int register_0_value = cpu.registerFile[asmLine.register_0.getIndex()];
                int register_1_value = cpu.registerFile[asmLine.register_1.getIndex()];

                // evaluate
                de_ex.branchTaken = register_0_value != register_1_value;

                // DEBUG
                if (de_ex.branchTaken) {
                    System.out.println("Branch taken.");
                } else {
                    System.out.println("Branch NOT taken.");
                }
                break;

            default:
                break;
        }
 
        return asmLine;
        */
        return null;
    }

}

// // check for data hazard - data hazards are not detected. The pipeline
// automatically forwards data and the stages check if their data has been
// forwarded and retrieve it from the forwarded pipeline storage if present
        // if (de_ex.asmLine != null) {
        //     if (de_ex.asmLine.register_0 == asmLine.register_1) {
        //         System.out.println("Hazard with rd <-> rs1");
        //         // the value is already written into the forwarded_rd_value right in the EX stage
        //         // de_ex.forwarded_rd_value = cpu.registerFile[de_ex.asmLine.register_0.getIndex()];
        //     }
        //     if (de_ex.asmLine.register_0 == asmLine.register_2) {
        //         System.out.println("Hazard with rd <-> rs2");
        //         // the value is already written into the forwarded_rd_value right in the EX stage
        //         // de_ex.forwarded_rd_value = cpu.registerFile[de_ex.asmLine.register_0.getIndex()];
        //     }
        // }