package com.mycompany.pseudo.resolve;

import java.util.List;
import java.util.Map;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

public class LiResolver implements AsmInstructionListModifier<RISCVRegister> {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        boolean done = false;
        while (!done) {

            boolean found = false;

            AsmLine<RISCVRegister> foundAsmLine = null;

            int index = 0;

            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_LI) {
                    index++;
                    continue;
                }

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            if (found) {

                //
                // li
                //

                foundAsmLine.optimized = false;

                long value = foundAsmLine.numeric_1;

                long upper_part = (value >> 12) & 0xFFFFF;
                long lower_part = (value >> 0) & 0xFFF;

                boolean upper_part_used = upper_part != 0;
                boolean lower_part_used = lower_part != 0;

                if (!upper_part_used && !lower_part_used) {

                    asmLines.remove(foundAsmLine);

                    // Case 0: addi to fill entire 32 bit register with zero

                    //
                    // addi
                    //

                    foundAsmLine.optimized = true;

                    AsmLine addi = new AsmLine();
                    asmLines.add(index, addi);
                    foundAsmLine.pseudoInstructionChildren.add(addi);
                    addi.pseudoInstructionAsmLine = foundAsmLine;
                    addi.section = foundAsmLine.section;
                    index++;

                    addi.mnemonic = Mnemonic.I_ADDI;
                    addi.register_0 = foundAsmLine.register_0;
                    addi.register_1 = RISCVRegister.REG_ZERO;
                    addi.numeric_2 = 0L;

                    if (foundAsmLine.label != null) {
                        addi.label = foundAsmLine.label;
                    }

                } else if (!upper_part_used && lower_part_used) {

                    // Case 1: CONSTANT fits into 12 lower bits.
                    // For CASE 1, a addi instruction is generated since addi handles 12 bit
                    // sufficiently

                    // data->instruction = I_ADDI;
                    // data->reg_rs1 = R_ZERO;

                    foundAsmLine.optimized = true;

                    foundAsmLine.mnemonic = Mnemonic.I_ADDI;
                    foundAsmLine.register_1 = RISCVRegister.REG_ZERO;

                    foundAsmLine.numeric_2 = foundAsmLine.numeric_1;
                    foundAsmLine.numeric_1 = null;

                } else if (upper_part_used && !lower_part_used) {

                    asmLines.remove(foundAsmLine);

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

                    foundAsmLine.optimized = true;
                    foundAsmLine.pseudoInstructionChildren.add(lui);
                    lui.pseudoInstructionAsmLine = foundAsmLine;
                    lui.section = foundAsmLine.section;
                    index++;

                    lui.mnemonic = Mnemonic.I_LUI;
                    lui.register_0 = RISCVRegister.REG_GP;
                    lui.numeric_1 = upper_part;

                    if (foundAsmLine.label != null) {
                        lui.label = foundAsmLine.label;
                    }

                } else {

                    asmLines.remove(foundAsmLine);

                    foundAsmLine.optimized = true;

                    // CASE 3 For CASE 3, a LUI, ADDI combination is generated so
                    // that the upper 20 bits and the lower 20 bits are used

                    // the 20 bit part is incremented by 1, (then shifted left by 12 bits to get
                    // (data_2))
                    // data_1 = data_1 + 1;

                    long twelve_bit_sign_extended = NumberParseUtil.sign_extend_12_bit_to_int32_t(value);
                    long udata = value - twelve_bit_sign_extended;
                    udata = udata >> 12;

                    //
                    // lui - LUI (load upper immediate) is used to build 32-bit constants and uses
                    // the U-type format. LUI
                    // places the U-immediate value in the top 20 bits of the destination register
                    // rd, filling in the lowest
                    // 12 bits with zeros.
                    //

                    foundAsmLine.optimized = true;

                    //
                    // lui
                    //

                    RISCVRegister tempRegister = foundAsmLine.register_0;

                    // since ADDI automatically performs sign extension, there is no
                    // need for a LUI instruction that performs sign extension manually
                    // by filling the upper part of the register with FFFFF. The LUI
                    // can be optimized away

                    // if ((twelve_bit_sign_extended > 0) || (Math.abs(twelve_bit_sign_extended) <=
                    // 2048)) { // works for blinker.s

                    // if (twelve_bit_sign_extended > 0) { // works for memory.s
                    if ((twelve_bit_sign_extended > 0) || (Math.abs(twelve_bit_sign_extended) >= 2048)) {

                        AsmLine lui = new AsmLine();
                        asmLines.add(index, lui);
                        foundAsmLine.pseudoInstructionChildren.add(lui);
                        lui.pseudoInstructionAsmLine = foundAsmLine;
                        lui.section = foundAsmLine.section;
                        index++;

                        lui.mnemonic = Mnemonic.I_LUI;

                        lui.register_0 = tempRegister;

                        lui.numeric_1 = udata;

                        if (foundAsmLine.label != null) {
                            lui.label = foundAsmLine.label;
                        }

                    } else {

                        tempRegister = RISCVRegister.REG_ZERO;

                    }

                    //
                    // addi
                    //

                    AsmLine addi = new AsmLine();
                    asmLines.add(index, addi);
                    foundAsmLine.pseudoInstructionChildren.add(addi);
                    addi.pseudoInstructionAsmLine = foundAsmLine;
                    addi.section = foundAsmLine.section;
                    index++;

                    addi.mnemonic = Mnemonic.I_ADDI;
                    addi.register_0 = foundAsmLine.register_0;

                    addi.register_1 = tempRegister;

                    addi.numeric_2 = NumberParseUtil.sign_extend_12_bit_to_int32_t(lower_part);

                }

                continue;
            }

            done = true;

        }

    }

}
