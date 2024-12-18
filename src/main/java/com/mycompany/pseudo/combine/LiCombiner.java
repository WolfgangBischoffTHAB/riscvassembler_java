package com.mycompany.pseudo.combine;

import java.util.List;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;

/**
 * One possible optimization is to recombine a combination of lui and addi
 * (which were manually added by a developer) into a li pseudo instruction.
 * The assembler can then resolve li into a potentially optimized smaller
 * amount of instructions.
 *
 * For example:
 * li -> addi if upper_part_used == 0 && lower_part_used == 0
 * li -> addi if (upper_part_used == 0 && lower_part_used != 0)
 * if -> lui if (upper_part_used != 0 && lower_part_used == 0)
 *
 * Effect of %hi(), %lo():
 * https://sourceware.org/binutils/docs/as/RISC_002dV_002dModifiers.html
 * %lo(symbol) - The low 12 bits of absolute address for symbol.
 * %hi(symbol) - The high 20 bits of absolute address for symbol.
 *
 * Code that can be optimized looks like this:
 *
 * lui a0, %hi(.L.str)
 * addi a0, a0, %lo(.L.str)
 *
 * This is basically the same as a li pseudo instruction
 * If the assembler could detect a li here, it can then potentially optimize
 * the li to a simple addi or lui
 *
 * To detect this pattern
 * 1. the same register R is used for lui.reg_rd, addi.reg_rd and addi.reg_rs1
 * 2. the same symbol is used in %hi(symbol) and %lo(symbol)
 *
 * The optimized output is:
 * li a0, .L.str
 */
public class LiCombiner implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        AsmLine data_1 = null;
        AsmLine data_2 = null;

        for (AsmLine asmLine : asmLines) {

            data_2 = asmLine;

            if (data_1 == null) {
                data_1 = data_2;
                data_2 = null;
                continue;
            }

            if ((data_1.mnemonic == Mnemonic.I_LUI) && (data_2.mnemonic == Mnemonic.I_ADDI)) {

                // offset label must match
                if (data_1.offsetLabel_1.equalsIgnoreCase(data_2.offsetLabel_2)) {

                    // registers must match
                    if ((data_1.register_0 == data_2.register_0) && (data_2.register_0 == data_2.register_1)) {

                        // attach pseudo instruction
                        AsmLine pseudoInstructionAsmLine = new AsmLine();
                        pseudoInstructionAsmLine.section = data_1.section;
                        pseudoInstructionAsmLine.optimized = false;
                        pseudoInstructionAsmLine.mnemonic = Mnemonic.I_LI;
                        pseudoInstructionAsmLine.register_0 = data_2.register_0;
                        pseudoInstructionAsmLine.identifier_1 = data_1.offsetLabel_1;
                        data_1.pseudoInstructionAsmLine = pseudoInstructionAsmLine;
                        pseudoInstructionAsmLine.pseudoInstructionChildren.add(data_1);
                        data_2.pseudoInstructionAsmLine = pseudoInstructionAsmLine;
                        pseudoInstructionAsmLine.pseudoInstructionChildren.add(data_2);
                    }

                }

            }

            data_1 = data_2;

        }
    }

}
