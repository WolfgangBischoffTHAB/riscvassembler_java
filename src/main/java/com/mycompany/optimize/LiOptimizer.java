package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;

public class LiOptimizer extends BaseOptimizer {

    @Override
    public void modify(List<AsmLine> asmLines) {

        boolean done = false;
        while (!done) {

            // build label table
            Map<String, Long> map = new HashMap<>();
            buildLabelTable(asmLines, map);

            updateAddresses(asmLines);

            // find unoptimized li pseudo instruction
            AsmLine liPseudoAsmLine = null;
            int index = 0;
            boolean found = false;
            for (AsmLine asmLine : asmLines) {

                if (asmLine.mnemonic == Mnemonic.I_LI) {
                    System.out.println("test");
                }

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

            liPseudoAsmLine.optimized = true;

/*
            // start with first child instruction
            AsmLine firstAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(0);
            AsmLine secondAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(1);

            if ((firstAsmLine.numeric_1 == 0) && (secondAsmLine.numeric_2 == 0)) {
                throw new RuntimeException();
            }
            if (firstAsmLine.numeric_1 == 0) {
                System.out.println("");
                liPseudoAsmLine.pseudoInstructionChildren.remove(0);
                liPseudoAsmLine.optimized = true;
                asmLines.remove(firstAsmLine);
                firstAsmLine.pseudoInstructionAsmLine = null;
            }
            if (secondAsmLine.numeric_2 == 0) {
                System.out.println("");
                liPseudoAsmLine.pseudoInstructionChildren.remove(1);
                liPseudoAsmLine.optimized = true;
                asmLines.remove(secondAsmLine);
                secondAsmLine.pseudoInstructionAsmLine = null;
            }
            if ((firstAsmLine.numeric_1 != 0) && (secondAsmLine.numeric_2 != 0)) {
                liPseudoAsmLine.optimized = true;
            }
 */
        }
    }

}
