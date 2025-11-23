package com.mycompany.pseudo.combine;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

/**
 * One possible optimization is to recombine a combination of lui and addi
 * (which were manually added by a developer) into a LI pseudo instruction.
 * The assembler can then resolve LI into a potentially optimized smaller
 * amount of instructions.
 *
 * For example:
 * 
 * <pre>
 * li -> addi if upper_part_used == 0 && lower_part_used == 0
 * li -> addi if (upper_part_used == 0 && lower_part_used != 0)
 * if -> lui if (upper_part_used != 0 && lower_part_used == 0)
 * </pre>
 *
 * Effect of %hi(), %lo():
 * https://sourceware.org/binutils/docs/as/RISC_002dV_002dModifiers.html
 * %lo(symbol) - The low 12 bits of absolute address for symbol.
 * %hi(symbol) - The high 20 bits of absolute address for symbol.
 *
 * Code that can be optimized looks like this:
 * 
 * <pre>
 * lui a0, %hi(.L.str)
 * addi a0, a0, %lo(.L.str)
 * </pre>
 *
 * This is basically the same as a LI pseudo instruction
 * If the assembler could detect a LI here, it can then potentially optimize
 * the LI to a simple addi or lui
 *
 * To detect this pattern
 * 1. the same register R is used for lui.reg_rd, addi.reg_rd and addi.reg_rs1
 * 2. the same symbol is used in %hi(symbol) and %lo(symbol)
 *
 * The optimized output is:
 * 
 * <pre>
 * li a0, .L.str
 * </pre>
 *
 * A second example is this sequence:
 * 
 * <pre>
 * lui x15, 2441
 * addi x15, x15, 1662
 * </pre>
 *
 * In this example, there are no labels.
 */
public class LiCombiner<T extends Register> implements AsmInstructionListModifier<T> {

    @Override
    public void modify(List<AsmLine<T>> asmLines, final Map<String, Section> sectionMap) {

        AsmLine<T> data_1 = null;
        AsmLine<T> data_2 = null;

        for (AsmLine<T> asmLine : asmLines) {

            data_2 = asmLine;

            if (data_1 == null) {
                data_1 = data_2;
                data_2 = null;
                continue;
            }

            if ((data_1.mnemonic == Mnemonic.I_LUI) && (data_2.mnemonic == Mnemonic.I_ADDI)) {

                boolean noLabels = (data_1.offsetLabel_1 == null) && (data_2.offsetLabel_2 == null);
                boolean labelsMatch = false;
                if ((data_1.offsetLabel_1 != null) && (data_2.offsetLabel_2 != null)) {
                    labelsMatch = data_1.offsetLabel_1.equalsIgnoreCase(data_2.offsetLabel_2);
                }

                // offset label must match
                if (noLabels || labelsMatch) {

                    // registers must match
                    if ((data_1.register_0 == data_2.register_0) && (data_2.register_0 == data_2.register_1)) {

                        // attach pseudo instruction
                        AsmLine<T> pseudoInstructionAsmLine = new AsmLine<>();
                        pseudoInstructionAsmLine.section = data_1.section;
                        pseudoInstructionAsmLine.optimized = false;
                        pseudoInstructionAsmLine.mnemonic = Mnemonic.I_LI;
                        pseudoInstructionAsmLine.register_0 = data_2.register_0;
                        pseudoInstructionAsmLine.identifier_1 = data_1.offsetLabel_1;

                        // attach parent and child
                        data_1.pseudoInstructionAsmLine = pseudoInstructionAsmLine;
                        pseudoInstructionAsmLine.pseudoInstructionChildren.add(data_1);

                        // attach parent and child
                        data_2.pseudoInstructionAsmLine = pseudoInstructionAsmLine;
                        pseudoInstructionAsmLine.pseudoInstructionChildren.add(data_2);

                        pseudoInstructionAsmLine.sourceLine = data_1.sourceLine;
                    }
                }
            }

            data_1 = data_2;
        }
    }
}
