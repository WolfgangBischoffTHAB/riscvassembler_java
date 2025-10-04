package com.mycompany.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Decoder {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    private static final int R_TYPE = 0b0110011;

    private static final int I_TYPE_1 = 0b1100111;
    private static final int I_TYPE_2 = 0b0010011;
    private static final int I_TYPE_3 = 0b0000011;
    private static final int I_TYPE_4 = 0b1110011;

    private static final int S_TYPE = 0b0100011;

    private static final int B_TYPE = 0b1100011;

    private static final int U_TYPE_1 = 0b0010111;
    private static final int U_TYPE_2 = 0b0110111;

    private static final int J_TYPE = 0b1101111;

    /**
     * decodes four byte of machine code into a ASMLine domain model
     * 
     * @param data machine code
     * @return ASMLine object with decoded information
     */
    public static AsmLine<?> decode(final int data) {

        AsmLine<Register> asmLine = new AsmLine<>();

        // decode 0 to NOP
        if (data == 0) {
            asmLine.mnemonic = Mnemonic.I_NOP;
            return asmLine;
        }

        // detect custom breakpoint instruction
        if (data == 0x1f1f1f1f) {
            asmLine.mnemonic = Mnemonic.I_BRK;
            return asmLine;
        }

        // emulator extension PUTS
        if (data == 0x11111111) {
            asmLine.mnemonic = Mnemonic.I_PUTS;
            return asmLine;
        }

        // DEBUG
        //logger.info("Decoding HEX: " + ByteArrayUtil.intToHex("%08x", data));

        int opcode = data & 0b1111111;
        int funct3 = (data >> 12) & 0b111;
        int funct7 = (data >> 25) & 0b1111111;
        int imm_11_0 = (data >> 20) & 0b11111111111111111111;
        int imm_11 = 0;
        int imm = 0;

        int imm_4_0 = (data >> 7) & 0b11111;
        int imm_11_5 = (data >> 25) & 0b1111111;

        int imm_31_12 = (data >> 12) & 0b11111111111111111111;

        int shamt = (data >> 20) & 0b11111;

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        switch (opcode) {

            case R_TYPE:
                switch (funct7) {

                    case 0b0000000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_ADD;
                                break;

                            case 0b001:
                                asmLine.mnemonic = Mnemonic.I_SLL;
                                break;

                            case 0b010:
                                asmLine.mnemonic = Mnemonic.I_SLT;
                                break;

                            case 0b100:
                                asmLine.mnemonic = Mnemonic.I_XOR;
                                break;

                            case 0b101:
                                asmLine.mnemonic = Mnemonic.I_SRL;
                                break;

                            case 0b110:
                                asmLine.mnemonic = Mnemonic.I_OR;
                                break;

                            case 0b111:
                                asmLine.mnemonic = Mnemonic.I_AND;
                                break;

                            default:
                                throw new RuntimeException(
                                        "Unknown funct7: " + funct7 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                        }
                        break;

                    case 0b0000001:
                        switch (funct3) {
                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_MUL;
                                break;
                        }
                        break;

                    case 0b0100000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_SUB;
                                break;

                            case 0b101:
                                asmLine.mnemonic = Mnemonic.I_SRA;
                                break;

                            default:
                                throw new RuntimeException(
                                        "Unknown funct3: " + funct7 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                        }
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct7: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                break;

            case I_TYPE_1:
                switch (funct3) {

                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_JALR;
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_1! funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                decodeIType_1(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case I_TYPE_2:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_ADDI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_SLLI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, shamt);
                        break;
                    case 0b100:
                        asmLine.mnemonic = Mnemonic.I_XORI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    case 0b101:
                        asmLine.mnemonic = Mnemonic.I_SRAI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, shamt);
                        break;
                    case 0b111:
                        asmLine.mnemonic = Mnemonic.I_ANDI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_2! funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                break;

            case I_TYPE_3:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_LB;
                        break;
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_LW;
                        break;
                    case 0b100:
                        asmLine.mnemonic = Mnemonic.I_LBU;
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_3! funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                decodeIType_3(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case I_TYPE_4:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_ECALL;
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_4! funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                // decodeIType_4(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case S_TYPE:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_SB;
                        break;
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_SW;
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }

                imm = (imm_11_5 << 5) | (imm_4_0 << 0);
                decodeSType(asmLine, funct3, funct7, rs1, rs2, imm);
                break;

            case B_TYPE:
                switch (funct3) {

                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_BEQ;
                        break;

                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_BNE;
                        break;

                    case 0b100:
                        asmLine.mnemonic = Mnemonic.I_BLT;
                        break;

                    case 0b101:
                        asmLine.mnemonic = Mnemonic.I_BGE;
                        break;

                    case 0b110:
                        asmLine.mnemonic = Mnemonic.I_BLTU;
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }

                int imm_12 = (data >> 31) & 0b1;
                imm_11 = (data >> 7) & 0b1;
                int imm_10_5 = (data >> 25) & 0b1111111;
                int imm4_1 = (data >> 8) & 0b1111;

                imm = (imm_12 << 11) | (imm_11 << 10) | (imm_10_5) << 5 | (imm4_1 << 1);
                decodeBType(asmLine, funct3, funct7, rs1, rs2, imm);
                break;

            case U_TYPE_1:
                asmLine.mnemonic = Mnemonic.I_AUIPC;
                asmLine.register_0 = RISCVRegister.fromInt(rd);
                imm = imm_31_12;
                asmLine.numeric_1 = (long) imm_31_12;
                break;

            case U_TYPE_2:
                asmLine.mnemonic = Mnemonic.I_LUI;
                asmLine.register_0 = RISCVRegister.fromInt(rd);
                imm = imm_31_12;
                asmLine.numeric_1 = (long) imm_31_12;
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
                throw new RuntimeException("Decoding HEX: " + ByteArrayUtil.intToHex("%08x", data)
                        + ". Unknown Instruction Type! opcode = " + opcode);
     
        }

        return asmLine;
    }

    private static void decodeIType_1(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);
        asmLine.numeric_2 = (long) imm;
    }

    public static int twosComplement(final int value) {
        final int mask = 0xffff_ffff;
        return (value ^ mask) + 1;
    }

    private static void decodeIType_2(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);

        asmLine.numeric_2 = (long) imm;

        // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // the number negative if it was negative
        if ((imm & 0x800) > 0) {
            asmLine.numeric_2 = (long) twosComplement(imm | 0xFFFFF000);
            asmLine.numeric_2 *= -1;
        }
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

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);

        asmLine.offset_1 = (long) imm;

        // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // the number negative if it was negative
        if ((imm & 0x800) > 0) {
            asmLine.offset_1 = (long) twosComplement(imm | 0xFFFFF000);
            asmLine.offset_1 *= -1;
        }
    }

    private static void decodeSType(AsmLine asmLine, int funct3, int funct7, int rs1, int rs2, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rs2);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);

        asmLine.offset_1 = (long) imm;

        // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // the number negative if it was negative
        if ((imm & 0x800) > 0) {
            asmLine.offset_1 = (long) twosComplement(imm | 0xFFFFF000);
            asmLine.offset_1 *= -1;
        }
    }

    private static void decodeRType(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int rs2) {

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);
        asmLine.register_2 = RISCVRegister.fromInt(rs2);
    }

    private static void decodeBType(AsmLine asmLine, int funct3, int funct7, int rs1, int rs2, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rs1);
        asmLine.register_1 = RISCVRegister.fromInt(rs2);

        asmLine.numeric_2 = (long) imm;

        // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // the number negative if it was negative
        if ((imm & 0x800) > 0) {
            asmLine.numeric_2 = (long) twosComplement(imm | 0xFFFFF000);
            asmLine.numeric_2 *= -1;
        }
    }

    private static void decodeJType(AsmLine asmLine, int rd, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.numeric_1 = (long) imm;

        // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // the number negative if it was negative
        if ((imm & 0x800) > 0) {
            asmLine.numeric_1 = (long) twosComplement(imm | 0xFFFFF000);
            asmLine.numeric_1 *= -1;
        }
    }

}
