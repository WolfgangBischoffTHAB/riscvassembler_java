package com.mycompany.optimize;

import java.util.List;
import java.util.Map;

import com.mycompany.data.AsmInstructionListModifier;
import com.mycompany.data.AsmLine;

public abstract class BaseOptimizer implements AsmInstructionListModifier {

    protected void updateAddresses(List<AsmLine> asmLines) {

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

    protected void buildLabelTable(List<AsmLine> asmLines, Map<String, Integer> map) {

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

        // // DEBUG
        // for (Map.Entry<String, Integer> mapEntry : map.entrySet()) {
        //     System.out.println(mapEntry.getKey() + " -> " + mapEntry.getValue());
        // }
    }
}
