package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.data.Section;

public abstract class BaseOptimizer<T extends Register> implements AsmInstructionListModifier<T> {

    static class Match {
        public AsmLine<?> asmLine;
        public long offset;
    }

    private static final Logger logger = LoggerFactory.getLogger(BaseOptimizer.class);

    /**
     * Iterates over all asm lines and updates the offset variable in the
     * AsmLine objects. Usefull after pseudo instructions have been resolved
     * to one or more real instructions and as a result, the addresses of all
     * following lines have changed.
     * 
     * The assembler instruction is executed in AsmInstructionEncoder.java
     *
     * @param asmLines
     * @param sectionMap
     */
    public void updateAddresses(final List<AsmLine<T>> asmLines, final Map<String, Section> sectionMap) {

        int address = 0;

        for (AsmLine<?> asmLine : asmLines) {

            asmLine.offset = address;

            System.out.println("[" + asmLine.offset + "] " + asmLine.toString());

            if (asmLine.mnemonic == null) {

                if (asmLine.asmInstruction != null) {

                    switch (asmLine.asmInstruction) {

                        case ZERO:
                            // reserve space and prefill the space with 0x00
                            address += asmLine.numeric_0;
                            break;

                        case SPACE:
                            address += NumberParseUtil.parseLong(asmLine.csvList.get(0)) * 1;
                            break;

                        case BYTE:
                            address += asmLine.csvList.size() * 1;
                            break;

                        case WORD:
                            address += asmLine.csvList.size() * 4;
                            break;

                        case QUAD:
                            address += asmLine.csvList.size() * 8;
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

                // continue;

            } else {
                // assume every instruction is 4 byte in size (TODO: COMPRESSED INSTRUCTIONS!!!)
                address += 4;
            }
        }
    }

    public void buildLabelTable(final List<AsmLine<T>> asmLines,
            final Map<String, Long> labelAddressMap, Map<Long, AsmLine<T>> offsetAsmLineMap,
            final Map<String, Section> sectionMap) {

        // reset the offsets in the sectionMap
        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().currentOffset = 0;
        }

        for (AsmLine<T> asmLine : asmLines) {

            // convert section name to offset
            Section section = sectionMap.get(asmLine.section.name);
            long offset = section.currentOffset;
            asmLine.offset = offset;

            if (asmLine.mnemonic != null) {

                if (asmLine.label != null) {
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.offset);
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.offset, asmLine);
                    }
                }

                section.currentOffset += 4;

            } else if (asmLine.asmInstruction != null) {

                switch (asmLine.asmInstruction) {

                    case ZERO:
                        // reserve space and prefill the space with 0x00
                        offset += asmLine.numeric_0;
                        break;

                    case SPACE:
                        offset += NumberParseUtil.parseLong(asmLine.csvList.get(0)) * 1;
                        break;

                    case BYTE:
                        offset += asmLine.csvList.size() * 1;
                        break;

                    case WORD:
                        offset += asmLine.csvList.size() * 4;
                        break;

                    case QUAD:
                        offset += asmLine.csvList.size() * 8;
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
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.offset, asmLine);
                    }
                }

                section.currentOffset = offset;

                continue;

            } else {

                if (asmLine.label != null) {
                    if (asmLine.section == null) {
                        throw new RuntimeException("bug");
                    }
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.offset);
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.offset, asmLine);
                    }
                }

            }
        }
    }

    public static void outputLabelAddressMap(final Map<String, Long> labelAddressMap) {
        // DEBUG
        logger.info("*************************************************");
        for (Map.Entry<String, Long> mapEntry : labelAddressMap.entrySet()) {
            logger.info(mapEntry.getKey() + " -> " + mapEntry.getValue());
        }
        logger.info("-------------------------------------------------");
    }

    /**
     * Resolves labels to offsets.<br />
     * <br />
     *
     * For each label,
     * <ol>
     * 
     * <li>the absolute address of that label
     * is retrieved in a first step.</li>
     * 
     * <li>In the second step, the relative offset of
     * the label from the current asm line in which the label is used (pc-relative)
     * is computed.</li>
     * 
     * <li>In a third step, the pc-relative offset is stored inside
     * the AsmLine's numeric_xyz member.</li>
     * 
     * </ol>
     *
     * @param asmLines
     * @param labelAddressMap
     */
    public void resolveLabels(List<AsmLine<RISCVRegister>> asmLines, Map<String, Long> labelAddressMap) {

        // connect lines to each other for easier traversal
        AsmLine<RISCVRegister> prev = null;
        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            asmLine.prev = prev;
            if (prev != null) {
                prev.next = asmLine;
            }

            prev = asmLine;
        }

        for (AsmLine<?> asmLine : asmLines) {

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

            if (asmLine.identifier_0 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_0);
                if (value != null) {

                    asmLine.numeric_0 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_0 = null;

                } else if (asmLine.identifier_0.toLowerCase().matches("[0-9]+b")) {

                    AsmLine<?> tempAsmLine = asmLine;
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
                        asmLine.numeric_0 = offset;
                    }

                } else if (asmLine.identifier_0.toLowerCase().matches("[0-9]+f")) {

                    AsmLine<?> tempAsmLine = asmLine;
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
                        asmLine.numeric_0 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_1 + "\"");

                }
            }

            if (asmLine.identifier_1 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_1);
                if (value != null) {

                    asmLine.numeric_1 = value - (asmLine.section.address + asmLine.offset);
                    asmLine.identifier_1 = null;

                } else if (asmLine.identifier_1.toLowerCase().matches("[0-9]+b")) {

                    AsmLine<?> tempAsmLine = asmLine;
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
                        asmLine.numeric_1 = offset;
                    }

                } else if (asmLine.identifier_1.toLowerCase().matches("[0-9]+f")) {

                    AsmLine<?> tempAsmLine = asmLine;
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
                        asmLine.numeric_1 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_1 + "\"");

                }
            }

            if (asmLine.identifier_2 != null) {

                Long value = labelAddressMap.get(asmLine.identifier_2);
                if (value != null) {

                    asmLine.numeric_2 = value - (asmLine.section.address + asmLine.offset + 0);
                    asmLine.identifier_2 = null;

                } else if (asmLine.identifier_2.toLowerCase().matches("[0-9]+b")) {

                    // iterate backwards and count +4 every time until label is found
                    // use the resulting value as numeric_2

                    String truncatedLabel = asmLine.identifier_2.substring(0,
                            asmLine.identifier_2.length() - 1);
                    Match match = findLabelBackwards(asmLine, truncatedLabel);

                    if (match.asmLine != null) {
                        asmLine.numeric_2 = match.offset;
                    }

                } else if (asmLine.identifier_2.toLowerCase().matches("[0-9]+f")) {

                    AsmLine<?> tempAsmLine = asmLine;
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
                        asmLine.numeric_2 = offset;
                    }

                } else {

                    throw new RuntimeException("(F) Unknown label: \"" + asmLine.identifier_2 + "\"");

                }
            }

        }
    }

    private static Match findLabelBackwards(AsmLine<?> asmLine, String label) {

        AsmLine<?> tempAsmLine = asmLine;
        long offset = 0;

        while ((tempAsmLine != null)
                && ((tempAsmLine.label == null) || (!tempAsmLine.label.equalsIgnoreCase(label)))) {
            tempAsmLine = tempAsmLine.prev;
            if (tempAsmLine == null) {
                // label could not be found!
                throw new RuntimeException(
                        "Looking backwards for label \"" + label + "\" but label could not be found!");
            }
            if (tempAsmLine.mnemonic != null) {
                offset -= 4;
            }
        }

        Match match = new Match();
        match.asmLine = tempAsmLine;
        match.offset = offset;

        return match;
    }

    /**
     * Resolve all modifiers (HI, LO) to addresses.
     *
     * @param asmLines        all assembler lines to process
     * @param labelAddressMap labels (functions, labels) are mapped to addresses
     */
    public static void resolveModifiers(List<AsmLine<RISCVRegister>> asmLines, Map<String, Long> labelAddressMap) {

        int offset = 4;

        // initialize PC with the start symbol
        // TODO: what if the source code does not use the specific _start label?
        // What if the source code uses main: for example?
        long pc = labelAddressMap.get("_start");

        // connect lines to each other for easier traversal
        AsmLine<RISCVRegister> prev = null;
        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            asmLine.prev = prev;
            if (prev != null) {
                prev.next = asmLine;
            }

            prev = asmLine;
        }

        for (AsmLine<?> asmLine : asmLines) {

            logger.trace(asmLine.toString());

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

                Long address = labelAddressMap.get(label);

                // special case for JALR: labels are resolved relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    address = address - (asmLine.offset + offset);
                }

                switch (asmLine.modifier_0) {

                    case LO:
                        newValue = (address >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (address >> 12) & 0xFFFFF;
                        break;

                    case PCREL_LO:
                        newValue = (address - pc >> 0) & 0xFFF;
                        break;

                    case PCREL_HI:
                        newValue = (address - pc >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_0 = null;
                asmLine.modifier_0 = null;
                asmLine.identifier_0 = null;

                if ((asmLine.register_0 == null) || (asmLine.register_0 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_0 = newValue;
                } else {
                    asmLine.offset_0 = newValue;
                }
            }

            if (asmLine.modifier_1 != null) {

                long newValue = 0L;
                String label = asmLine.identifier_1;

                Long address = labelAddressMap.get(label);

                // special case for JALR: labels are resolved relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    address = address - (asmLine.offset + offset);
                }

                switch (asmLine.modifier_1) {

                    case LO:
                        newValue = (address >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (address >> 12) & 0xFFFFF;
                        break;

                    case PCREL_LO:
                        newValue = (address - pc >> 0) & 0xFFF;
                        break;

                    case PCREL_HI:
                        newValue = (address - pc >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_1 = null;
                asmLine.modifier_1 = null;
                asmLine.identifier_1 = null;

                if ((asmLine.register_1 == null) || (asmLine.register_1 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_1 = newValue;
                } else {
                    asmLine.offset_1 = newValue;
                }
            }

            if (asmLine.modifier_2 != null) {

                long newValue = 0L;
                String label = asmLine.identifier_2;

                Long address = null;

                if (asmLine.identifier_2.endsWith("b")) {

                    String truncatedLabel = asmLine.identifier_2.substring(0,
                            asmLine.identifier_2.length() - 1);
                    Match match = findLabelBackwards(asmLine, truncatedLabel);

                    address = match.asmLine.offset;

                } else {

                    address = labelAddressMap.get(label);

                }

                // special case for JALR: labels are resolved pc relative
                if (asmLine.mnemonic == Mnemonic.I_JALR) {
                    address = address - (asmLine.offset + offset);
                }

                switch (asmLine.modifier_2) {

                    case LO:
                        newValue = (address >> 0) & 0xFFF;
                        break;

                    case HI:
                        newValue = (address >> 12) & 0xFFFFF;
                        break;

                    case PCREL_LO:
                        newValue = (address - pc >> 0) & 0xFFF;
                        break;

                    case PCREL_HI:
                        newValue = (address - pc >> 12) & 0xFFFFF;
                        break;

                    default:
                        throw new RuntimeException();
                }

                asmLine.offsetLabel_2 = null;
                asmLine.modifier_2 = null;
                asmLine.identifier_2 = null;

                if ((asmLine.register_2 == null) || (asmLine.register_2 == RISCVRegister.REG_UNKNOWN)) {
                    asmLine.numeric_2 = newValue;
                } else {
                    asmLine.offset_2 = newValue;
                }
            }

            if (asmLine.mnemonic != null) {
                // pc += asmLine.encodedLength;
                pc += 4; // what about compressed instructions? They are only two bytes in size.
            }
        }

    }
}
