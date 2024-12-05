package com.mycompany.data;

public class AsmLine {

    public Mnemonic mnemonic;

    public Register register_0 = Register.REG_UNKNOWN;
    public Register register_1 = Register.REG_UNKNOWN;
    public Register register_2 = Register.REG_UNKNOWN;

    public Integer numeric_0 = null;
    public Integer numeric_1 = null;
    public Integer numeric_2 = null;

    public String toString() {

        if (mnemonic == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(Mnemonic.toString(mnemonic)).append(" ");

        if (register_0 != Register.REG_UNKNOWN) {
            stringBuilder.append(Register.toStringAbi(register_0)).append(", ");
        }
        if (register_1 != Register.REG_UNKNOWN) {
            stringBuilder.append(Register.toStringAbi(register_1)).append(", ");
        }
        if (register_2 != Register.REG_UNKNOWN) {
            stringBuilder.append(Register.toStringAbi(register_2));
        }

        if (numeric_0 != null) {
            stringBuilder.append(numeric_0).append(", ");
        }
        if (numeric_1 != null) {
            stringBuilder.append(numeric_1).append(", ");
        }
        if (numeric_2 != null) {
            stringBuilder.append(numeric_2);
        }


        return stringBuilder.toString();
        //return Mnemonic.toString(mnemonic) + " " + Register.toStringAbi(register_0) + " " + Register.toStringAbi(register_1);
    }

}
