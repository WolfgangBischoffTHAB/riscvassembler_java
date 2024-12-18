package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;

public abstract class BaseOptimizer implements AsmInstructionListModifier {

    public static void updateAddresses(List<AsmLine> asmLines) {

        int address = 0;

        for (AsmLine asmLine : asmLines) {

            asmLine.offset = address;

            if (asmLine.mnemonic == null) {

                if (asmLine.asmInstruction != null) {
                    switch (asmLine.asmInstruction) {

                        case BYTE:
                            address += asmLine.csvList.size() * 1;
                            break;

                        case WORD:
                            address += asmLine.csvList.size() * 4;
                            break;

                        case ASCII:
                            address += asmLine.stringValue.length() + 0; // +0 for no zero termination
                            break;

                        // https://course.ece.cmu.edu/~ee349/f-2012/lab2/gas-tips.pdf
                        // The GNU assembler (gas) recognizes three assembler directives for defining
                        // strings.
                        // “.string” and “.asciz” both assemble string literals with null terminators
                        // (the same as C strings),
                        // whereas “.ascii” assembles a string literal with no null terminator
                        case ASCIZ:
                        case STRING:
                            address += asmLine.stringValue.length() + 1; // +1 for zero termination
                            break;

                        case FILE:
                        case EQU:
                        case DATA:
                        case SECTION:
                        case TEXT:
                        case GLOBAL:
                            break;

                        default:
                            throw new RuntimeException("Unknown assembler instruction: " + asmLine.asmInstruction);
                    }
                }

                continue;
            }

            address += 4;
        }
    }

    public static void buildLabelTable(final List<AsmLine> asmLines, final Map<String, Long> map) {

        int offset = 0;

        for (AsmLine asmLine : asmLines) {

            asmLine.offset = offset;

            if (asmLine.mnemonic != null) {

                if (asmLine.label != null) {
                    map.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

                offset += 4;

            } else if (asmLine.asmInstruction != null) {

                switch (asmLine.asmInstruction) {

                    case BYTE:
                        offset += asmLine.csvList.size() * 1;
                        break;

                    case WORD:
                        offset += asmLine.csvList.size() * 4;
                        break;

                    case ASCII:
                        offset += asmLine.stringValue.length() + 0; // +0 for no zero termination
                        break;

                    // https://course.ece.cmu.edu/~ee349/f-2012/lab2/gas-tips.pdf
                    // The GNU assembler (gas) recognizes three assembler directives for defining
                    // strings.
                    // “.string” and “.asciz” both assemble string literals with null terminators
                    // (the same as C strings),
                    // whereas “.ascii” assembles a string literal with no null terminator
                    case ASCIZ:
                    case STRING:
                        offset += asmLine.stringValue.length() + 1; // +1 for zero termination
                        break;

                    case FILE:
                    case EQU:
                    case DATA:
                    case SECTION:
                    case TEXT:
                    case GLOBAL:
                        break;

                    default:
                        throw new RuntimeException("Unknown assembler instruction: " + asmLine.asmInstruction);
                }

                if (asmLine.label != null) {
                    map.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

                continue;

            } else {

                if (asmLine.label != null) {
                    map.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

            }
        }

        // DEBUG
        System.out.println("*************************************************");
        for (Map.Entry<String, Long> mapEntry : map.entrySet()) {
            System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        }
        System.out.println("-------------------------------------------------");
    }
}
