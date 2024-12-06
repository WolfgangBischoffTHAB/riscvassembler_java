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

    public AsmInstruction asmInstruction = null;

    public List<String> csvList = null;

    public Modifier modifier_0;
    public Modifier modifier_1;
    public Modifier modifier_2;

    public String stringValue;

    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();

        if (label != null) {
            stringBuilder.append(label).append(": ");
        }

        if (asmInstruction != null) {
            stringBuilder.append(AsmInstruction.toString(asmInstruction));
            switch (asmInstruction) {
                case STRING:
                    stringBuilder.append(" \"").append(stringValue).append("\"");
                    break;

                default:
                    stringBuilder.append(" ");
                    break;
            }
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
        if (modifier_0 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_0)).append("(");
            if (identifier_0 != null) {
                stringBuilder.append(identifier_0);
            }
            stringBuilder.append(")");
        } else if (identifier_0 != null) {
            stringBuilder.append(", ");
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
        if (modifier_1 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_1)).append("(");
            if (identifier_1 != null) {
                stringBuilder.append(identifier_1);
            }
            stringBuilder.append(")");
        } else if (identifier_1 != null) {
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
        if (modifier_2 != null) {
            stringBuilder.append(", ");
            stringBuilder.append(Modifier.toString(modifier_2)).append("(");
            if (identifier_2 != null) {
                stringBuilder.append(identifier_2);
            }
            stringBuilder.append(")");
        } else if (identifier_2 != null) {
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
