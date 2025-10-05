package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

public class LaResolver<T extends Register> implements AsmInstructionListModifier<T> {

    @Override
    public void modify(List<AsmLine<T>> asmLines, final Map<String, Section> sectionMap) {

        boolean done = false;
        while (!done) {

            boolean found = false;
            AsmLine<T> foundAsmLine = null;

            int index = 0;

            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine<T> asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_LA) {
                    index++;
                    continue;
                }

                //System.out.println(asmLine);

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            if (found) {

                // int data_0 = 0;

                // // split it into a 20 bit (upper_data) and a lower twelve bit part (is ignored)
                // int upper_data = ((data_0 & 0b11111111111111111111000000000000) >> 12);
                // //printf("upper_data: %08" PRIx32 "\n", upper_data);

                // int lower_data = data_0 & 0xFFF;
                // //printf("lower_data: %08" PRIx32 "\n", lower_data);

                // int upper_part_used = upper_data;
                // int lower_part_used = lower_data;

                asmLines.remove(foundAsmLine);

                //
                // auipc
                //

                foundAsmLine.optimized = true;

                AsmLine<T> auipc = new AsmLine<>();
                asmLines.add(index, auipc);
                foundAsmLine.pseudoInstructionChildren.add(auipc);
                auipc.pseudoInstructionAsmLine = foundAsmLine;
                auipc.section = foundAsmLine.section;
                index++;

                auipc.mnemonic = Mnemonic.I_AUIPC;
                auipc.register_0 = foundAsmLine.register_0;

                // TODO: resolve the label 'foundAsmLine.identifier_1' to its absolute address
                // then perform the computations outlined in

                //C:\Users\wolfg\dev\Java\riscvassembler_java\src\test\resources\riscvasm\la.s

                auipc.offsetLabel_1 = foundAsmLine.identifier_1;
                auipc.modifier_1 = Modifier.HI;

                if (foundAsmLine.label != null) {
                    auipc.label = foundAsmLine.label;
                }

                //
                // addi
                //

                AsmLine<T> addi = new AsmLine<>();
                asmLines.add(index, addi);
                foundAsmLine.pseudoInstructionChildren.add(addi);
                addi.pseudoInstructionAsmLine = foundAsmLine;
                addi.section = foundAsmLine.section;
                index++;

                addi.mnemonic = Mnemonic.I_ADDI;
                addi.register_0 = foundAsmLine.register_0;
                addi.register_1 = foundAsmLine.register_0;
                //addi.numeric_2 = sign_extend_12_bit_to_int32_t(lower_part);

                addi.offsetLabel_2 = foundAsmLine.identifier_1;
                addi.modifier_2 = Modifier.LO;

                // // DEBUG
                // System.out.println("\n\n\n");
                // for (AsmLine asmLine : asmLines) {
                //     System.out.println(asmLine);
                // }

                continue;

            }

            done = true;

        }
    }

}
