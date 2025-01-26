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

        // TODO: check for data hazard
        if (de_ex.asm_line != null) {
            if (de_ex.asm_line.register_0 == asmLine.register_1) {

                System.out.println("Hazard with rd <-> rs1");

                // forward value
                de_ex.forwarded = true;
                de_ex.rd_value = cpu.registerFile[de_ex.asm_line.register_0.getIndex()];

            }
            if (de_ex.asm_line.register_0 == asmLine.register_2) {
                System.out.println("Hazard with rd <-> rs2");
            }
        }

        return asmLine;
    }

}
