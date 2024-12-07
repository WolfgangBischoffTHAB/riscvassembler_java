package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;

public abstract class BaseOptimizer implements AsmInstructionListModifier {

    public static void updateAddresses(List<AsmLine> asmLines) {

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

                        // https://course.ece.cmu.edu/~ee349/f-2012/lab2/gas-tips.pdf
                        // The GNU assembler (gas) recognizes three assembler directives for defining strings.
                        // “.string” and “.asciz” both assemble string literals with null terminators (the same as C strings),
                        // whereas “.ascii” assembles a string literal with no null terminator
                        case ASCIZ:
                        case STRING:
                            address += asmLine.stringValue.length() + 1; // +1 for zero termination
                            break;

                        case EQU:
                        case DATA:
                        case SECTION:
                        case TEXT:
                        case GLOBAL:
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

    public static void buildLabelTable(List<AsmLine> asmLines, Map<String, Long> map) {

        long address = 0;
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

                        // https://course.ece.cmu.edu/~ee349/f-2012/lab2/gas-tips.pdf
                        // The GNU assembler (gas) recognizes three assembler directives for defining strings.
                        // “.string” and “.asciz” both assemble string literals with null terminators (the same as C strings),
                        // whereas “.ascii” assembles a string literal with no null terminator
                        case ASCIZ:
                        case STRING:
                            address += asmLine.stringValue.length() + 1; // +1 for zero termination
                            break;

                        case EQU:
                        case DATA:
                        case SECTION:
                        case TEXT:
                        case GLOBAL:
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

        // // DEBUG
        // for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
        //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        // }
    }
}
