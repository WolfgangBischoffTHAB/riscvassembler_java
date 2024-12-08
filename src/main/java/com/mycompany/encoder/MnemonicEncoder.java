package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Register;

public class MnemonicEncoder {

    public void encodeMnemonic(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        switch (asmLine.mnemonic) {

            // case I_AUIPC:
            // encoded_asm_line = encode_auipc(byteArrayOutStream, asmLine);
            // break;

            case I_ADD:
                encodeADD(byteArrayOutStream, asmLine);
                break;

            case I_ADDI:
                encodeADDI(byteArrayOutStream, asmLine);
                break;

            // // ADDIW is part of RV64I not RV32I. Only generate this instruction if the
            // extension RV64I is enabled !!!
            case I_ADDIW:
                encodeADDIW(byteArrayOutStream, asmLine);
                break;

            case I_AND:
                encodeAND(byteArrayOutStream, asmLine);
                break;

            case I_ANDI:
                encodeANDI(byteArrayOutStream, asmLine);
                break;

            // case I_MUL:
            // encoded_asm_line = encode_mul(byteArrayOutStream, asmLine);
            // break;

            case I_BEQ:
                encodeBEQ(byteArrayOutStream, asmLine);
                break;

            case I_BNE:
            encodeBNE(byteArrayOutStream, asmLine);
            break;

            // case I_BGE:
            // encoded_asm_line = encode_bge(byteArrayOutStream, asmLine);
            // break;

            // case I_BLT:
            // encoded_asm_line = encode_blt(byteArrayOutStream, asmLine);
            // break;

            // case I_ECALL:
            // encoded_asm_line = encode_ecall(byteArrayOutStream, asmLine);
            // break;

            case I_JAL:
                encodeJAL(byteArrayOutStream, asmLine);
                break;

            case I_JALR:
                encodeJALR(byteArrayOutStream, asmLine);
                break;

            case I_LUI:
                encodeLUI(byteArrayOutStream, asmLine);
                break;

            // case I_LB:
            // encoded_asm_line = encode_lb(byteArrayOutStream, asmLine);
            // break;

            case I_LBU:
                encodeLBU(byteArrayOutStream, asmLine);
                break;

            // case I_LH:
            // encoded_asm_line = encode_lh(byteArrayOutStream, asmLine);
            // break;

            case I_LW:
                encodeLW(byteArrayOutStream, asmLine);
                break;

            // case I_LD:
            // encoded_asm_line = encode_ld(byteArrayOutStream, asmLine);
            // break;

            // case I_SRLI:
            // encoded_asm_line = encode_srli(byteArrayOutStream, asmLine);
            // break;

            case I_SLLI:
                encodeSLLI(byteArrayOutStream, asmLine);
                break;

            // case I_SD:
            // encoded_asm_line = encode_sd(byteArrayOutStream, asmLine);
            // break;

            case I_SW:
                encodeSW(byteArrayOutStream, asmLine);
                break;

            // case I_SH:
            // encoded_asm_line = encode_sh(byteArrayOutStream, asmLine);
            // break;

            case I_SB:
                encodeSB(byteArrayOutStream, asmLine);
                break;

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }
    }

