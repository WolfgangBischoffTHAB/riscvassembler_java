package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.management.RuntimeErrorException;

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

            //
            // RV32I
            //

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

            case I_BEQ:
                return encodeBEQ(byteArrayOutStream, asmLine);

            case I_BNE:
                return encodeBNE(byteArrayOutStream, asmLine);

            case I_BGE:
                return encodeBGE(byteArrayOutStream, asmLine);

            case I_BGEU:
                return encodeBGEU(byteArrayOutStream, asmLine);

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

            //
            // Zicsr Extension
            //

            // I_CSRR(true),
            case I_CSRRS:
                return encodeCSRRS(byteArrayOutStream, asmLine);
            // I_CSRWI(true),
            case I_CSRRWI:
                return encodeCSRRWI(byteArrayOutStream, asmLine);
            // I_CSRW(true),
            case I_CSRRW:
                return encodeCSRRW(byteArrayOutStream, asmLine);
            // I_CSRS(true),
            // I_CSRSI(true),
            case I_CSRRSI:
                return encodeCSRRSI(byteArrayOutStream, asmLine);
            // I_CSRC(true),
            case I_CSRRC:
                return encodeCSRRC(byteArrayOutStream, asmLine);
            // I_CSRCI(true),
            case I_CSRRCI:
                return encodeCSRRCI(byteArrayOutStream, asmLine);

            //
            // M extension
            //

            case I_MUL:
                return encodeMUL(byteArrayOutStream, asmLine);

            case I_DIV:
                return encodeDIV(byteArrayOutStream, asmLine);

            case I_REMU:
                return encodeREMU(byteArrayOutStream, asmLine);

            //
            // V Extension - https://rvv-isadoc.readthedocs.io/en/latest/load_and_store.html
            //

            case I_VSETVLI:
                return encodeVSETVLI(byteArrayOutStream, asmLine);
            case I_VSETVL:
                return encodeVSETVL(byteArrayOutStream, asmLine);
            case I_VLE32_V:
                return encodeVLE32_V(byteArrayOutStream, asmLine);
            case I_VSE32_V:
                return encodeVSE32_V(byteArrayOutStream, asmLine);
            case I_VMSNE_VI: // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
                return encodeVMSNE(byteArrayOutStream, asmLine);
            case I_VADD_VV:
                return encodeVADD(byteArrayOutStream, asmLine);
            case I_VMV_V_I:
                return encodeVMV_V_I(byteArrayOutStream, asmLine);

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }
    }

    private int encodeVMV_V_I(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct3 = 0b011;
        byte opcode = 0b1010111;
        byte upperOpCode = 0b010111;

        byte vd = (byte) asmLine.register_0.getIndex();
        byte vs2 = (byte) 0x00;
        byte imm = (byte) asmLine.numeric_1.byteValue();
        byte vm = (byte) 1;

        int result = encodeVectorArithmeticInstruction(funct3, opcode, upperOpCode, vd, vs2, imm, vm);
        
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vadd
     * vadd.vv vd, vs2, vs1, vm
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException 
     */
    private int encodeVADD(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct3 = 0b111;
        byte opcode = 0b1010111;
        byte upperOpCode = 0b111111;

        byte vd = (byte) asmLine.register_0.getIndex();
        byte vs2 = (byte) asmLine.register_1.getIndex();

        int result = 0;

        // switch (asmLine.mnemonic) {

        //     case I_VADD_VV:
                upperOpCode = 0b000000;
                funct3 = 0b000;
                byte vs1 = (byte) asmLine.register_2.getIndex();
                byte vm = (byte) ((asmLine.register_3 != null) ? 1 : 0);

                result = encodeVectorArithmeticInstruction(funct3, opcode, upperOpCode, vd, vs2, vs1, vm);
        //         break;

        //     default:
        //         throw new RuntimeException("Not implemented yet!");
        // }
        
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeVectorArithmeticInstruction(byte funct3, byte opcode, byte upperOpCode, byte vd, byte vs2, byte vs1, byte vm) {
        int result;
        result = ((opcode & 0b1111111) << 0) |          // 0
            ((vd & 0b11111) << 7) |                     // 7
            ((funct3 & 0b111) << (7 + 5)) |             // 12
            ((vs1 & 0b11111) << (7 + 5 + 3)) |          // 15
            ((vs2 & 0b11111) << (7 + 5 + 3 + 5)) |      // 20
            ((vm & 0b11111) << (7 + 5 + 3 + 5 + 5)) |   // 25
            ((upperOpCode) << (7 + 5 + 3 + 5 + 5 + 1)); // 26
        return result;
    }

    /**
     * https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
     * 
     * vmsne.vv vd, vs2, vs1, vm
     * vmsne.vx vd, vs2, rs1, vm
     * vmsne.vi vd, vs2, imm, vm
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException 
     */
    private int encodeVMSNE(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {
        
        byte funct3 = 0b111;
        byte opcode = 0b1010111;
        byte upperOpCode = 0b111111;

        byte vd = (byte) asmLine.register_0.getIndex();
        byte vs2 = (byte) asmLine.register_1.getIndex();

        int result = 0;

        // switch (asmLine.mnemonic) {

        //     case I_VMSNE_VI:
                upperOpCode = 0b011001;
                funct3 = 0b011;
                byte imm = (byte) asmLine.numeric_2.byteValue();

                byte vm = 0;
                if (asmLine.register_3 != null) {
                    vm = 1;
                }

                result = encodeRVVVectorInstruction(funct3, opcode, upperOpCode, vd, vs2, imm, vm);

        //     default:
        //         throw new RuntimeException("Not implemented yet!");
        // }

        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * https://rvv-isadoc.readthedocs.io/en/latest/load_and_store.html
     * 
     * 
     * 
     * vle8.v vd, (rs1), vm # 8-bit unit-stride load with width == '000'
     * vle16.v vd, (rs1), vm # 16-bit unit-stride load with width == '101'
     * vle32.v vd, (rs1), vm # 32-bit unit-stride load with width == '110'
     * vle64.v vd, (rs1), vm # 64-bit unit-stride load with width == '111'
     * 
     * Option 1:
     * vle32.v v0, 0(a3)
     * 
     * Option 2:
     * vle32.v v2, 0(a2), v0.t
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException
     */
    private int encodeVLE32_V(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct3 = 0b110; // for 32 bit
        byte opcode = 0b0000111;

        // How to encode the offset? Is there an offset allowed?
        long offset = asmLine.offset_1;

        byte vd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();

        // masking (enabled or disabled)
        byte vm = 0;
        byte rs2 = 0;
        boolean has_rs2 = false;
        if (asmLine.register_2 != null) {
            vm = 1;
            has_rs2 = true;
            rs2 = (byte) asmLine.register_2.getIndex();
        }

        int result = ((opcode & 0b1111111) << 0) |
                ((vd & 0b11111) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((vm & 0b1) << (7 + 5 + 3 + 5 + 5));

        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * https://rvv-isadoc.readthedocs.io/en/latest/load_and_store.html#vse-eew
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException 
     */
    private int encodeVSE32_V(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte width = 0b110; // for 32 bit
        byte opcode = 0b0100111;

        byte vs3 = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();

        // masking (enabled or disabled)
        byte vm = 0;
        if (asmLine.register_2 != null) {
            vm = 1;
        }

        int result = ((opcode & 0b1111111) << 0) |      // 0
                ((vs3 & 0b11111) << 7) |                // 7
                ((width & 0b111) << (7 + 5)) |          // 12
                ((rs1 & 0b11111) << (7 + 5 + 3)) |      // 15
                ((0b00000) << (7 + 5 + 3 + 5)) |        // 20
                ((vm & 0b1) << (7 + 5 + 3 + 5 + 5));    // 25

        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    /**
     * vsetvl rd, rs1, rs2 # rd = new vl, rs1 = AVL, rs2 = new vtype value
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException
     */
    private int encodeVSETVL(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct3 = 0b111;
        byte opcode = 0b1010111;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        byte funct7 = 0b1000000;

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeVSETVLI(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte opcode = 0b1010111;
        byte funct3 = 0b111;

        byte rs1 = (byte) asmLine.register_0.getIndex();
        byte rd = (byte) asmLine.register_1.getIndex();

        // mask agnostic
        byte vma = (byte) (asmLine.rvvMask.equalsIgnoreCase("ma") ? 1 : 0);

        // tail agnostic
        byte vta = (byte) (asmLine.rvvTail.equalsIgnoreCase("ta") ? 1 : 0);

        // selected element width (SEW)
        byte sew = 4;
        if (asmLine.rvvSew.equalsIgnoreCase("e8")) {
            sew = 0;
        } else if (asmLine.rvvSew.equalsIgnoreCase("e16")) {
            sew = 1;
        } else if (asmLine.rvvSew.equalsIgnoreCase("e32")) {
            sew = 2;
        } else if (asmLine.rvvSew.equalsIgnoreCase("e64")) {
            sew = 3;
        }

        // register grouping or fractions of register (LMUL)
        byte lmul = 0; // default value
        if (asmLine.rvvLmul != null) {
            if (asmLine.rvvLmul.equalsIgnoreCase("mf8")) { // multiplier fractional
                lmul = 5;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("mf4")) { // multiplier fractional
                lmul = 6;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("mf2")) { // multiplier fractional
                lmul = 7;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("m1")) { // grouped
                lmul = 0;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("m2")) { // grouped
                lmul = 1;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("m4")) { // grouped
                lmul = 2;
            } else if (asmLine.rvvLmul.equalsIgnoreCase("m8")) { // grouped
                lmul = 3;
            }
        }

        // I do not know how zimm is defined. The RISCV V-Extension spec does not say!
        // For now, I assume zimm is vtypei. vtypei is defined.
        int zimm_10_0 = ((vma & 0b1) << 7) | ((vta & 0b1) << 6) | ((sew & 0b111) << 3) | ((lmul & 0b111) << 0);

        byte vill = 0b0; // illegal bit ??? What does it do? I think since
        // vsetvli, vsetivli and vsetvl all have the same opcode and funct3,
        // the uppermost bits (including vill) are used to distinguish vsetvli, vsetivli
        // and vsetvl

        int result = ((vill & 0b1) << 31) | ((zimm_10_0 & 0b111_1111_1111) << 20) | ((rs1 & 0b11111) << 15)
                | ((funct3 & 0b111) << 12) | ((rd & 0b11111) << 7) | ((opcode & 0b1111111) << 0);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeCSRRCI(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encodeCSRRCI'");
    }

    private int encodeCSRRC(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encodeCSRRC'");
    }

    private int encodeCSRRSI(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encodeCSRRSI'");
    }

    private int encodeCSRRW(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encodeCSRRW'");
    }

    private int encodeCSRRWI(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encodeCSRRWI'");
    }

    /**
     * csrrs rd, csr, rs1
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException
     */
    private int encodeCSRRS(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {

        byte funct3 = 0b010;
        short csr = asmLine.numeric_1.shortValue();
        byte opcode = 0b1110011;

        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeIType(csr, rs2, funct3, rd, opcode);
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

    private int encodeDIV(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine) throws IOException {
        byte funct7 = 0b0000001;
        byte funct3 = 0b100;
        byte opcode = 0b0110011;

        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();
        byte rd = (byte) asmLine.register_0.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeREMU(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) throws IOException {

        byte funct7 = 0b0000001;
        byte funct3 = 0b111;
        byte opcode = 0b0110011;

        byte rd = (byte) asmLine.register_0.getIndex();
        byte rs1 = (byte) asmLine.register_1.getIndex();
        byte rs2 = (byte) asmLine.register_2.getIndex();

        int result = encodeRType(funct7, rs2, rs1, funct3, rd, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
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

        // System.out.println(asmLine);

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

    /**
     * bgeu
     * Branch Greater or Equal Unsigned
     * bgeu rs1, rs2, imm
     * if(rs1 ≥ rs2) pc += imm
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @return
     * @throws IOException
     */
    private int encodeBGEU(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {

        byte funct3 = 0b111;
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

    private int encodeBLTU(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine)
            throws IOException {
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
        
        System.out.println(asmLine.toString());

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
