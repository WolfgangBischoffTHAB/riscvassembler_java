package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class MnemonicEncoder {

    public void encodeMnemonic(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        switch (asmLine.mnemonic) {

            case I_ADDI:
                encodeADDI(byteArrayOutStream, asmLine);
                break;

            case I_SW:
                encodeSW(byteArrayOutStream, asmLine);
                break;

            case I_SB:
                encodeSB(byteArrayOutStream, asmLine);
                break;

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }
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

    private void encodeSB(ByteArrayOutputStream byteArrayOutStream, AsmLine asmLine) {
        byte funct3 = 0b000;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encode_s_type(imm, rs2, rs1, funct3, opcode);

        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private void encodeSW(ByteArrayOutputStream byteArrayOutStream, AsmLine asmLine) {
        byte funct3 = 0b010;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.ordinal();
        byte rs1 = (byte) asmLine.register_1.ordinal();
        short imm = asmLine.offset_1.shortValue();

        int result = encode_s_type(imm, rs2, rs1, funct3, opcode);

        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);
    }

    private int encodeIType(short imm, byte rs1, byte funct3, byte rd, byte opcode) {

        int result = ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((imm & 0b111111111111) << (7 + 5 + 3 + 5));

        return result;
    }

    private int encode_s_type(short imm, byte rs2, byte rs1, byte funct3, byte opcode) {

        int imm_4_0 = (imm >> 0) & 0b11111;
        int imm_11_5 = (imm >> 5) & 0b1111111;

        int result = ((opcode & 0b1111111) << 0) |
               ((imm_4_0) << 7) |
               ((funct3 & 0b111) << (7+5)) |
               ((rs1 & 0b11111) << (7+5+3)) |
               ((rs2 & 0b11111) << (7+5+3+5)) |
               ((imm_11_5 & 0b1111111) << (7+5+3+5+5));

        return result;
    }

}
