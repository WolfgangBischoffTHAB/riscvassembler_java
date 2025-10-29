package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

/**
 * Converts (resolve) the pseudo instruction la (load address) to real or
 * non-pseudo instructions that can be executed on a RISC-V CPU.<br />
 * <br />
 * 
 * la produces pc-relative addresses (as opposed to absolute addressing) because
 * it is resolved to an auipc call. auipc is pc-relative by specification.<br />
 * <br />
 * 
 * The pseudo instruction la (load address) is defined in the RISC-V Assembler
 * Programmer's Manual on page 41.<br />
 * <br />
 * 
 * Chapter 29. A listing of standard RISC-V pseudoinstructions<br />
 * <br />
 * 
 * The resolution of la to non-pseudo instructions is affected by the .option
 * nopic or .option pic. This resolver currently works as if option .nopic is
 * used.
 * <br />
 * <br />
 * 
 * This means the resolution will be:
 * ```
 * la rd, symbol
 * auipc rd, symbol[31:12]
 * addi rd, rd, symbol[11:0]
 * ```
 * <br />
 * <br />
 * https://stackoverflow.com/questions/52574537/risc-v-pc-absolute-vs-pc-relative
 * <br />
 * <br />
 * auipc auipc - (Add Upper Immediate to Program Counter): this sets rd to the
 * sum of the current PC and a 32-bit value with the low 12 bits as 0 and the
 * high 20 bits coming from the U-type immediate.
 */
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

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            if (found) {

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

                // C:\Users\wolfg\dev\Java\riscvassembler_java\src\test\resources\riscvasm\la.s

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
                // addi.numeric_2 = sign_extend_12_bit_to_int32_t(lower_part);

                addi.offsetLabel_2 = foundAsmLine.identifier_1;
                addi.modifier_2 = Modifier.LO;

                // // DEBUG
                // System.out.println("\n\n\n");
                // for (AsmLine asmLine : asmLines) {
                // System.out.println(asmLine);
                // }

                continue;

            }

            done = true;

        }
    }

}
