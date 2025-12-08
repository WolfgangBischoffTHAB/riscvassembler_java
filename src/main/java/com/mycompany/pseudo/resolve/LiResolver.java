package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * Resolve LI pseudo instruction to real instructions from the RV32 ISA.
 *
 * LI is resolved to either
 *
 * <ul>
 * <li>a single ADDI instruction</li>
 * <li>a single LUI instruction</li>
 * <li>a ADDI followed by a LUI instruction</li>
 * </ul>
 *
 * depending on which option describes the effect of the LI most optimally
 * with the least amount of statements.
 */
public class LiResolver implements AsmInstructionListModifier<RISCVRegister> {

    // public Map<String, Long> labelAddressMap;

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        boolean done = false;
        while (!done) {

            boolean found = false;

            AsmLine<RISCVRegister> liPseudoAsmLine = null;

            int index = 0;

            // find next LI instruction
            //
            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_LI) {
                    index++;
                    continue;
                }

                found = true;
                liPseudoAsmLine = asmLine;

                break;
            }

            if (found) {

                if (liPseudoAsmLine.numeric_1 == null) {

                    // Long value = labelAddressMap.get(liPseudoAsmLine.identifier_1);
                    // if (value == null) {
                    // throw new RuntimeException("null");
                    // }
                    // liPseudoAsmLine.numeric_1 = value;

                    throw new RuntimeException(
                            "Invalid use of li pseudo instruction! Used without an immediate value! "
                                    + liPseudoAsmLine.toString());
                }

                long value = liPseudoAsmLine.numeric_1;

                // TODO: THIS IS WRONG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                strictSegmentation(asmLines, liPseudoAsmLine, index, value);

                // as the LiResolver chooses an optimal set of statements to implement the LI
                // statement, the li instruction is flagged as optimized
                liPseudoAsmLine.optimized = true;

                continue;
            }

            done = true;

        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void strictSegmentation(List<AsmLine<RISCVRegister>> asmLines, AsmLine<RISCVRegister> liPseudoAsmLine,
            int index, long value) {

        long upper_part = (value >> 12) & 0xFFFFF;
        long lower_part = (value >> 0) & 0xFFF;

        boolean upper_part_used = upper_part != 0;
        boolean lower_part_used = lower_part != 0;

        if (!upper_part_used && !lower_part_used) {

            asmLines.remove(liPseudoAsmLine);

            // Case 0: addi to fill entire 32 bit register with zero

            //
            // addi
            //

            AsmLine addi = new AsmLine();
            asmLines.add(index, addi);

            // connect parent and child
            liPseudoAsmLine.pseudoInstructionChildren.add(addi);
            addi.pseudoInstructionAsmLine = liPseudoAsmLine;

            addi.section = liPseudoAsmLine.section;
            index++;

            addi.mnemonic = Mnemonic.I_ADDI;
            addi.register_0 = liPseudoAsmLine.register_0;
            addi.register_1 = RISCVRegister.REG_ZERO;
            addi.numeric_2 = 0L;

            if (liPseudoAsmLine.label != null) {
                addi.label = liPseudoAsmLine.label;
            }

        } else if (!upper_part_used && lower_part_used) {

            // Case 1: CONSTANT fits into 12 lower bits.
            // For CASE 1, a single addi instruction is generated since addi handles 12 bit
            // sufficiently

            //
            // addi
            //

            liPseudoAsmLine.optimized = true;

            liPseudoAsmLine.mnemonic = Mnemonic.I_ADDI;
            liPseudoAsmLine.register_1 = RISCVRegister.REG_ZERO;

            liPseudoAsmLine.numeric_2 = liPseudoAsmLine.numeric_1;
            liPseudoAsmLine.numeric_1 = null;

        } else if (upper_part_used && !lower_part_used) {

            asmLines.remove(liPseudoAsmLine);

            // Case 2: CONSTANT fits into the 20 upper bits.

            //
            // lui - LUI (load upper immediate) is used to build 32-bit constants and uses
            // the U-type format. LUI
            // places the U-immediate value in the top 20 bits of the destination register
            // rd, filling in the lowest
            // 12 bits with zeros.
            //

            //
            // lui
            //

            AsmLine lui = new AsmLine();
            asmLines.add(index, lui);

            // connect parent and child
            liPseudoAsmLine.pseudoInstructionChildren.add(lui);
            lui.pseudoInstructionAsmLine = liPseudoAsmLine;

            lui.section = liPseudoAsmLine.section;
            index++;

            lui.mnemonic = Mnemonic.I_LUI;
            lui.register_0 = RISCVRegister.REG_GP;
            lui.numeric_1 = upper_part;

            if (liPseudoAsmLine.label != null) {
                lui.label = liPseudoAsmLine.label;
            }

        } else {

            asmLines.remove(liPseudoAsmLine);

            // CASE 3 For CASE 3, a LUI, ADDI combination is generated so
            // that the upper 20 bits and the lower 12 bits are used

            // the 20 bit part is incremented by 1, (then shifted left by 12 bits to get
            // (data_2))

            long twelve_bit_sign_extended = NumberParseUtil.sign_extend_12_bit_to_int32_t(value);
            long udata = value - twelve_bit_sign_extended;
            udata = udata >> 12;

            //
            // lui - LUI (load upper immediate) is used to build 32-bit constants and uses
            // the U-type format. LUI places the U-immediate value in the top 20 bits of the
            // destination register rd, filling in the lowest 12 bits with zeros.
            //

            //
            // lui
            //

            RISCVRegister tempRegister = liPseudoAsmLine.register_0;

            // since ADDI automatically performs sign extension, there is no
            // need for a LUI instruction that performs sign extension manually
            // by filling the upper part of the register with FFFFF. The LUI
            // can be optimized away

            // if ((twelve_bit_sign_extended > 0) || (Math.abs(twelve_bit_sign_extended) >=
            // 2048)) {

            AsmLine lui = new AsmLine();
            asmLines.add(index, lui);

            // connect parent and child
            liPseudoAsmLine.pseudoInstructionChildren.add(lui);
            lui.pseudoInstructionAsmLine = liPseudoAsmLine;

            lui.section = liPseudoAsmLine.section;
            index++;

            lui.mnemonic = Mnemonic.I_LUI;
            lui.register_0 = tempRegister;
            lui.numeric_1 = udata;

            if (liPseudoAsmLine.label != null) {
                lui.label = liPseudoAsmLine.label;
            }

            // } else {

            // tempRegister = RISCVRegister.REG_ZERO;

            // }

            //
            // addi
            //

            AsmLine addi = new AsmLine();
            asmLines.add(index, addi);

            // connect parent and child
            liPseudoAsmLine.pseudoInstructionChildren.add(addi);
            addi.pseudoInstructionAsmLine = liPseudoAsmLine;

            addi.section = liPseudoAsmLine.section;
            index++;

            addi.mnemonic = Mnemonic.I_ADDI;
            addi.register_0 = liPseudoAsmLine.register_0;
            addi.register_1 = tempRegister;

            addi.numeric_2 = NumberParseUtil.sign_extend_12_bit_to_int32_t(lower_part);

        }
    }

}
