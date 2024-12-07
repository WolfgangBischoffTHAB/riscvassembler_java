package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class LiOptimizer extends BaseOptimizer {

    @Override
    public void modify(List<AsmLine> asmLines) {

        boolean done = false;
        while (!done) {

            // build label table
            Map<String, Integer> map = new HashMap<>();
            buildLabelTable(asmLines, map);

            updateAddresses(asmLines);

            // find unoptimized li pseudo instruction
            AsmLine liPseudoAsmLine = null;
            int index = 0;
            boolean found = false;
            for (AsmLine asmLine : asmLines) {
                if ((asmLine.pseudoInstructionAsmLine != null)
                        && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LI)
                        && (asmLine.pseudoInstructionAsmLine.optimized == false)) {
                            liPseudoAsmLine = asmLine.pseudoInstructionAsmLine;
                    found = true;
                    break;
                }
                index++;
            }

            if (!found) {
                done = true;
                continue;
            }

            // start with first child instruction
            AsmLine firstAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(0);
            AsmLine secondAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(1);

            // determine movement direction towards label (use label table for that)
            int direction = 0;
            if (firstAsmLine.address > map.get(firstAsmLine.offsetLabel_1)) {
                direction = -1;
            } else {
                direction = +1;
                throw new RuntimeException();
            }

            if (direction == -1) {

                // move upwards
                for (int i = index - 1; i > 0; i--) {

                    AsmLine currentAsmLine = asmLines.get(i);

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if (currentAsmLine.pseudoInstructionAsmLine != null) {
                        throw new RuntimeException("Cannot optimize!");
                    }

                    if (firstAsmLine.offsetLabel_1.equalsIgnoreCase(currentAsmLine.label)) {
                        break;
                    }
                }
            }

            // // DEBUG
            // System.out.println("first: " + firstAsmLine);
            // System.out.println("Second: " + secondAsmLine);
            // System.out.println("Label: " + firstAsmLine.offsetLabel_1);
            // System.out.println("absolute address of label: " + map.get(firstAsmLine.offsetLabel_1));

            // if arriving at the target label is possible only crossing real instructions
            // take the absolute value of the label and put it into the modifier.

            int address = map.get(firstAsmLine.offsetLabel_1);
            long highValue = 0;
            long lowValue = 0;

            switch (firstAsmLine.modifier_1) {

                case HI:
                    highValue = address >> 12 & 0xFFFFF;
                    break;

                case LO:
                    lowValue = address & 0xFFF;
                    break;

                default:
                    throw new RuntimeException();
            }

            switch (secondAsmLine.modifier_2) {

                case HI:
                    highValue = address >> 12 & 0xFFFFF;
                    break;

                case LO:
                    lowValue = address & 0xFFF;
                    break;

                default:
                    throw new RuntimeException();
            }

            // System.out.println("highValue: " + highValue);
            // System.out.println("lowValue: " + lowValue);

            // if the absolute label address is exactly 12-bit throw exception!

            if (highValue == 0 && lowValue == 0) {

                // Case 0: addi to fill entire 32bit register with zero

                throw new RuntimeException();

                //
                // addi
                //

                // uint8_t rd = encode_register(data->reg_rd);
                // uint8_t rs1 = encode_register(data->reg_rd);
                // uint32_t imm = 0x00;

                // asm_line_t addi;
                // reset_asm_line(&addi);
                // addi.used = 1;
                // addi.line_nr = line_nr + 1;
                // addi.instruction = I_ADDI;
                // addi.instruction_type = IT_I;
                // addi.instruction_index = data->instruction_index + 1;
                // addi.reg_rd = data->reg_rd;
                // addi.reg_rs1 = R_ZERO;
                // addi.imm = imm;

                // copy_asm_line(data, &addi);

            } else if (highValue == 0 && lowValue != 0) {

                // Case 1: CONSTANT fits into 12 lower bits.
                // For CASE 1, a addi instruction is generated since addi handles 12 bit sufficiently

                // data->instruction = I_ADDI;
                // data->reg_rs1 = R_ZERO;

                asmLines.remove(firstAsmLine);
                asmLines.remove(secondAsmLine);

                AsmLine asmLine = new AsmLine();
                asmLine.mnemonic = Mnemonic.I_ADDI;
                asmLine.register_0 = Register.REG_RA;
                asmLine.register_1 = Register.REG_ZERO;
                asmLine.numeric_2 = lowValue;

                asmLines.add(index, asmLine);

                liPseudoAsmLine.optimized = true;
                liPseudoAsmLine.pseudoInstructionChildren.clear();
                liPseudoAsmLine.pseudoInstructionChildren.add(asmLine);
                asmLine.pseudoInstructionAsmLine = liPseudoAsmLine;

            } else if (highValue != 0 && lowValue == 0) {

                // Case 2: CONSTANT fits into the 20 upper bits.

                throw new RuntimeException();

                //
                // lui - LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format. LUI
                // places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest
                // 12 bits with zeros.
                //

                // uint8_t rd = encode_register(data->reg_rd);
                // uint32_t imm = upper_data;

                // asm_line_t lui;
                // reset_asm_line(&lui);
                // lui.used = 1;
                // lui.line_nr = line_nr;
                // lui.instruction = I_LUI;
                // lui.instruction_type = IT_U;
                // lui.instruction_index = data->instruction_index;
                // lui.reg_rd = data->reg_rd;
                // lui.imm = imm;

                // copy_asm_line(data, &lui);

            } else {

                throw new RuntimeException();

                // CASE 3 For CASE 3, a LUI, ADDI combination is generated so
                // that the upper 20 bits and the lower 20 bits are used

                // the 20 bit part is incremented by 1, (then shifted left by 12 bits to get (data_2))
                //data_1 = data_1 + 1;

                // int32_t twelve_bit_sign_extended = sign_extend_12_bit_to_int32_t(data_0);
                // int udata = data_0 - twelve_bit_sign_extended;
                // udata = udata >> 12;

                // //
                // // lui - LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format. LUI
                // // places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest
                // // 12 bits with zeros.
                // //

                // uint8_t rd = encode_register(data->reg_rd);
                // uint32_t imm = udata;

                // asm_line_t lui;
                // reset_asm_line(&lui);
                // lui.used = 1;
                // lui.line_nr = line_nr;
                // lui.instruction = I_LUI;
                // lui.instruction_type = IT_U;
                // lui.instruction_index = data->instruction_index;
                // lui.reg_rd = data->reg_rd;
                // lui.imm = imm;

                // copy_asm_line(data, &lui);

                // //
                // // addi
                // //

                // rd = encode_register(data->reg_rd);
                // uint8_t rs1 = encode_register(data->reg_rd);
                // imm = lower_data;

                // asm_line_t addiw;
                // reset_asm_line(&addiw);
                // addiw.used = 1;
                // addiw.line_nr = line_nr + 1;
                // addiw.instruction = I_ADDIW;
                // addiw.instruction_type = IT_I;
                // addiw.instruction_index = data->instruction_index + 1;
                // addiw.reg_rd = data->reg_rd;
                // addiw.reg_rs1 = data->reg_rd;
                // addiw.imm = imm;

                // reset_asm_line(data);

                // for (int i = size-1; i > index; i--) {
                //     //printf("index: %d\n", (i > index));
                //     //printf("copy %d <- %d\n", i+1, i);
                //     copy_asm_line(&asm_line_array[i + 1], &asm_line_array[i]);
                //     asm_line_array[i + 1].line_nr++;

                //     if (asm_line_array[i + 1].instruction_index != -1) {
                //         asm_line_array[i + 1].instruction_index++;
                //     }
                // }

                // copy_asm_line(data, &lui);
                // copy_asm_line(&asm_line_array[index + 1], &addiw);

            }

            // // if the modifier returns 0, the instruction can be optimized
            // if (highValue == 0) {

            //     asmLines.remove(firstAsmLine);
            //     asmLines.remove(secondAsmLine);

            //     AsmLine asmLine = new AsmLine();
            //     asmLine.mnemonic = Mnemonic.I_JAL;
            //     asmLine.register_0 = Register.REG_RA;
            //     asmLine.numeric_1 = lowValue;

            //     asmLines.add(index, asmLine);

            //     liPseudoAsmLine.optimized = true;
            //     liPseudoAsmLine.pseudoInstructionChildren.clear();
            //     liPseudoAsmLine.pseudoInstructionChildren.add(asmLine);
            //     asmLine.pseudoInstructionAsmLine = liPseudoAsmLine;

            // } else {

            //     firstAsmLine.modifier_1 = null;
            //     firstAsmLine.offsetLabel_1 = null;
            //     firstAsmLine.numeric_1 = highValue;

            //     System.out.println("firstAsmLine: " + firstAsmLine);

            //     secondAsmLine.modifier_2 = null;
            //     secondAsmLine.offsetLabel_2 = null;
            //     secondAsmLine.numeric_2 = highValue;

            //     System.out.println("secondAsmLine: " + secondAsmLine);

            // }
        }
    }

}
