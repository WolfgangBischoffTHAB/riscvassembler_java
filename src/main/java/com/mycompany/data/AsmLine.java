package com.mycompany.data;

import java.util.List;

public class AsmLine {

    public String label;

    public Mnemonic mnemonic;

    public Register register_0 = Register.REG_UNKNOWN;
    public Register register_1 = Register.REG_UNKNOWN;
    public Register register_2 = Register.REG_UNKNOWN;

    public Integer numeric_0 = null;
    public Integer numeric_1 = null;
    public Integer numeric_2 = null;

    public Integer offset_0 = null;
    public Integer offset_1 = null;
    public Integer offset_2 = null;

    public String identifier_0 = null;
    public String identifier_1 = null;
    public String identifier_2 = null;

    public AsmInstruction asm_instruction = null;

    public List<String> csvList = null;

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        if (label != null) {
            stringBuilder.append(label).append(": ");
        }

        if (asm_instruction != null) {
            stringBuilder.append(AsmInstruction.toString(asm_instruction)).append(": ");
        }

        if (csvList != null) {
            boolean first = true;
            for (String val : csvList) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(val);

                first = false;
            }
        }

        if (mnemonic == null) {
            return stringBuilder.toString();
        }

        stringBuilder.append(Mnemonic.toString(mnemonic)).append(" ");

        if (numeric_0 != null) {
            stringBuilder.append(String.format("0x%08X", numeric_0));
        }
        if (identifier_0 != null) {
            stringBuilder.append(identifier_0);
        }
        if (offset_0 != null) {
            stringBuilder.append(offset_0).append("(");
            if (register_0 != Register.REG_UNKNOWN) {
                stringBuilder.append(Register.toStringAbi(register_0));
            }
            stringBuilder.append(")");
        } else {
            if (register_0 != Register.REG_UNKNOWN) {
                stringBuilder.append(Register.toStringAbi(register_0));
            }
        }

        if (numeric_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%08X", numeric_1));
        }
        if (identifier_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_1);
        }
        if (offset_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_1).append("(");
            if (register_1 != Register.REG_UNKNOWN) {
                stringBuilder.append(Register.toStringAbi(register_1));
            }
            stringBuilder.append(")");
        } else {
            if (register_1 != Register.REG_UNKNOWN) {
                stringBuilder.append(", ");
                stringBuilder.append(Register.toStringAbi(register_1));
            }
        }

        if (numeric_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(String.format("0x%08X", numeric_2));
        }
        if (identifier_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(identifier_2);
        }
        if (offset_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(offset_2).append("(");
            if (register_2 != Register.REG_UNKNOWN) {
                stringBuilder.append(Register.toStringAbi(register_2));
            }
            stringBuilder.append(")");
        } else {
            if (register_2 != Register.REG_UNKNOWN) {
                stringBuilder.append(", ");
                stringBuilder.append(Register.toStringAbi(register_2));
            }
        }

        return stringBuilder.toString();
    }

}