    private void encodeADD(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        byte funct7 = 0b0000000;
        byte funct3 = 0b000;
        byte opcode = 0b0110011;

        byte rd = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        byte rs2 = (byte) asmLine.register_2.ordinal();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeADDI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b0010011;
        byte rd = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeADDIW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b0011011;

        byte rd = (byte) asmLine.register_0.ordinal();
        byte rs1 =  (byte) asmLine.register_1.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeAND(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct7 = 0b0000000;
        byte funct3 = 0b111;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.ordinal();
        byte rs2 = (byte) asmLine.register_2.ordinal();
        byte rd = (byte) asmLine.register_0.ordinal();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeANDI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b111;
        byte opcode = 0b0010011;

        byte rs1 = (byte) asmLine.register_1.ordinal();
        byte rd = (byte) asmLine.register_0.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeBEQ(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.ordinal();
        byte rs2 = (byte) asmLine.register_2.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeBNE(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b001;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.ordinal();
        ///byte rs2 = (byte) asmLine.register_2.ordinal();
        byte rs2 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeJAL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte opcode = 0b1101111;

        byte rd = (byte) asmLine.register_0.ordinal();
        int imm = asmLine.numeric_1.intValue();

        int result = encodeJType(imm, rd, opcode);

        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeJALR(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b1100111;

        byte rd = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = 0;
        if (asmLine.numeric_2 != null) {
            imm = asmLine.numeric_2.shortValue();
        } else if (asmLine.offset_1 != null) {
            imm = asmLine.offset_1.shortValue();
        }

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeLUI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte opcode = 0b0110111;
        byte rd = (byte) asmLine.register_0.ordinal();
        int imm = asmLine.numeric_1.shortValue();

        int result = encodeUType(imm, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeLBU(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b100;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.ordinal();
        byte rd = (byte) asmLine.register_0.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeSB(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeSType(imm, rs2, rs1, funct3, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeLW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b010;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.ordinal();
        byte rd = (byte) asmLine.register_0.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeSLLI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b001;
        byte opcode = 0b0010011;

        byte rd = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeSW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {
        byte funct3 = 0b010;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeSType(imm, rs2, rs1, funct3, opcode);
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private int encodeRType(byte funct7, byte rs2, byte rs1, byte funct3, byte rd, byte opcode) {

        return ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((rs2 & 0b11111) << (7 + 5 + 3 + 5)) |
                ((funct7 & 0b1111111) << (7 + 5 + 3 + 5 + 5));
    }

    private int encodeIType(short imm, byte rs1, byte funct3, byte rd, byte opcode) {

        return ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((imm & 0b111111111111) << (7 + 5 + 3 + 5));
    }

    private int encodeSType(short imm, byte rs2, byte rs1, byte funct3, byte opcode) {

        int imm_4_0 = (imm >> 0) & 0b11111;
        int imm_11_5 = (imm >> 5) & 0b1111111;

        int result = ((opcode & 0b1111111) << 0) |
                ((imm_4_0) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((rs2 & 0b11111) << (7 + 5 + 3 + 5)) |
                ((imm_11_5 & 0b1111111) << (7 + 5 + 3 + 5 + 5));

        return result;
    }

    private int encodeBType(short imm, byte rs2, byte rs1, byte funct3, byte opcode) {

        int imm_11 = (imm >> 10) & 0b1;
        int imm_4_1 = (imm >> 1) & 0b1111;
        int imm_10_5 = (imm >> 5) & 0b111111;
        int imm_12 = (imm >> 11) & 0b1;

        return ((opcode & 0b1111111) << 0) |
                ((imm_11) << 7) |
                ((imm_4_1) << (7 + 1)) |
                ((funct3 & 0b111) << (7 + 1 + 4)) |
                ((rs1 & 0b11111) << (7 + 1 + 4 + 3)) |
                ((rs2 & 0b11111) << (7 + 1 + 4 + 3 + 5)) |
                ((imm_10_5 & 0b111111) << (7 + 1 + 4 + 3 + 5 + 5)) |
                ((imm_12 & 0b1) << (7 + 1 + 4 + 3 + 5 + 5 + 6));

    }

    private int encodeUType(int imm, byte rd, byte opcode) {

        return ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((imm & 0b11111111111111111111) << (7 + 5));
    }

    private int encodeJType(int imm, byte rd, byte opcode) {

        int imm_20 = (imm >> 19) & 0b1; // 1
        int imm_10_1 = (imm >> 1) & 0b1111111111; // 10
        int imm_11 = (imm >> 10) & 0b1; // 1
        int imm_19_12 = (imm >> 11) & 0b11111111; // 8

        return ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((imm_19_12 & 0b11111111) << (7 + 5)) |
                ((imm_11 & 0b1) << (7 + 5 + 8)) |
                ((imm_10_1 & 0b1111111111) << (7 + 5 + 8 + 1)) |
                ((imm_20 & 0b1) << (7 + 5 + 8 + 1 + 10));
    }

}
