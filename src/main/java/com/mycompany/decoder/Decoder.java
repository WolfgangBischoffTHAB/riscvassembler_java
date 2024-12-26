package com.mycompany.decoder;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class Decoder {

    private static final int R_TYPE = 0b0110011;

    private static final int I_TYPE = 0b1100111;
    private static final int I_TYPE_2 = 0b0010011;
    
    private static final int B_TYPE = 0b1100011;

    private static final int J_TYPE = 0b1101111;

    public static AsmLine decode(final int data) {

        int opcode = data & 0b1111111;
        int funct3 = (data >> 12) & 0b111;
        int funct7 = (data >> 25) & 0b1111111;
        int imm11_0 = (data >> 20) & 0b11111111111111111111;
        int imm_11 = 0;
        int imm = 0;

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        AsmLine asmLine = new AsmLine();

        switch (opcode) {

            case R_TYPE:
                switch (funct3) {
                    case 0b000:
                        switch (funct7) {
                            case 0b0000000:
                                asmLine.mnemonic = Mnemonic.I_ADD;
                                break;
                            case 0b0100000:
                                asmLine.mnemonic = Mnemonic.I_SUB;
                                break;
                            default:
                                throw new RuntimeException("Unknown funct7: " + funct7);
                        }
                    break;

                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                break;

            case I_TYPE_2:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_ADDI;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeIType(asmLine, funct3, funct7, rd, rs1, imm11_0);
                break;
                
            case I_TYPE:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_JALR;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeIType(asmLine, funct3, funct7, rd, rs1, imm11_0);
                break;

            case B_TYPE:
                switch (funct3) {
                    case 0b101:
                        asmLine.mnemonic = Mnemonic.I_BGE;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }

                int imm_12 = (data >> 31) & 0b1;
                imm_11 = (data >> 7) & 0b1;
                int imm_10_5 = (data >> 25) & 0b1111111;
                int imm4_1 = (data >> 8) & 0b1111;

                imm = (imm_12 << 11) | (imm_11 << 10) | (imm_10_5) << 5 | (imm4_1 << 1);
                decodeBType(asmLine, funct3, funct7, rs1, rs2, imm);
                break;

            case J_TYPE:
                int imm_19_12 = (data >> 12) & 0b11111111;
                imm_11 = (data >> 20) & 0b1;
                int imm_10_1 = (data >> 21) & 0b1111111111;
                int imm_20 = (data >> 30) & 0b1;

                imm = (imm_20 << 20) | (imm_19_12 << 12) | (imm_11) << 11 | (imm_10_1 << 1);
                decodeJType(asmLine, rd, imm);
                
                asmLine.mnemonic = Mnemonic.I_JAL;
                break;
            }
    
            return asmLine;
        }
                
    private static void decodeIType(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm11_0) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.numeric_2 = (long) imm11_0;
    }

    private static void decodeRType(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int rs2) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.register_2 = Register.fromInt(rs2);
    }

    private static void decodeBType(AsmLine asmLine, int funct3, int funct7, int rs1, int rs2, int imm) {
        asmLine.register_0 = Register.fromInt(rs1);
        asmLine.register_1 = Register.fromInt(rs2);
        asmLine.numeric_2 = (long) imm;
    }

    private static void decodeJType(AsmLine asmLine, int rd, int imm) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.numeric_1 = (long) imm;
    }

}
