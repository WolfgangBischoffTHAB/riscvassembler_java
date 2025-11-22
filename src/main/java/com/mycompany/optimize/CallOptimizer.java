package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

/**
 * First, the CallResolver has added two non-pseudo asm lines as children
 * into the pseudo assembler asmLine. Now the job of the CallOptimizer is
 * it to apply both of the child assembler lines or apply any optimizations
 * if possible.
 * 
 * If possible turns far calls (auipc+jalr) to near calls (jal)
 *
 * A far call goes beyond +/-1Mb relative to the PC.
 * A near call remains within +/-1MB relative to the PC.
 */
public class CallOptimizer<T extends Register> extends BaseOptimizer<T> {

    @SuppressWarnings("rawtypes")
    @Override
    public void modify(final List<AsmLine<T>> asmLines, final Map<String, Section> sectionMap) {

        boolean done = false;
        while (!done) {

            // update addresses to make sure that the addresses are still correct should the
            // last cycle of optimization have removed instructions
            updateAddresses(asmLines, sectionMap);

            // build label table
            Map<String, Long> labelTableMap = new HashMap<>();
            Map<Long, AsmLine<T>> offsetAsmLineMap = new HashMap<>();
            buildLabelTable(asmLines, labelTableMap, offsetAsmLineMap, sectionMap);

            // // DEBUG output label table
            // for (Map.Entry<String, Long> mapEntry : labelTableMap.entrySet()) {
            // System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
            // }

            AsmLine<?> callPseudoAsmLine = null;
            int index = 0;
            boolean found = false;

            // find call (pseudo) instruction
            for (AsmLine<?> asmLine : asmLines) {

                if ((asmLine.pseudoInstructionAsmLine != null)
                        && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_CALL)
                        && (asmLine.pseudoInstructionAsmLine.optimized == false)) {

                    // found a call instruction
                    callPseudoAsmLine = asmLine.pseudoInstructionAsmLine;
                    found = true;

                    // DEBUG
                    System.out.println(callPseudoAsmLine);

                    // DEBUG
                    if (callPseudoAsmLine.identifier_0.equalsIgnoreCase("upCountingMatrix")) {
                        System.out.println(callPseudoAsmLine);

                        System.out.println("Address of target label: " + labelTableMap.get(callPseudoAsmLine.identifier_0));
                    }

                    break;
                }
                index++;
            }

            // if, after looking at all assembler lines, no call instruction has been found
            // abort this CallOptimizer
            if (!found) {
                done = true;
                continue;
            }

            //
            // call-pseudo instruction is resolved to aupic + jalr
            //

            // start with first child instruction -- aupic instruction
            AsmLine<?> firstAsmLine = callPseudoAsmLine.pseudoInstructionChildren.get(0);
            String offsetLabel = firstAsmLine.offsetLabel_1;

            if (!labelTableMap.containsKey(offsetLabel)) {
                throw new RuntimeException("The function/Label \"" + offsetLabel + "\" is not defined!");
            }

            // determine movement direction towards label (use label table for that)
            int direction = 0;
            if ((firstAsmLine.section.address + firstAsmLine.getOffset()) > labelTableMap.get(offsetLabel)) {
                direction = -1;
            } else {
                direction = +1;
            }

            if (direction == -1) {

                // move upwards
                for (int i = index - 1; i > 0; i--) {

                    AsmLine<?> currentAsmLine = asmLines.get(i);

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null)
                            && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        currentAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    if (offsetLabel.equalsIgnoreCase(currentAsmLine.label)) {
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
                    if ((currentAsmLine.pseudoInstructionAsmLine != null)
                            && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        firstAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    if (offsetLabel.equalsIgnoreCase(currentAsmLine.label)) {
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
            // System.out.println("absolute address of label: " +
            // map.get(firstAsmLine.offsetLabel_1));

            // if arriving at the target label is possible only crossing real instructions
            // take the absolute value of the label and put it into the modifier.

            long address = labelTableMap.get(offsetLabel);
            long highValue = 0;
            @SuppressWarnings("unused")
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

            // look at the second pseudo instruction -- jalr instruction
            AsmLine<?> secondAsmLine = callPseudoAsmLine.pseudoInstructionChildren.get(1);
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

            // if the HI modifier returns 0, the instruction can be optimized.
            // A short jump is used
            if (highValue == 0) {

                // JAL only works with half word aligned values.
                // If the offset is not half word aligned, generate a JALR
                // instruction which jumps to any value we like

                boolean twoByteAligned = true;
                long delta = (firstAsmLine.section.address + firstAsmLine.getOffset())
                        - labelTableMap.get(firstAsmLine.offsetLabel_1);
                twoByteAligned = (delta % 2) == 0;

                asmLines.remove(firstAsmLine);
                asmLines.remove(secondAsmLine);

                AsmLine asmLine = new AsmLine();
                asmLine.section = firstAsmLine.section;

                if (twoByteAligned) {

                    // JAL is a PC relative jump
                    // rd <- pc + 4; pc <- pc + imm20

                    asmLine.mnemonic = Mnemonic.I_JAL;
                    asmLine.register_0 = RISCVRegister.REG_RA;

                    // int offset = lowValue <= secondAsmLine.offset ? 4 : 0;
                    // int offset = 4;
                    //asmLine.numeric_1 = lowValue - secondAsmLine.offset + offset; // +4 or +0

                    //long off = labelTableMap.get(asmLine.identifier_0);
                    long off = labelTableMap.get(secondAsmLine.offsetLabel_2);
                    AsmLine referencedTarget = offsetAsmLineMap.get(off);
                    asmLine.referencedTarget = referencedTarget;

                    // DEBUG
                    // System.out.println(asmLine);

                    callPseudoAsmLine.optimized = true;
                    callPseudoAsmLine.pseudoInstructionChildren.clear();
                    callPseudoAsmLine.pseudoInstructionChildren.add(asmLine);
                    asmLine.pseudoInstructionAsmLine = callPseudoAsmLine;

                } else {

                    // JALR because relative offset would not be half word aligned

                    asmLine.mnemonic = Mnemonic.I_JALR;
                    asmLine.register_0 = RISCVRegister.REG_RA;
                    asmLine.offset_1 = labelTableMap.get(firstAsmLine.offsetLabel_1);
                    asmLine.register_1 = RISCVRegister.REG_ZERO;

                    callPseudoAsmLine.optimized = true;
                    callPseudoAsmLine.pseudoInstructionChildren.clear();
                    callPseudoAsmLine.pseudoInstructionChildren.add(asmLine);
                    asmLine.pseudoInstructionAsmLine = callPseudoAsmLine;

                }

                asmLines.add(index, asmLine);

            } else {

                firstAsmLine.modifier_1 = null;
                firstAsmLine.offsetLabel_1 = null;
                firstAsmLine.numeric_1 = highValue;

                secondAsmLine.modifier_2 = null;
                secondAsmLine.offsetLabel_2 = null;
                secondAsmLine.numeric_2 = highValue;

            }
        }
    }

}
