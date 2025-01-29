package com.mycompany.cpu;

import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class PipelinedCPUInstructionDecodeStage {

    public AsmLine step(PipelinedCPU cpu, final int instruction, DE_EX de_ex) {

        // skip
        if (instruction == 0x00) {
            return null;
        }

        AsmLine asmLine = Decoder.decode(instruction);

        System.out.println(asmLine);

        if (asmLine.mnemonic == null) {
            System.out.println(asmLine);
            throw new RuntimeException("Decoding instruction without mnemonic!");
        }

        // check for data hazard
        if (de_ex.asmLine != null) {
            if (de_ex.asmLine.register_0 == asmLine.register_1) {

                System.out.println("Hazard with rd <-> rs1");

                // forward value
                //de_ex.forwarded = true;

                // the value is already written into the forwarded_rd_value right in the EX stage
                // de_ex.forwarded_rd_value = cpu.registerFile[de_ex.asmLine.register_0.getIndex()];

            }
            if (de_ex.asmLine.register_0 == asmLine.register_2) {
                System.out.println("Hazard with rd <-> rs2");
            }
        }

        return asmLine;
    }

    public void step_write(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        // nop
    }

    public AsmLine step_read(PipelinedCPU pipelinedCPU, int instruction, DE_EX de_ex) {
        return step(pipelinedCPU, instruction, de_ex);
    }

}
