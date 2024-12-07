package com.mycompany.pseudo.resolve;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;

public class LiResolver implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        boolean done = false;
        while (!done) {

            boolean found = false;
            AsmLine foundAsmLine = null;

            int index = 0;

            // to prevent concurrent modification exception, separate search
            // from modification
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic != Mnemonic.I_LI) {
                    index++;
                    continue;
                }

                //System.out.println(asmLine);

                found = true;
                foundAsmLine = asmLine;

                break;
            }

            if (found) {

                //
                // li
                //

                asmLines.remove(foundAsmLine);

                foundAsmLine.optimized = false;

                long value = foundAsmLine.numeric_1;

                long upper_part = (value >> 12) & 0xFFFFF;
                long lower_part = (value >> 0) & 0xFFF;


                // boolean upper_part_used = upper_part != 0;
                // boolean lower_part_used = lower_part != 0;


                // if (!upper_part_used && !lower_part_used) {

                //     throw new RuntimeException();

                //     // // Case 0: addi to fill entire 32bit register with zero

                //     // //
                //     // // addi
                //     // //

                //     // uint8_t rd = encode_register(data->reg_rd);
                //     // uint8_t rs1 = encode_register(data->reg_rd);
                //     // uint32_t imm = 0x00;

                //     // asm_line_t addi;
                //     // reset_asm_line(&addi);
                //     // addi.used = 1;
                //     // addi.line_nr = line_nr + 1;
                //     // addi.instruction = I_ADDI;
                //     // addi.instruction_type = IT_I;
                //     // addi.instruction_index = data->instruction_index + 1;
                //     // addi.reg_rd = data->reg_rd;
                //     // addi.reg_rs1 = R_ZERO;
                //     // addi.imm = imm;

                //     // copy_asm_line(data, &addi);

                // } else if (!upper_part_used && lower_part_used) {

                //     //throw new RuntimeException();

                //     // // Case 1: CONSTANT fits into 12 lower bits.
                //     // // For CASE 1, a addi instruction is generated since addi handles 12 bit sufficiently

                //     // data->instruction = I_ADDI;
                //     // data->reg_rs1 = R_ZERO;

                //     foundAsmLine.mnemonic = Mnemonic.I_ADDI;
                //     foundAsmLine.register_1 = Register.REG_ZERO;

                //     foundAsmLine.pseudoInstructionAsmLine.optimized = true;

                // } else if (upper_part_used && !lower_part_used) {

                //     throw new RuntimeException();

                //     // // Case 2: CONSTANT fits into the 20 upper bits.

                //     // //
                //     // // lui - LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format. LUI
                //     // // places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest
                //     // // 12 bits with zeros.
                //     // //

                //     // uint8_t rd = encode_register(data->reg_rd);
                //     // uint32_t imm = upper_data;

                //     // asm_line_t lui;
                //     // reset_asm_line(&lui);
                //     // lui.used = 1;
                //     // lui.line_nr = line_nr;
                //     // lui.instruction = I_LUI;
                //     // lui.instruction_type = IT_U;
                //     // lui.instruction_index = data->instruction_index;
                //     // lui.reg_rd = data->reg_rd;
                //     // lui.imm = imm;

                //     // copy_asm_line(data, &lui);

                // } else {

                //     throw new RuntimeException();

                //     // // CASE 3 For CASE 3, a LUI, ADDI combination is generated so
                //     // // that the upper 20 bits and the lower 20 bits are used

                //     // // the 20 bit part is incremented by 1, (then shifted left by 12 bits to get (data_2))
                //     // //data_1 = data_1 + 1;

                //     // int32_t twelve_bit_sign_extended = sign_extend_12_bit_to_int32_t(data_0);
                //     // int udata = data_0 - twelve_bit_sign_extended;
                //     // udata = udata >> 12;

                //     // // // if the lower 12 bits have a 1 in the most signifiant bit, output an optimized sequence
                //     // // if ((lower_data & 0x08) > 0) {

                //     // // }

                //     // //
                //     // // lui - LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format. LUI
                //     // // places the U-immediate value in the top 20 bits of the destination register rd, filling in the lowest
                //     // // 12 bits with zeros.
                //     // //

                //     // uint8_t rd = encode_register(data->reg_rd);
                //     // uint32_t imm = udata;

                //     // asm_line_t lui;
                //     // reset_asm_line(&lui);
                //     // lui.used = 1;
                //     // lui.line_nr = line_nr;
                //     // lui.instruction = I_LUI;
                //     // lui.instruction_type = IT_U;
                //     // lui.instruction_index = data->instruction_index;
                //     // lui.reg_rd = data->reg_rd;
                //     // lui.imm = imm;

                //     // copy_asm_line(data, &lui);

                //     // //
                //     // // addi
                //     // //

                //     // rd = encode_register(data->reg_rd);
                //     // uint8_t rs1 = encode_register(data->reg_rd);
                //     // imm = lower_data;

                //     // asm_line_t addiw;
                //     // reset_asm_line(&addiw);
                //     // addiw.used = 1;
                //     // addiw.line_nr = line_nr + 1;
                //     // addiw.instruction = I_ADDIW;
                //     // addiw.instruction_type = IT_I;
                //     // addiw.instruction_index = data->instruction_index + 1;
                //     // addiw.reg_rd = data->reg_rd;
                //     // addiw.reg_rs1 = data->reg_rd;
                //     // addiw.imm = imm;

                //     // reset_asm_line(data);

                //     // for (int i = size-1; i > index; i--) {
                //     //     //printf("index: %d\n", (i > index));
                //     //     //printf("copy %d <- %d\n", i+1, i);
                //     //     copy_asm_line(&asm_line_array[i + 1], &asm_line_array[i]);
                //     //     asm_line_array[i + 1].line_nr++;

                //     //     if (asm_line_array[i + 1].instruction_index != -1) {
                //     //         asm_line_array[i + 1].instruction_index++;
                //     //     }
                //     // }

                //     // copy_asm_line(data, &lui);
                //     // copy_asm_line(&asm_line_array[index + 1], &addiw);

                // }






                //
                // lui
                //

                AsmLine lui = new AsmLine();
                asmLines.add(index, lui);
                foundAsmLine.pseudoInstructionChildren.add(lui);
                lui.pseudoInstructionAsmLine = foundAsmLine;
                index++;

                lui.mnemonic = Mnemonic.I_LUI;
                lui.register_0 = Register.REG_A5;
                lui.numeric_1 = upper_part;

                if (foundAsmLine.label != null) {
                    lui.label = foundAsmLine.label;
                }

                //
                // addi
                //

                AsmLine addi = new AsmLine();
                asmLines.add(index, addi);
                foundAsmLine.pseudoInstructionChildren.add(addi);
                addi.pseudoInstructionAsmLine = foundAsmLine;
                index++;

                addi.mnemonic = Mnemonic.I_ADDI;
                addi.register_0 = foundAsmLine.register_0;
                addi.register_1 = Register.REG_A5;
                addi.numeric_2 = lower_part;

                continue;
            }

            done = true;

        }

    }

}
