package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.Register;

public class LiOptimizer extends BaseOptimizer {

    @Override
    public void modify(List<AsmLine> asmLines) {

        boolean done = false;
        while (!done) {

            // build label table
            Map<String, Long> map = new HashMap<>();
            buildLabelTable(asmLines, map);

            // // DEBUG
            // for (Map.Entry<String, Long> mapEntry : map.entrySet()) {
            //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
            // }

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

            //liPseudoAsmLine.optimized = true;
/**/
            // start with first child instruction
            AsmLine firstAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(0);
            AsmLine secondAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(1);



            // determine movement direction towards label (use label table for that)
            int direction = 0;
            if (firstAsmLine.address > map.get(firstAsmLine.offsetLabel_1)) {
                direction = -1;
            } else {
                direction = +1;
            }

            if (direction == -1) {

                // move upwards
                for (int i = index - 1; i > 0; i--) {

                    AsmLine currentAsmLine = asmLines.get(i);

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null) && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        //throw new RuntimeException("Cannot optimize!");
                        //currentAsmLine.pseudoInstructionAsmLine.optimized = true;
                        currentAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    if (firstAsmLine.offsetLabel_1.equalsIgnoreCase(currentAsmLine.label)) {
                        break;
                    }
                }
            }

            if (direction == +1) {

                // move downwards
                for (int i = index + 1; i < asmLines.size(); i++) {

                    AsmLine currentAsmLine = asmLines.get(i);

                    if (currentAsmLine.pseudoInstructionAsmLine == firstAsmLine.pseudoInstructionAsmLine) {
                        continue;
                    }

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null) && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        //throw new RuntimeException("Cannot optimize!");
                        //firstAsmLine.pseudoInstructionAsmLine.optimized = true;
                        firstAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    if (firstAsmLine.offsetLabel_1.equalsIgnoreCase(currentAsmLine.label)) {
                        break;
                    }
                }
            }


            if (firstAsmLine.pseudoInstructionAsmLine.optimized == true) {
                continue;
            }

            // // DEBUG
            // System.out.println("first: " + firstAsmLine);
            // System.out.println("Second: " + secondAsmLine);
            // System.out.println("Label: " + firstAsmLine.offsetLabel_1);
            // System.out.println("absolute address of label: " + map.get(firstAsmLine.offsetLabel_1));

            // if arriving at the target label is possible only crossing real instructions
            // take the absolute value of the label and put it into the modifier.

            long address = map.get(firstAsmLine.offsetLabel_1);
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

            // if the modifier returns 0, the instruction can be optimized
            if (highValue == 0) {

                asmLines.remove(firstAsmLine);
                asmLines.remove(secondAsmLine);

                // byte rd = (byte) asmLine.register_0.ordinal();
                // byte rs1 = (byte) asmLine.register_1.ordinal();
                // short imm = asmLine.numeric_2.shortValue();

                AsmLine asmLine = new AsmLine();
                asmLine.mnemonic = Mnemonic.I_ADDI;
                asmLine.register_0 = secondAsmLine.register_0;
                asmLine.register_1 = Register.REG_ZERO;
                asmLine.modifier_2 = Modifier.LO;
                asmLine.offsetLabel_2 = secondAsmLine.offsetLabel_2;

                firstAsmLine.pseudoInstructionAsmLine.optimized = true;
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.clear();
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.add(asmLine);
                asmLine.pseudoInstructionAsmLine = firstAsmLine.pseudoInstructionAsmLine;

                asmLines.add(index, asmLine);

            } else {
                throw new RuntimeException("Not implemented yet!");
            }
            // else {

            //     firstAsmLine.modifier_1 = null;
            //     firstAsmLine.offsetLabel_1 = null;
            //     firstAsmLine.numeric_1 = highValue;

            //     // System.out.println("firstAsmLine: " + firstAsmLine);

            //     secondAsmLine.modifier_2 = null;
            //     secondAsmLine.offsetLabel_2 = null;
            //     secondAsmLine.numeric_2 = highValue;

            //     // System.out.println("secondAsmLine: " + secondAsmLine);

            // }

            //throw new RuntimeException("Apply the same algorithm as the call optimizer!");

            /*
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
