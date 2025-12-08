package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Modifier;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

/**
 * A pair of lui + addi instructions has been combined into a LI pseudo
 * instructions by the LiCombiner in a prior step. The idea is that sometimes
 * ADDI and LI are not needed at the same time but only one of them implements
 * the LI statement correclty. Therefore, taking a step back to LI and then
 * optimizing LI is a way to save on instructions. since there is potential for
 * the assembler to optimize the pair of two instructions into a single
 * instruction.
 *
 * This class checks for the optimal way to implement the LI pseudo instruction.
 */
public class LiOptimizer<T extends Register> extends BaseOptimizer<T> {

    private static final Logger logger = LoggerFactory.getLogger(LiOptimizer.class);


    @SuppressWarnings("unchecked")
    @Override
    public void modify(List<AsmLine<T>> asmLines, final Map<String, Section> sectionMap) {

        int optimizationCounter = 0;

        boolean done = false;
        while (!done) {

            // build label table
            Map<String, Long> map = new HashMap<>();
            Map<Long, AsmLine<T>> offsetAsmLineMap = new HashMap<>();
            buildLabelTable(asmLines, map, offsetAsmLineMap, sectionMap);

            // // DEBUG
            // for (Map.Entry<String, Long> mapEntry : map.entrySet()) {
            //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
            // }

            updateAddresses(asmLines, sectionMap);

            // find unoptimized li pseudo instruction
            AsmLine<T> liPseudoAsmLine = null;
            int index = 0;
            boolean found = false;
            for (AsmLine<T> asmLine : asmLines) {

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
            AsmLine<T> firstAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(0);
            AsmLine<T> secondAsmLine = liPseudoAsmLine.pseudoInstructionChildren.get(1);

            // // DEBUG
            // System.out.println(liPseudoAsmLine);
            // System.out.println(firstAsmLine);
            // System.out.println(secondAsmLine);

            // look for offset label in all possible spots
            String offsetLabel = retrieveLabel(firstAsmLine);

            // if no label is used, then continue
            if (offsetLabel == null) {

                asmLines.remove(liPseudoAsmLine);
                liPseudoAsmLine.optimized = true;
                firstAsmLine.optimized = true;
                secondAsmLine.optimized = true;

                continue;
            }

            // determine movement direction towards label (use label table for that)
            int direction = 0;
            if ((firstAsmLine.section.address + firstAsmLine.getOffset()) > map.get(offsetLabel)) {
                direction = -1;
            } else {
                direction = +1;
            }

            if (direction == -1) {

                // move upwards with the goal to find the label
                for (int i = index - 1; i > 0; i--) {

                    @SuppressWarnings("rawtypes")
                    AsmLine currentAsmLine = asmLines.get(i);

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null)
                            && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        currentAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    String currentLineOffsetLabel = retrieveLabel(currentAsmLine);

                    if (firstAsmLine.offsetLabel_1.equalsIgnoreCase(currentLineOffsetLabel)) {
                        break;
                    }
                }
            }

            if (direction == +1) {

                // move downwards with the goal to find the label
                for (int i = index + 1; i < asmLines.size(); i++) {

                    @SuppressWarnings("rawtypes")
                    AsmLine currentAsmLine = asmLines.get(i);

                    if (currentAsmLine.pseudoInstructionAsmLine == firstAsmLine.pseudoInstructionAsmLine) {
                        continue;
                    }

                    // for each instruction, check if it is a real instruction (not pseudo)
                    if ((currentAsmLine.pseudoInstructionAsmLine != null)
                            && (!currentAsmLine.pseudoInstructionAsmLine.optimized)) {
                        firstAsmLine.pseudoInstructionAsmLine.optimized = false;
                    }

                    String currentLineOffsetLabel = retrieveLabel(currentAsmLine);

                    if (offsetLabel.equalsIgnoreCase(currentLineOffsetLabel)) {
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
            // (which is the positive case)
            // take the absolute value of the label and put it into the modifier.

            long address = map.get(offsetLabel);

            System.out.println(offsetLabel + " = " + address);

            if (address < 0) {
                throw new RuntimeException("A label can never resolved to a negative absolute address!");
            }

            // lowerLI(asmLines, liPseudoAsmLine, index, firstAsmLine, secondAsmLine,
            // address);

            if (address < 2048) {

                optimizationCounter++;

                asmLines.remove(firstAsmLine);
                asmLines.remove(secondAsmLine);

                AsmLine<T> asmLine = new AsmLine<>();
                asmLine.mnemonic = Mnemonic.I_ADDI;
                asmLine.register_0 = (T) secondAsmLine.register_0;
                asmLine.register_1 = (T) RISCVRegister.REG_ZERO;
                asmLine.numeric_2 = address; // - (optimizationCounter * 8);
                asmLine.modifier_2 = null;
                asmLine.offsetLabel_2 = null;
                asmLine.section = liPseudoAsmLine.section;

                // copy source line for displaying the line in the editor / debugger
                asmLine.sourceLine = firstAsmLine.sourceLine;

                firstAsmLine.pseudoInstructionAsmLine.optimized = true;
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.clear();
                firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.add(asmLine);
                asmLine.pseudoInstructionAsmLine = firstAsmLine.pseudoInstructionAsmLine;

                asmLines.add(index, asmLine);

            } else {

                int next = nextPowerOf2((int) address);
                System.out.println(next);

                int diff = (int)address - next;
                System.out.println(diff);

                next = next >> 12;

                // Assumption: FirstLine: lui a1, %hi(num1)
                if (firstAsmLine.modifier_1 != null) {
                    firstAsmLine.modifier_1 = null;
                    firstAsmLine.numeric_1 = (long) next;
                    firstAsmLine.identifier_1 = null;
                }
                firstAsmLine.optimized = true;

                // Assumption: SecondLine: addi a1, a1, %lo(num1)
                if (secondAsmLine.modifier_2 != null) {
                    secondAsmLine.modifier_2 = null;
                    secondAsmLine.numeric_2 = (long) diff;
                    secondAsmLine.identifier_2 = null;
                }
                secondAsmLine.optimized = true;

                firstAsmLine.pseudoInstructionAsmLine.optimized = true;

            }
        }
    }

    /**
     * How it works: n-- handles inputs that are already powers of 2
     * (e.g., 8 becomes 7, which then gets processed to 15, then +1 is 16).
     *
     * The OR-shifts (|=) propagate the highest set bit to all lower positions,
     * creating 0...0111...1. Adding 1 flips all those 1s to 0s and sets the next bit,
     * resulting in the next power of 2 (e.g., 01000 for 8).
     *
     * @param n
     * @return
     */
    int nextPowerOf2(int n) {
        if (n == 0)
            return 1; // Or handle as error
        n--; // Handle case where n is already a power of 2
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16; // For 32-bit integers
        n++;
        return n;
    }

    @SuppressWarnings("unchecked")
    private void lowerLI(List<AsmLine<T>> asmLines, AsmLine<T> liPseudoAsmLine, int index, AsmLine<T> firstAsmLine,
            AsmLine<T> secondAsmLine, long address) {

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

            AsmLine<T> asmLine = new AsmLine<>();
            asmLine.mnemonic = Mnemonic.I_ADDI;
            asmLine.register_0 = (T) secondAsmLine.register_0;
            asmLine.register_1 = (T) RISCVRegister.REG_ZERO;
            asmLine.modifier_2 = Modifier.LO;
            asmLine.offsetLabel_2 = secondAsmLine.offsetLabel_2;
            asmLine.section = liPseudoAsmLine.section;

            // copy source line for displaying the line in the editor / debugger
            asmLine.sourceLine = firstAsmLine.sourceLine;

            firstAsmLine.pseudoInstructionAsmLine.optimized = true;
            firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.clear();
            firstAsmLine.pseudoInstructionAsmLine.pseudoInstructionChildren.add(asmLine);
            asmLine.pseudoInstructionAsmLine = firstAsmLine.pseudoInstructionAsmLine;

            asmLines.add(index, asmLine);

        } else {

            // logger.info("Keeping original statements");

            // Assumption: FirstLine: lui a1, %hi(num1)
            if (firstAsmLine.modifier_1 != null) {
                firstAsmLine.modifier_1 = null;
                firstAsmLine.numeric_1 = highValue;
            }
            firstAsmLine.optimized = true;

            // Assumption: SecondLine: addi a1, a1, %lo(num1)
            if (secondAsmLine.modifier_2 != null) {
                secondAsmLine.modifier_2 = null;
                secondAsmLine.numeric_2 = lowValue;
            }
            secondAsmLine.optimized = true;

            firstAsmLine.pseudoInstructionAsmLine.optimized = true;

        }
    }

    private String retrieveLabel(AsmLine<T> firstAsmLine) {
        String offsetLabel = firstAsmLine.identifier_1;
        if (offsetLabel == null) {
            offsetLabel = firstAsmLine.offsetLabel_1;
        }
        return offsetLabel;
    }

}
