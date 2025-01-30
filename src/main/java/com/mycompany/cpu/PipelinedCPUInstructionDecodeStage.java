package com.mycompany.cpu;

import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class PipelinedCPUInstructionDecodeStage {

    private AsmLine asmLineForDebugging;

    public AsmLine step_read(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        return step(pipelinedCPU, instruction, de_ex);
    }

    public void step_write(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        // nop

        if (asmLineForDebugging == null) {
            return;
        }
        System.out.println("[DECOD] " + asmLineForDebugging);
    }

    public AsmLine step(PipelinedCPU cpu, final int instruction, DE_EX de_ex) {

        // skip
        if (instruction == 0x00) {
            asmLineForDebugging = null;
            return null;
        }

        AsmLine asmLine = Decoder.decode(instruction);
        asmLineForDebugging = asmLine;

        // System.out.println("[DECOD] " + asmLine);

        if (asmLine.mnemonic == null) {
            System.out.println(asmLine);
            throw new RuntimeException("Decoding instruction without mnemonic!");
        }

        // // check for data hazard
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

        return asmLine;
    }

}
