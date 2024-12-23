package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Section;

public abstract class BaseOptimizer implements AsmInstructionListModifier {

    /**
     * Iterates over all asm lines and updates the offset variable in the 
     * AsmLine objects. Usefull after pseudo instructions have been resolved
     * to one or more real instructions and as a result, the addresses of all 
     * following lines have changed.
     * 
     * @param asmLines
     * @param sectionMap
     */
    public static void updateAddresses(final List<AsmLine> asmLines, final Map<String, Section> sectionMap) {

        int address = 0;

        for (AsmLine asmLine : asmLines) {

            asmLine.offset = address;

            if (asmLine.mnemonic == null) {

                if (asmLine.asmInstruction != null) {
                    switch (asmLine.asmInstruction) {

                        case SPACE:
                            address += NumberParseUtil.parseLong(asmLine.csvList.get(0)) * 1;
                            break;

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

    public static void buildLabelTable(final List<AsmLine> asmLines,
        final Map<String, Long> labelAddressMap, final Map<String, Section> sectionMap) {

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().currentOffset = 0;
        }

        for (AsmLine asmLine : asmLines) {

            Section section = sectionMap.get(asmLine.section.name);

            long offset = section.currentOffset;
            asmLine.offset = offset;

            if (asmLine.mnemonic != null) {

                if (asmLine.label != null) {
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

                section.currentOffset += 4;

            } else if (asmLine.asmInstruction != null) {

                switch (asmLine.asmInstruction) {

                    case SPACE:
                        offset += NumberParseUtil.parseLong(asmLine.csvList.get(0)) * 1;
                        break;

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
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

                section.currentOffset = offset;

                continue;

            } else {

                if (asmLine.label != null) {
                    if (asmLine.section == null) {
                        System.out.println("bug");
                    }
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.offset);
                }

            }
        }

        //outputLabelAddressMap(labelAddressMap);
    }

    public static void outputLabelAddressMap(final Map<String, Long> labelAddressMap) {
        // DEBUG
        System.out.println("*************************************************");
        for (Map.Entry<String, Long> mapEntry : labelAddressMap.entrySet()) {
            System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        }
        System.out.println("-------------------------------------------------");
    }
}
