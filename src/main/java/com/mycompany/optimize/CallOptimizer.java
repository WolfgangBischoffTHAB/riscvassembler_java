package com.mycompany.optimize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;

public class CallOptimizer implements AsmInstructionListModifier {

    @Override
    public void modify(List<AsmLine> asmLines) {

        // build label table
        Map<String, Integer> map = new HashMap<>();
        buildLabelTable(asmLines, map);

        updateAddresses(asmLines);

        // find unoptimized call pseudo instruction
        AsmLine callPseudoAsmLine = null;
        int index = 0;
        for (AsmLine asmLine : asmLines) {
            if ((asmLine.pseudoInstructionAsmLine != null) && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_CALL) && (asmLine.pseudoInstructionAsmLine.optimized == false)) {
                callPseudoAsmLine = asmLine.pseudoInstructionAsmLine;
                break;
            }
            index++;
        }

        // start with first child instruction
        AsmLine firstAsmLine = callPseudoAsmLine.pseudoInstructionChildren.get(0);

        // determine movement direction towards label (use label table for that)
        int direction = 0;
        if (firstAsmLine.address > map.get(firstAsmLine.offsetLabel_1)) {
            direction = -1;
        } else {
            direction = +1;
        }

        if (direction == -1) {

            // move upwards
            for (int i = index-1; i > 0; i--) {

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

        // if arriving at the target label is possible only crossing real instructions
        // take the absolute value of the label and put it into the modifier.

        // if the absolute label address is exactly 12-bit throw exception!

        // if the modifier returns 0, the instruction can be optimized
    }

    private void updateAddresses(List<AsmLine> asmLines) {

        int address = 0;
        for (AsmLine asmLine : asmLines) {

            asmLine.address = address;

            if (asmLine.mnemonic == null) {

                if (asmLine.asmInstruction != null) {
                    switch (asmLine.asmInstruction) {

                        case BYTE:
                            address += asmLine.csvList.size() * 1;
                            break;

                        case WORD:
                            address += asmLine.csvList.size() * 4;
                            break;

                        case STRING:
                            address += asmLine.stringValue.length();
                            break;

                        default:
                            throw new RuntimeException();
                    }
                }

                continue;
            }

            address += 4;
        }
    }

    private void buildLabelTable(List<AsmLine> asmLines, Map<String, Integer> map) {

        int address = 0;
        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic == null) {

                if (asmLine.asmInstruction != null) {
                    switch (asmLine.asmInstruction) {

                        case BYTE:
                            address += asmLine.csvList.size() * 1;
                            break;

                        case WORD:
                            address += asmLine.csvList.size() * 4;
                            break;

                        case STRING:
                            address += asmLine.stringValue.length();
                            break;

                        default:
                            throw new RuntimeException();
                    }
                }

                if (asmLine.label != null) {
                    map.put(asmLine.label, address);
                }

                continue;
            }

            address += 4;
        }

        // DEBUG
        for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
            System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        }
        System.out.println("test");
    }

}
