package com.mycompany.pseudo.resolve;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.common.StringUtils;
import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;

/**
 * converts pseudo instruction j to jal
 */
public class JResolver implements AsmInstructionListModifier<RISCVRegister> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void modify(List<AsmLine<RISCVRegister>> asmLines, final Map<String, Section> sectionMap) {

        // build label table
        Map<String, Long> labelTableMap = new HashMap<>();
        Map<Long, AsmLine<RISCVRegister>> offsetAsmLineMap = new HashMap<>();
        buildLabelTable(asmLines, labelTableMap, offsetAsmLineMap, sectionMap);

        for (AsmLine asmLine : asmLines) {

            if (asmLine.mnemonic != Mnemonic.I_J) {
                continue;
            }

            asmLine.mnemonic = Mnemonic.I_JAL;
            asmLine.register_0 = RISCVRegister.REG_ZERO;

            // This is needed for e.g.:      j .L18
            asmLine.identifier_1 = asmLine.identifier_0;
            long off = labelTableMap.get(asmLine.identifier_1);
            asmLine.referencedTarget = offsetAsmLineMap.get(off);

            asmLine.identifier_0 = null;
        }
    }

    public void buildLabelTable(final List<AsmLine<RISCVRegister>> asmLines,
            final Map<String, Long> labelAddressMap, Map<Long, AsmLine<RISCVRegister>> offsetAsmLineMap,
            final Map<String, Section> sectionMap) {

        // reset the offsets in the sectionMap
        for (Map.Entry<String, Section> entry : sectionMap.entrySet()) {
            entry.getValue().setCurrentOffset(0);
        }

        for (AsmLine<RISCVRegister> asmLine : asmLines) {

            // convert section name to offset
            Section section = sectionMap.get(asmLine.section.name);
            long offset = section.getCurrentOffset();
            asmLine.setOffset(offset);

            if (asmLine.mnemonic != null) {

                if (asmLine.label != null) {
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.getOffset());
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.getOffset(), asmLine);
                    }
                }

                section.setCurrentOffset(section.getCurrentOffset() + 4);

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
                        offset += (StringUtils.stringLengthWithEscape(asmLine.stringValue) + 0); // +0 for no zero termination
                        break;

                    // https://course.ece.cmu.edu/~ee349/f-2012/lab2/gas-tips.pdf
                    // The GNU assembler (gas) recognizes three assembler directives for defining
                    // strings.
                    // “.string” and “.asciz” both assemble string literals with null terminators
                    // (the same as C strings),
                    // whereas “.ascii” assembles a string literal with no null terminator
                    case ASCIZ:
                    case STRING:
                        offset += (StringUtils.stringLengthWithEscape(asmLine.stringValue) + 1); // +1 for zero termination
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
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.getOffset());
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.getOffset(), asmLine);
                    }
                }

                section.setCurrentOffset(offset);

                continue;

            } else {

                if (asmLine.label != null) {
                    if (asmLine.section == null) {
                        throw new RuntimeException("bug");
                    }
                    labelAddressMap.put(asmLine.label, asmLine.section.address + asmLine.getOffset());
                    if (offsetAsmLineMap != null) {
                        offsetAsmLineMap.put(asmLine.section.address + asmLine.getOffset(), asmLine);
                    }
                }

            }
        }
    }
}
