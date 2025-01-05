package com.mycompany.decoder;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.Register;

public class Decoder {

    private static final int R_TYPE = 0b0110011;

    private static final int I_TYPE_1 = 0b1100111;
    private static final int I_TYPE_2 = 0b0010011;
    private static final int I_TYPE_3 = 0b0000011;

    private static final int S_TYPE = 0b0100011;

    private static final int B_TYPE = 0b1100011;

    private static final int J_TYPE = 0b1101111;

    public static AsmLine decode(final int data) {

        // DEBUG
        System.out.println("Decoding HEX: " + ByteArrayUtil.intToHex("%08x", data));

        int opcode = data & 0b1111111;
        int funct3 = (data >> 12) & 0b111;
        int funct7 = (data >> 25) & 0b1111111;
        int imm_11_0 = (data >> 20) & 0b11111111111111111111;
        int imm_11 = 0;
        int imm = 0;

        int imm_4_0 = (data >> 7) & 0b11111;
        int imm_11_5 = (data >> 25) & 0b1111111;

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        AsmLine asmLine = new AsmLine();

        switch (opcode) {

            case R_TYPE:
                switch (funct7) {

                    case 0b0000000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_ADD;
                                break;

                            case 0b010:
                                asmLine.mnemonic = Mnemonic.I_SLT;
                                break;

                            case 0b110:
                                asmLine.mnemonic = Mnemonic.I_OR;
                                break;

                            case 0b111:
                                asmLine.mnemonic = Mnemonic.I_AND;
                                break;

                            default:
                                throw new RuntimeException("Unknown funct7: " + funct7);
                        }
                        break;

                    case 0b0100000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_SUB;
                                break;

                            default:
                                throw new RuntimeException("Unknown funct3: " + funct7);
                        }
                        break;

                    default:
                        throw new RuntimeException("Unknown funct7: " + funct3);
                }
                decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                break;

            case I_TYPE_1:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_JALR;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeIType_1(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case I_TYPE_2:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_ADDI;
                        break;

                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case I_TYPE_3:
                switch (funct3) {
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_LW;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }
                decodeIType_3(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case S_TYPE:
                switch (funct3) {
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_SW;
                        break;
                    default:
                        throw new RuntimeException("Unknown funct3: " + funct3);
                }

                imm = (imm_11_5 << 5) | (imm_4_0 << 0);
                decodeSType(asmLine, funct3, funct7, rs1, rs2, imm);
                break;

            case B_TYPE:
                switch (funct3) {

                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_BEQ;
                        break;

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

            default:
                throw new RuntimeException("Unknown Instruction Type!");
        }

        return asmLine;
    }

    private static void decodeIType_1(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.numeric_2 = (long) imm;
    }

    private static void decodeIType_2(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.numeric_2 = (long) imm;
    }

    /**
     * I think I_TYPE type_3 means, that the immediate is an offset
     * to register 1
     *
     * @param asmLine
     * @param funct3
     * @param funct7
     * @param rd
     * @param rs1
     * @param imm
     */
    private static void decodeIType_3(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {
        asmLine.register_0 = Register.fromInt(rd);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.offset_1 = (long) imm;
    }

    private static void decodeSType(AsmLine asmLine, int funct3, int funct7, int rs1, int rs2, int imm) {
        asmLine.register_0 = Register.fromInt(rs2);
        asmLine.register_1 = Register.fromInt(rs1);
        asmLine.offset_1 = (long) imm;
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
