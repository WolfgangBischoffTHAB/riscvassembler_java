package com.mycompany.data;

public enum Mnemonic {

    I_ADD,
    I_ADDI,
    I_AND,
    I_ANDI,
    I_AUIPC,

    I_BEQ,
    I_BEQZ,
    I_BGE,
    I_BGT,
    I_BLT,
    I_BNE,
    I_BNEZ,

    I_CALL,
    I_ECALL,

    I_J,
    I_JR,
    I_JALR,

    I_LA,
    I_LD,
    I_LW,
    I_LH,
    I_LB,
    I_LBU,
    I_LI,
    I_LUI,

    I_MUL,
    I_MV,

    I_NOP,
    I_NOT,

    I_RET,

    I_SRLI,
    I_SLLI,
    I_SUB,
    I_SD,
    I_SW,
    I_SH,
    I_SB,

    I_WFI,

    I_XORI;

    public static Mnemonic fromString(final String mnemonic) {

        if (mnemonic.equalsIgnoreCase("ADD")) {
            return I_ADD;
        } else if (mnemonic.equalsIgnoreCase("ADDI")) {
            return I_ADDI;
        } else if (mnemonic.equalsIgnoreCase("AND")) {
            return I_AND;
        } else if (mnemonic.equalsIgnoreCase("ANDI")) {
            return I_ANDI;
        } else if (mnemonic.equalsIgnoreCase("AUIPC")) {
            return I_AUIPC;
        } else if (mnemonic.equalsIgnoreCase("BEQ")) {
            return I_BEQ;
        } else if (mnemonic.equalsIgnoreCase("BEQZ")) {
            return I_BEQZ;
        } else if (mnemonic.equalsIgnoreCase("BGE")) {
            return I_BGE;
        } else if (mnemonic.equalsIgnoreCase("BGT")) {
            return I_BGT;
        } else if (mnemonic.equalsIgnoreCase("BLT")) {
            return I_BLT;
        } else if (mnemonic.equalsIgnoreCase("BNE")) {
            return I_BNE;
        } else if (mnemonic.equalsIgnoreCase("BNEZ")) {
            return I_BNEZ;
        } else if (mnemonic.equalsIgnoreCase("BEQZ")) {
            return I_BEQZ;
        } else if (mnemonic.equalsIgnoreCase("CALL")) {
            return I_CALL;
        } else if (mnemonic.equalsIgnoreCase("ECALL")) {
            return I_ECALL;
        } else if (mnemonic.equalsIgnoreCase("J")) {
            return I_J;
        } else if (mnemonic.equalsIgnoreCase("JR")) {
            return I_JR;
        } else if (mnemonic.equalsIgnoreCase("JALR")) {
            return I_JALR;
        } else if (mnemonic.equalsIgnoreCase("LA")) {
            return I_LA;
        } else if (mnemonic.equalsIgnoreCase("LD")) {
            return I_LD;
        } else if (mnemonic.equalsIgnoreCase("LW")) {
            return I_LW;
        } else if (mnemonic.equalsIgnoreCase("LH")) {
            return I_LH;
        } else if (mnemonic.equalsIgnoreCase("LB")) {
            return I_LB;
        } else if (mnemonic.equalsIgnoreCase("LBU")) {
            return I_LBU;
        } else if (mnemonic.equalsIgnoreCase("LI")) {
            return I_LI;
        } else if (mnemonic.equalsIgnoreCase("LUI")) {
            return I_LUI;
        } else if (mnemonic.equalsIgnoreCase("MUL")) {
            return I_MUL;
        } else if (mnemonic.equalsIgnoreCase("MV")) {
            return I_MV;
        } else if (mnemonic.equalsIgnoreCase("NOP")) {
            return I_NOP;
        } else if (mnemonic.equalsIgnoreCase("NOT")) {
            return I_NOT;
        } else if (mnemonic.equalsIgnoreCase("RET")) {
            return I_RET;
        } else if (mnemonic.equalsIgnoreCase("SRLI")) {
            return I_SRLI;
        } else if (mnemonic.equalsIgnoreCase("SLLI")) {
            return I_SLLI;
        } else if (mnemonic.equalsIgnoreCase("SUB")) {
            return I_SUB;
        } else if (mnemonic.equalsIgnoreCase("SD")) {
            return I_SD;
        } else if (mnemonic.equalsIgnoreCase("SW")) {
            return I_SW;
        } else if (mnemonic.equalsIgnoreCase("SH")) {
            return I_SH;
        } else if (mnemonic.equalsIgnoreCase("SB")) {
            return I_SB;
        } else if (mnemonic.equalsIgnoreCase("WFI")) {
            return I_WFI;
        } else if (mnemonic.equalsIgnoreCase("XORI")) {
            return I_XORI;
        }

        throw new RuntimeException("Unknown instruction: \"" + mnemonic + "\"");
    }

    public static String toString(final Mnemonic mnemonic) {

        switch (mnemonic) {
            case I_ADD:
                return "add";
            case I_ADDI:
                return "addi";
            case I_AND:
                return "and";
            case I_ANDI:
                return "andi";
            case I_AUIPC:
                return "auipc";

            case I_BEQ:
                return "beq";
            case I_BEQZ:
                return "beqz";
            case I_BGE:
                return "bge";
            case I_BGT:
                return "bgt";
            case I_BLT:
                return "blt";
            case I_BNE:
                return "bne";
            case I_BNEZ:
                return "bnez";

            case I_CALL:
                return "call";
            case I_ECALL:
                return "ecall";

            case I_J:
                return "j";
            case I_JR:
                return "jr";
            case I_JALR:
                return "jalr";

            case I_LA:
                return "la";
            case I_LD:
                return "ld";
            case I_LW:
                return "lw";
            case I_LH:
                return "lh";
            case I_LB:
                return "lb";
            case I_LBU:
                return "lbu";
            case I_LI:
                return "li";
            case I_LUI:
                return "lui";

            case I_MUL:
                return "mul";
            case I_MV:
                return "mv";

            case I_NOP:
                return "nop";
            case I_NOT:
                return "not";

            case I_RET:
                return "ret";

            case I_SRLI:
                return "srli";
            case I_SLLI:
                return "slli";
            case I_SUB:
                return "sub";
            case I_SD:
                return "sd";
            case I_SW:
                return "sw";
            case I_SH:
                return "sh";
            case I_SB:
                return "sb";

            case I_WFI:
                return "wfi";

            case I_XORI:
                return "xori";

            default:
                throw new RuntimeException("Unknown instruction: \"" + mnemonic + "\"");
        }
    }

}
