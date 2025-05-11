package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;

/**
 * Encodes RISCV mnemonics (add, sub, ...) but no assembler instructions (.BYTE,
 * .WORD, ...)
 */
public class RISCVMnemonicEncoder implements MnemonicEncoder {

    @SuppressWarnings("unused")
    private static final boolean USE_64_BIT = false;

    public int encodeMnemonic(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap, final long currentAddress)
            throws IOException {

        if (asmLine.pseudoInstructionAsmLine != null) {
            System.out.println(currentAddress + " -> " + asmLine.pseudoInstructionAsmLine);
            addressSourceAsmLineMap.put(currentAddress, asmLine.pseudoInstructionAsmLine);
        } else {
            System.out.println(currentAddress + " -> " + asmLine);
            addressSourceAsmLineMap.put(currentAddress, asmLine);
        }

        switch (asmLine.mnemonic) {

            case I_AUIPC:
                return encodeAUIPC(byteArrayOutStream, asmLine, labelAddressMap, currentAddress);

            case I_ADD:
                return encodeADD(byteArrayOutStream, asmLine);

            case I_ADDI:
                return encodeADDI(byteArrayOutStream, asmLine, labelAddressMap, currentAddress);

            // // ADDIW is part of RV64I not RV32I. Only generate this instruction if the
            // // extension RV64I is enabled !!!
            // case I_ADDIW:
            // if (!USE_64_BIT) {
            // throw new RuntimeException("64 bit not supported!");
            // }
            // return encodeADDIW(byteArrayOutStream, asmLine);

            case I_AND:
                return encodeAND(byteArrayOutStream, asmLine);

            case I_ANDI:
                return encodeANDI(byteArrayOutStream, asmLine);

            case I_MUL:
                return encodeMUL(byteArrayOutStream, asmLine);

            case I_BEQ:
                return encodeBEQ(byteArrayOutStream, asmLine);

            case I_BNE:
                return encodeBNE(byteArrayOutStream, asmLine);

            case I_BGE:
                return encodeBGE(byteArrayOutStream, asmLine);

            case I_BLT:
                return encodeBLT(byteArrayOutStream, asmLine);

            case I_BLTU:
                return encodeBLTU(byteArrayOutStream, asmLine);

            case I_BRK:
                // custom breakpoint instruction
                return encodeBRK(byteArrayOutStream, asmLine);

            case I_ECALL:
                return encodeECALL(byteArrayOutStream, asmLine);

            case I_JAL:
                return encodeJAL(byteArrayOutStream, asmLine);

            case I_JALR:
                return encodeJALR(byteArrayOutStream, asmLine);

            case I_LUI:
                return encodeLUI(byteArrayOutStream, asmLine);

            case I_LB:
                return encodeLB(byteArrayOutStream, asmLine);

            case I_LBU:
                return encodeLBU(byteArrayOutStream, asmLine);

            // case I_LH:
            // encoded_asm_line = encode_lh(byteArrayOutStream, asmLine);
            // break;

            case I_LW:
                return encodeLW(byteArrayOutStream, asmLine);

            // // THIS IS 64 BIT !!!
            // case I_LD:
            // if (!USE_64_BIT) {
            // throw new RuntimeException("64 bit not supported!");
            // }
            // return encodeLD(byteArrayOutStream, asmLine);

            case I_OR:
                return encodeOR(byteArrayOutStream, asmLine);

            case I_SRA:
                return encodeSRA(byteArrayOutStream, asmLine);

            case I_SRAI:
                return encodeSRAI(byteArrayOutStream, asmLine);

            case I_SRL:
                return encodeSRL(byteArrayOutStream, asmLine);

            case I_SRLI:
                return encodeSRLI(byteArrayOutStream, asmLine);

            case I_SLL:
                return encodeSLL(byteArrayOutStream, asmLine);

            case I_SLLI:
                return encodeSLLI(byteArrayOutStream, asmLine);

            case I_SLT:
                return encodeSLT(byteArrayOutStream, asmLine);

            // // THIS IS 64 BIT !!!
            // case I_SD:
            // if (!USE_64_BIT) {
            // throw new RuntimeException("64 bit not supported!");
            // }
            // return encodeSD(byteArrayOutStream, asmLine);

            case I_SW:
                return encodeSW(byteArrayOutStream, asmLine);

            // case I_SH:
            // encoded_asm_line = encode_sh(byteArrayOutStream, asmLine);
            // break;

            case I_SB:
                return encodeSB(byteArrayOutStream, asmLine);

            case I_SUB:
                return encodeSUB(byteArrayOutStream, asmLine);

            case I_XOR:
                return encodeXOR(byteArrayOutStream, asmLine);

            case I_XORI:
                return encodeXORI(byteArrayOutStream, asmLine);

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }
    }

