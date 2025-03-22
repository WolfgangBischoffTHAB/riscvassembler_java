package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

public class LiOptimizer extends BaseOptimizer {

    @Override
    public void modify(List<AsmLine<?>> asmLines, final Map<String, Section> sectionMap) {

        boolean done = false;
        while (!done) {

            // build label table
            Map<String, Long> map = new HashMap<>();
            buildLabelTable(asmLines, map, sectionMap);

            // // DEBUG
            // for (Map.Entry<String, Long> mapEntry : map.entrySet()) {
            //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
            // }

            updateAddresses(asmLines, sectionMap);

            // find unoptimized li pseudo instruction
            AsmLine<?> liPseudoAsmLine = null;
            int index = 0;
            boolean found = false;
            for (AsmLine<?> asmLine : asmLines) {

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
            AsmLine<?> firstAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(0);
            AsmLine<?> secondAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(1);

            System.out.println(liPseudoAsmLine);
            System.out.println(firstAsmLine);
            System.out.println(secondAsmLine);

            // if no label is used, then return
            if (firstAsmLine.offsetLabel_1 == null) {
                asmLines.remove(liPseudoAsmLine);
                liPseudoAsmLine.optimized = true;
                firstAsmLine.optimized = true;
                // asmLines.add(firstAsmLine);
                secondAsmLine.optimized = true;
                // asmLines.add(secondAsmLine);
                return;
            }

            // determine movement direction towards label (use label table for that)
            int direction = 0;
            if ((firstAsmLine.section.address + firstAsmLine.offset) > map.get(firstAsmLine.offsetLabel_1)) {
                direction = -1;
            } else {
                direction = +1;
            }

            if (direction == -1) {

                // move upwards with the goal to find the
                for (int i = index - 1; i > 0; i--) {

                    AsmLine<?> currentAsmLine = asmLines.get(i);

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null) && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
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

                    AsmLine<?> currentAsmLine = asmLines.get(i);

                    if (currentAsmLine.pseudoInstructionAsmLine == firstAsmLine.pseudoInstructionAsmLine) {
                        continue;
                    }

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null) && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
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

            // if arriving at the target label is possible only crossing real instructions (which is the positive case)
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

                AsmLine asmLine = new AsmLine();
                asmLine.mnemonic = Mnemonic.I_ADDI;
                asmLine.register_0 = secondAsmLine.register_0;
                asmLine.register_1 = RISCVRegister.REG_ZERO;
                asmLine.modifier_2 = Modifier.LO;
                asmLine.offsetLabel_2 = secondAsmLine.offsetLabel_2;
                asmLine.section = liPseudoAsmLine.section;

                firstAsmLine.pseudoInstructionAsmLine.optimized = true;
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.clear();
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.add(asmLine);
                asmLine.pseudoInstructionAsmLine = firstAsmLine.pseudoInstructionAsmLine;

                asmLines.add(index, asmLine);

            } else {
                throw new RuntimeException("Not implemented yet!");
            }

        }
    }

}
