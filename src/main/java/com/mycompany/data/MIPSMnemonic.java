package com.mycompany.data;

public enum MIPSMnemonic {

    I_ADD(false),

    I_LI(true);

    private boolean pseudo;

    MIPSMnemonic(final boolean pseudo) {
        this.pseudo = pseudo;
    }

    public boolean isPseudo() {
        return pseudo;
    }

    public static MIPSMnemonic fromString(String mnemonic) {

        if (mnemonic.equalsIgnoreCase("ADD")) {
            return I_ADD;
        }

        else if (mnemonic.equalsIgnoreCase("LI")) {
            return I_LI;
        }

        throw new RuntimeException("Unknown instruction: \"" + mnemonic + "\"");
    }

}