    private int encodeXOR(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0000000;
        byte funct3 = 0b100;
        byte opcode = 0b0110011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeXORI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {

        byte funct3 = 0b100;
        byte opcode = 0b0010011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeAUIPC(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final long currentAddress) throws IOException {

        if ((asmLine.pseudoInstructionAsmLine != null)
                && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)) {

            final String label = asmLine.offsetLabel_1;

            if (!labelAddressMap.containsKey(label)) {
                throw new RuntimeException(
                        "Trying to lookup label \"" + label + "\" in labelAddressMap but it is not defined!");
            }

            long value = labelAddressMap.get(label);
            // value = 0x10000L;

            // Computation for auipc:
            //
            // data_1 = ((label-.)) >>U 12)
            // ((0x10000 - 4)) >>U 12) = b1111
            //
            // data_2 = ((label-.) & 0x00000800 ? 1 : 0)
            // ((0x10000 - 4) & 0x00000800 ? 1 : 0) is 1
            //
            // in total b1111 + 1 = 16 = 0x10

            long data_1 = ((value - currentAddress) >> 12);
            long data_2 = (((value - currentAddress) & 0x00000800L) != 0) ? 1L : 0L;

            byte opcode = 0b0010111;

            byte rd = (byte) asmLine.register_0.getIndex();
            int imm = (int) (data_1 + data_2);

            int result = encodeUType(imm, rd, opcode);

            System.out.println(asmLine + " -> " + result);
            EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        } else {

            byte opcode = 0b0010111;

            byte rd = (byte) asmLine.register_0.getIndex();
            int imm = asmLine.numeric_1.shortValue();

            int result = encodeUType(imm, rd, opcode);
            System.out.println(asmLine + " -> " + String.format("%08X", result));
            EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        }

        return 4;
    }

    private int encodeADD(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0000000;
        byte funct3 = 0b000;
        byte opcode = 0b0110011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeADDI(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final long currentAddress) throws IOException {

        if ((asmLine.pseudoInstructionAsmLine != null)
                && (asmLine.pseudoInstructionAsmLine.mnemonic == Mnemonic.I_LA)) {

            String label = asmLine.offsetLabel_1;
            if (label == null) {
                label = asmLine.offsetLabel_2;
            }

            Long value = labelAddressMap.get(label);

            long data_1 = 0L;

            // boolean use_formula = false; // works for memory.s
            boolean use_formula = true; // works for hello_world.s
            if (use_formula) {

                // Computation for addi:
                //
                // data_1 = ((label - .) & 0xfff)
                // ((0x10000-4) & 0xfff) = b 1111 1111 1100 = -4

                data_1 = ((value - (currentAddress - 4)) & 0xfff);

            } else {

                // This code fails for hello_world.s

                // this works for the file memory.s on the GNU 32 bit elf toolchain
                // I do not know why the code above was used! Maybe 64 bit or a special
                // example file?!?
                data_1 = value & 0xfff;
            }

            // System.out.println(data_1);

            byte funct3 = 0b000;
            byte opcode = 0b0010011;
            byte rd = (byte) asmLine.register_0.getIndex();
            byte rs1 = (byte) asmLine.register_1.getIndex();
            short imm = (short) data_1;

            int result = encodeIType(imm, rs1, funct3, rd, opcode);

            System.out.println(asmLine + " -> " + result);
            EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        } else {

            byte funct3 = 0b000;
            byte opcode = 0b0010011;
            byte rd = (byte) asmLine.register_0.getIndex();
            byte rs1 = (byte) asmLine.register_1.getIndex();
            short imm = asmLine.numeric_2.shortValue();

            int result = encodeIType(imm, rs1, funct3, rd, opcode);
            System.out.println(asmLine + " -> " + String.format("%08X", result));
            EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        }

        return 4;
    }

    /** This is 64 bit */
    @SuppressWarnings("unused")
    private int encodeADDIW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
        byte funct3 = 0b000;
        byte opcode = 0b0011011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeAND(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0000000;
        byte funct3 = 0b111;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeANDI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
        byte funct3 = 0b111;
        byte opcode = 0b0010011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeMUL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0000001;
        byte funct3 = 0b000;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSUB(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0100000;
        byte funct3 = 0b000;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeBEQ(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b000;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rs2 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        if ((asmLine.register_0 == null) || (asmLine.register_1 == null)) {
            throw new RuntimeException("Register is unknown!");
        }

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeECALL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
        byte funct3 = 0b000;
        byte opcode = 0b1110011;

        byte rs1 = (byte) RISCVRegister.REG_ZERO.getIndex();
        byte rs2 = (byte) RISCVRegister.REG_ZERO.getIndex();
        short imm = 0x00;

        int result = encodeIType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeBNE(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b001;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rs2 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeBGE(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b101;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rs2 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeBLT(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b100;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rs2 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeBLTU(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b110;
        byte opcode = 0b1100011;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rs2 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeBType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * Custom breakpoint instruction
     *
     * @param byteArrayOutStream
     * @param asmLine
     * @return encoded size of the instruction
     */
    private int encodeBRK(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        EncoderUtils.convertToUint32_t(byteArrayOutStream, 0x1f1f1f1f);

        return 4;
    }

    /**
     * rd <- pc + 4
     * pc <- pc + imm20
     *
     * @param byteArrayOutStream
     * @param asmLine
     * @return size of the encoded instruction in bytes
     */
    private int encodeJAL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte opcode = 0b1101111;

        byte rd = (byte) asmLine.register_0.getIndex();
        int imm = asmLine.numeric_1.intValue();

        int result = encodeJType(imm, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeJALR(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {

        byte funct3 = 0b000;
        byte opcode = 0b1100111;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = 0;
        if (asmLine.numeric_2 != null) {
            imm = asmLine.numeric_2.shortValue();
        } else if (asmLine.offset_1 != null) {
            imm = asmLine.offset_1.shortValue();
        }

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeLUI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte opcode = 0b0110111;
        byte rd = (byte) asmLine.register_0.getIndex();
        int imm = asmLine.numeric_1.shortValue();

        int result = encodeUType(imm, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeLB(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b000;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeLBU(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b100;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSB(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b000;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeSType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeLW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b010;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeOR(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0000000;
        byte funct3 = 0b110;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * THIS IS 64 BIT!!!
     *
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     */
    @SuppressWarnings("unused")
    private int encodeLD(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b011;
        byte opcode = 0b0000011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSRA(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0100000;
        byte funct3 = 0b101;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSRAI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
        byte funct3 = 0b101;
        byte opcode = 0b0010011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = (short) (((short) 0b010000000000) + ((short) asmLine.numeric_2.shortValue()));

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSRL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0000000;
        byte funct3 = 0b101;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSRLI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
        byte funct3 = 0b101;
        byte opcode = 0b0010011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = (short) (((short) 0b000000000000) + ((short) asmLine.numeric_2.shortValue()));

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSLL(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0000000;
        byte funct3 = 0b001;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSLLI(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {

        byte funct3 = 0b001;
        byte opcode = 0b0010011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.numeric_2.shortValue();

        int result = encodeIType(imm, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSLT(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0000000;
        byte funct3 = 0b010;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    @SuppressWarnings("unused")
    private int encodeSD(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b011;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeSType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeSW(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct3 = 0b010;
        byte opcode = 0b0100011;
        byte rs2 = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        short imm = asmLine.offset_1.shortValue();

        int result = encodeSType(imm, rs2, rs1, funct3, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeRType(byte funct7, byte rs2, byte rs1, byte funct3, byte rd, byte opcode) throws IOException {

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
