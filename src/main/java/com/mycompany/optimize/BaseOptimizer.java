package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
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
    public static void updateAddresses(final List<AsmLine<?>> asmLines, final Map<String, Section> sectionMap) {

        int address = 0;

        for (AsmLine<?> asmLine : asmLines) {

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

    public static void buildLabelTable(final List<AsmLine<?>> asmLines,
            final Map<String, Long> labelAddressMap, final Map<String, Section> sectionMap) {

        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().currentOffset = 0;
        }

        for (AsmLine<?> asmLine : asmLines) {

            // convert section name to offset
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

        // outputLabelAddressMap(labelAddressMap);
    }

    public static void outputLabelAddressMap(final Map<String, Long> labelAddressMap) {
        // DEBUG
        System.out.println("*************************************************");
        for (Map.Entry<String, Long> mapEntry : labelAddressMap.entrySet()) {
            System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        }
        System.out.println("-------------------------------------------------");
    }

    /**
     * Resolves labels to offset! For each label, the absolute address of that label
     * is retrieved in a first step. In the second step, the relative offset of
     * the label from the current asm line in which the label is used (pc-relative)
     * is compputed. In a third step, the pc-relative offset is stored inside
     * the AsmLine's numeric_xyz member.
     *
     * @param asmLines
     * @param labelAddressMap
     */
    public static void resolveLabels(List<AsmLine<?>> asmLines, Map<String, Long> labelAddressMap) {

        AsmLine prev = null;
        for (AsmLine asmLine : asmLines) {

            asmLine.prev = prev;
            if (prev != null) {
                prev.next = asmLine;
            }

            prev = asmLine;
        }

        for (AsmLine asmLine : asmLines) {
            // if (asmLine.mnemonic == Mnemonic.I_BNE) {
            // System.out.println("test");
            // }

            if (asmLine.asmInstruction != null) {
                continue;
            }

            if ((asmLine.pseudoInstructionAsmLine != null)
                    && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)
                    && (asmLine.mnemonic == Mnemonic.I_AUIPC)) {
                // the final resolution is done in MnemonicEncoder.encodeAUIPC()
                continue;
            }
            if ((asmLine.pseudoInstructionAsmLine != null)
                    && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)
                    && (asmLine.mnemonic == Mnemonic.I_ADDI)) {
                // the final resolution is done in MnemonicEncoder.encodeADDI()
                continue;
            }

            // if (asmLine.offsetLabel_0 != null) {
            // Long value = labelAddressMap.get(asmLine.offsetLabel_0);
            // if (value != null) {
            // asmLine.numeric_0 = value - (asmLine.section.address + asmLine.offset);
            // asmLine.offsetLabel_0 = null;
            // } else {
            // throw new RuntimeException("(A) Unknown label: \"" + asmLine.offsetLabel_0 +
            // "\"");
            // }
            // }
            if (asmLine.identifier_0 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_0);
                if (value != null) {

                    asmLine.numeric_0 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_0 = null;

                } else if (asmLine.identifier_0.endsWith("b")) {

                    AsmLine tempAsmLine = asmLine;
                    long offset = 0;

                    String truncatedLabel = tempAsmLine.identifier_0.substring(0,
                            tempAsmLine.identifier_0.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.prev;
                        if (tempAsmLine.mnemonic != null) {
                            offset -= 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_0 = offset;
                    }

                } else if (asmLine.identifier_0.endsWith("f")) {

                    AsmLine tempAsmLine = asmLine;
                    long offset = 4;

                    String truncatedLabel = tempAsmLine.identifier_0.substring(0,
                            tempAsmLine.identifier_0.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.next;
                        if (tempAsmLine.mnemonic != null) {
                            offset += 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_0 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_1 + "\"");

                }
            }

            // if (asmLine.offsetLabel_1 != null) {
            // Long value = labelAddressMap.get(asmLine.offsetLabel_1);
            // if (value != null) {
            // asmLine.numeric_1 = value - (asmLine.section.address + asmLine.offset);
            // asmLine.offsetLabel_1 = null;
            // } else {
            // throw new RuntimeException("(C) Unknown label: \"" + asmLine.offsetLabel_1 +
            // "\"");
            // }
            // }
            if (asmLine.identifier_1 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_1);
                if (value != null) {

                    asmLine.numeric_1 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_1 = null;

                } else if (asmLine.identifier_1.endsWith("b")) {

                    AsmLine tempAsmLine = asmLine;
                    long offset = 0;

                    String truncatedLabel = tempAsmLine.identifier_1.substring(0,
                            tempAsmLine.identifier_1.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.prev;
                        if (tempAsmLine.mnemonic != null) {
                            offset -= 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_1 = offset;
                    }

                } else if (asmLine.identifier_1.endsWith("f")) {

                    AsmLine tempAsmLine = asmLine;
                    long offset = 4;

                    String truncatedLabel = tempAsmLine.identifier_1.substring(0,
                            tempAsmLine.identifier_1.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.next;
                        if (tempAsmLine.mnemonic != null) {
                            offset += 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_1 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_1 + "\"");

                }
            }

            // if (asmLine.offsetLabel_2 != null) {
            // Long value = labelAddressMap.get(asmLine.offsetLabel_2);
            // if (value != null) {
            // asmLine.numeric_2 = value - (asmLine.section.address + asmLine.offset);
            // asmLine.offsetLabel_2 = null;
            // } else {
            // throw new RuntimeException("(E) Unknown label: \"" + asmLine.offsetLabel_2 +
            // "\"");
            // }
            // }
            if (asmLine.identifier_2 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_2);
                if (value != null) {

                    asmLine.numeric_2 = value - (asmLine.section.address + asmLine.offset + 0);
                    asmLine.identifier_2 = null;

                } else if (asmLine.identifier_2.endsWith("b")) {

                    // iterate backwards and count +4 every time until label is found
                    // use the resulting value as numeric_2

                    AsmLine tempAsmLine = asmLine;
                    long offset = 0;

                    String truncatedLabel = tempAsmLine.identifier_2.substring(0,
                            tempAsmLine.identifier_2.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.prev;
                        if (tempAsmLine.mnemonic != null) {
                            offset -= 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_2 = offset;
                    }

                } else if (asmLine.identifier_2.endsWith("f")) {

                    AsmLine tempAsmLine = asmLine;
                    long offset = 4;

                    String truncatedLabel = tempAsmLine.identifier_2.substring(0,
                            tempAsmLine.identifier_2.length() - 1);
                    while ((tempAsmLine != null)
                            && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(truncatedLabel)))) {
                        tempAsmLine = tempAsmLine.next;
                        if (tempAsmLine.mnemonic != null) {
                            offset += 4;
                        }
                    }
                    if (tempAsmLine != null) {
                        System.out.println("found");
                        asmLine.numeric_2 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_2 + "\"");

                }
            }

        }
    }

    /**
     * Resolve all modifiers
     *
     * @param asmLines
     * @param map
     */
    public static void resolveModifiers(List<AsmLine<?>> asmLines, Map<String, Long> map) {

        //int offset = 4;
        int offset = 0;

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic == Mnemonic.I_JALR) {
                System.out.println("DEBUG");
            }

            if ((asmLine.pseudoInstructionAsmLine != null)
                    && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)
                    && (asmLine.mnemonic == Mnemonic.I_AUIPC)) {
                // the resolution is done in MnemonicEncoder.encodeAUIPC()
                continue;
            }
            if ((asmLine.pseudoInstructionAsmLine != null)
                    && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)
                    && (asmLine.mnemonic == Mnemonic.I_ADDI)) {
                // the resolution is done in MnemonicEncoder.encodeADDI()
                continue;
            }

            if (asmLine.modifier_0 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_0;

                Long value = map.get(label);

                // special case for JALR: labels are resolved relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    value = value - (asmLine.offset - offset);
                }

                switch (asmLine.modifier_0) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_0 = null;
                asmLine.modifier_0 = null;

                if ((asmLine.register_0 == null) || (asmLine.register_0 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_0 = newValue;
                } else {
                    asmLine.offset_0 = newValue;
                }
            }

            if (asmLine.modifier_1 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_1;

                Long value = map.get(label);

                // special case for JALR: labels are resolved relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    value = value - (asmLine.offset - offset);
                }

                switch (asmLine.modifier_1) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_1 = null;
                asmLine.modifier_1 = null;

                if ((asmLine.register_1 == null) || (asmLine.register_1 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_1 = newValue;
                } else {
                    asmLine.offset_1 = newValue;
                }
            }

            if (asmLine.modifier_2 != null) {

                long newValue = 0L;
                String label = asmLine.offsetLabel_2;

                Long value = map.get(label);

                // special case for JALR: labels are resolved pc relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    value = value - (asmLine.offset - offset);
                }

                switch (asmLine.modifier_2) {

                    case LO:
                        newValue = (value >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (value >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_2 = null;
                asmLine.modifier_2 = null;

                if ((asmLine.register_2 == null) || (asmLine.register_2 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_2 = newValue;
                } else {
                    asmLine.offset_2 = newValue;
                }
            }
        }
    }
}
