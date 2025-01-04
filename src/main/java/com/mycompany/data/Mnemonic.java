package com.mycompany.data;

public enum Mnemonic {

    I_ADD(false),
    I_ADDI(false),
    // I_ADDIW(false), // This is 64 bits!!!
    I_AND(false),
    I_ANDI(false),
    I_AUIPC(false),

    I_BEQ(false),
    I_BEQZ(true),
    I_BGE(false),
    I_BGEU(false),
    I_BGEZ(true),
    I_BGT(true),
    I_BGTU(true),
    I_BGTZ(true),
    I_BLT(false),
    I_BLTU(false),
    I_BLTZ(true),
    I_BLE(true),
    I_BLEU(true),
    I_BLEZ(true),
    I_BNE(false),
    I_BNEU(false),
    I_BNEZ(true),

    I_CALL(true),
    I_ECALL(false),

    I_J(true),
    I_JR(false),
    I_JALR(false),
    I_JAL(false),

    I_LA(false),
    // I_LD(false), // This is 64 bits!!!
    I_LW(false),
    I_LH(false),
    I_LB(false),
    I_LBU(false),
    I_LBW(false),
    I_LI(true),
    I_LUI(false),

    I_MUL(false),
    I_MV(true),

    I_NOP(true),
    I_NOT(false),

    I_OR(false),
    I_ORI(false),

    I_RET(false),

    I_SRA(false),
    I_SRAI(false),
    I_SRL(false),
    I_SRLI(false),
    I_SLLI(false),
    I_SLTI(false),
    I_SLTIU(false),
    I_SUB(false),
    I_SLL(false),
    I_SLT(false),
    I_SLTU(false),
    // I_SD(false), // This is 64 bits!!!
    I_SW(false),
    I_SH(false),
    I_SB(false),

    I_WFI(false),

    I_XOR(false),
    I_XORI(false),

    I_UNKNOWN(true);

    private boolean pseudo;

	Mnemonic(final boolean pseudo) {
		this.pseudo = pseudo;
	}

	public boolean isPseudo() {
		return pseudo;
	}

    public static Mnemonic fromString(final String mnemonic) {

        if (mnemonic.equalsIgnoreCase("ADD")) {
            return I_ADD;
        } else if (mnemonic.equalsIgnoreCase("ADDI")) {
            return I_ADDI;
        }
        // else if (mnemonic.equalsIgnoreCase("ADDIW")) {
        //     return I_ADDIW;
        // }
        else if (mnemonic.equalsIgnoreCase("AND")) {
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
        } else if (mnemonic.equalsIgnoreCase("BLE")) {
            return I_BLE;
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
        } else if (mnemonic.equalsIgnoreCase("JAL")) {
            return I_JAL;
        } else if (mnemonic.equalsIgnoreCase("LA")) {
            return I_LA;
        }
        // else if (mnemonic.equalsIgnoreCase("LD")) { // 64 bits!!!
        //     return I_LD;
        // }
        else if (mnemonic.equalsIgnoreCase("LW")) {
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
        } else if (mnemonic.equalsIgnoreCase("OR")) {
            return I_OR;
        } else if (mnemonic.equalsIgnoreCase("ORI")) {
            return I_ORI;
        } else if (mnemonic.equalsIgnoreCase("RET")) {
            return I_RET;
        } else if (mnemonic.equalsIgnoreCase("SRAI")) {
            return I_SRAI;
        } else if (mnemonic.equalsIgnoreCase("SRLI")) {
            return I_SRLI;
        } else if (mnemonic.equalsIgnoreCase("SLLI")) {
            return I_SLLI;
        } else if (mnemonic.equalsIgnoreCase("SUB")) {
            return I_SUB;
        }
        // else if (mnemonic.equalsIgnoreCase("SD")) { // 64 bits !!!
        //     return I_SD;
        // }
        else if (mnemonic.equalsIgnoreCase("SW")) {
            return I_SW;
        } else if (mnemonic.equalsIgnoreCase("SH")) {
            return I_SH;
        } else if (mnemonic.equalsIgnoreCase("SB")) {
            return I_SB;
        } else if (mnemonic.equalsIgnoreCase("SLT")) {
            return I_SLT;
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
            // case I_ADDIW: // This is 64 bits!
            //     return "addiw";
            case I_AND:
                return "and";
            case I_ANDI:
                return "andi";
            case I_AUIPC:
                return "auipc";

            // Note: BGT, BGTU, BLE, and BLEU can be synthesized by reversing the operands
            //    to BLT, BLTU, BGE, and BGEU, respectively.
            //
            // https://docs.openhwgroup.org/projects/cva6-user-manual/01_cva6_user/RISCV_Instructions_RV32I.html

            case I_BEQ:         // Branch Equal
                return "beq";
            // case I_BEQU:        // does not exist!
            //     return "bequ";
            case I_BEQZ:        // pseudo instruction (Branch if == zero)
                return "beqz";

            case I_BGE:
                return "bge";
            case I_BGEU:        // Branch Greater or Equal Unsigned
                return "bgeu";
            case I_BGEZ:        // pseudo instruction
                return "bgez";

            case I_BGT:         // pseudo instruction, synthesized via BLT!
                 return "bgt";
            case I_BGTU:        // pseudo instruction, synthesized by BLTU
                return "bgtu";
            case I_BGTZ:
                return "bgtz";  // pseudo instruction

            case I_BLE:         // pseudo instruction (synthesized by BGE)
                return "ble";
            case I_BLEU:        // pseudo instruction (synthesized by BGEU)
                return "bleu";
            case I_BLEZ:        // pseudo instruction
                return "blez";

            case I_BLT:
                return "blt";
            case I_BLTU:        // Branch Less Than Unsigned
                return "bltu";
            case I_BLTZ:        // pseudo instruction
                return "bltz";

            case I_BNE:
                return "bne";
            case I_BNEU:        // pseudo instruction
                return "bneu";
            case I_BNEZ:        // pseudo instruction
                return "bnez";

            case I_CALL:        // pseudo instruction
                return "call";
            case I_ECALL:
                return "ecall";

            case I_J:           // pseudo instruction
                return "j";
            case I_JR:
                return "jr";
            case I_JALR:
                return "jalr";
            case I_JAL:
                return "jal";

            case I_LA:          // pseudo instruction
                return "la";

            // // This is 64 bits!
            // case I_LD:          // pseudo instruction
            //     return "ld";
            case I_LW:          // pseudo instruction
                return "lw";
            case I_LH:          // pseudo instruction
                return "lh";
            case I_LB:          // pseudo instruction
                return "lb";
            case I_LBU:
                return "lbu";
            case I_LI:          // pseudo instruction
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

            case I_OR:
                return "or";
            case I_ORI:
                return "ori";

            case I_RET:         // pseudo instruction
                return "ret";

            case I_SRAI:
                return "srai";
            case I_SRLI:
                return "srli";
            case I_SLLI:
                return "slli";
            case I_SUB:
                return "sub";
            // // This is 64 bits!
            // case I_SD:          // pseudo instruction
            //     return "sd";
            case I_SW:          // pseudo instruction
                return "sw";
            case I_SH:          // pseudo instruction
                return "sh";
            case I_SB:          // pseudo instruction
                return "sb";
            case I_SLT:         // https://www.reddit.com/r/RISCV/comments/1bi03h4/whats_the_purpose_of_sltslti/?rdt=37367
                return "slt";

            case I_WFI:
                return "wfi";

            case I_XORI:
                return "xori";

            default:
                throw new RuntimeException("Unknown instruction: \"" + mnemonic + "\"");
        }
    }

}
