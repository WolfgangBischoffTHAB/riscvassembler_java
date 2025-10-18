package com.mycompany.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;

/**
 * RV32I Base Integer Instruction Set, Version 2.1
 * 
 * Decodes opcodes in defined in
 * https://github.com/riscv/riscv-opcodes/blob/master/extensions/rv_i
 * 
 * https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/chapters/rv32.html
 * 
 * https://riscv.atlassian.net/wiki/spaces/CSC/pages/15761503/Extension+Naming+Convention
 * All RISC-V standard extensions that are not a single capital letter start
 * with either a
 * - S (Privileged ISA extension) or a
 * - Z (Unprivileged ISA extension).
 * 
 * https://riscv.atlassian.net/wiki/spaces/HOME/pages/16154683/RISC-V+extension+and+feature+support+in+the+Open+Source+SW+Ecosystem
 * Prefixes:
 * Extensions with prefix Z...standard user-level
 * Extensions with prefix X...non-standard user-level
 * Extensions with prefix S...standard supervisor-level
 * Extensions with prefix SX...non-standard supervisor-level
 * 
 * OP-CODES are defined in https://github.com/riscv/riscv-opcodes
 * 
 * https://github.com/riscv/riscv-opcodes/blob/master/extensions/rv_i
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class RV32IBaseIntegerInstructionSetDecoder implements Decoder {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    /**  */
    private static final int FENCE_TYPE = 0b0001111;

    /** 0b0110011 */
    private static final int R_TYPE_1 = 0b0110011;

    /** 0b0111011 */
    private static final int R_TYPE_2 = 0b0111011;

    private static final int I_TYPE_1 = 0b1100111;
    private static final int I_TYPE_2 = 0b0010011;
    /** 0b0000011 */
    private static final int I_TYPE_3 = 0b0000011;
    /** 0b1110011 */
    private static final int I_TYPE_4 = 0b1110011;
    /** 0b0011011 = 27dec = 0x1B */
    private static final int I_TYPE_5 = 0b0011011;

    /** 0b0100011 */
    private static final int S_TYPE = 0b0100011;

    private static final int B_TYPE = 0b1100011;

    private static final int U_TYPE_1 = 0b0010111;
    private static final int U_TYPE_2 = 0b0110111;

    private static final int J_TYPE = 0b1101111;

    /** 0b1010111 */
    private static final int V_EXTENSION_OPERATION = 0b1010111;
    /** 0b0000111 */
    private static final int V_EXTENSION_LOAD = 0b0000111;
    /** 0b0100111 */
    private static final int V_EXTENSION_STORE = 0b0100111;

    private int xlen;

    public RV32IBaseIntegerInstructionSetDecoder(int xlen) {
        if ((xlen != 32) && (xlen != 64)) {
            throw new RuntimeException("unknown xlen length!");
        }
        this.xlen = xlen;
    }

    /**
     * decodes four byte of machine code into a ASMLine domain model
     * 
     * @param data machine code
     * @return ASMLine object with decoded information
     */
    public AsmLine<?> decode(final int data) {

        if ((xlen != 32) && (xlen != 64)) {
            throw new RuntimeException("unknown xlen length!");
        }

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

        // // emulator extension PUTS
        // if (data == 0x11111111) {
        //     asmLine.mnemonic = Mnemonic.I_PUTS;
        //     return asmLine;
        // }

        // DEBUG
        if (logger.isInfoEnabled()) {
            logger.info("Decoding HEX: " + ByteArrayUtil.intToHex("%08x", data));
        }

        int opcode = data & 0b1111111;
        int funct3 = (data >> 12) & 0b111;
        int funct7 = (data >> 25) & 0b1111111;
        int imm_11_0 = (data >> 20) & 0b11111111111111111111;
        int imm_11 = 0;
        int imm = 0;

        int imm_4_0 = (data >> 7) & 0b11111;
        int imm_11_5 = (data >> 25) & 0b1111111;

        int imm_31_12 = (data >> 12) & 0b11111111111111111111;
        int imm_31_20 = (data >> 20) & 0b111111111111;

        int shtyp = (data >> 25) & 0b1111111;
        int shamt = (data >> 20) & ((xlen == 32) ? 0b11111 : 0b111111);

        int rd = (data >> 7) & 0b11111;
        int rs1 = (data >> 15) & 0b11111;
        int rs2 = (data >> 20) & 0b11111;

        /**
         * fm mode -
         * https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/fence.html#_defining_extension
         * 0000 - normal fence. 1000 - TSO - With FENCE RW,RW: exclude write-to-read
         * ordering;
         */
        int fm = (data >> 28) & 0b1111;
        int pred = (data >> 24) & 0b1111;
        int succ = (data >> 20) & 0b1111;

        int vm = 0;

        switch (opcode) {

            case FENCE_TYPE:

                switch (funct3) {

                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_FENCE;
                        asmLine.register_1 = RISCVRegister.fromInt(rs1);
                        asmLine.register_0 = RISCVRegister.fromInt(rd);
                        break;

                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_FENCEI;
                        // asmLine.register_1 = RISCVRegister.fromInt(rs1);
                        // asmLine.register_0 = RISCVRegister.fromInt(rd);
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                break;

            case R_TYPE_1:
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

                            case 0b011:
                                asmLine.mnemonic = Mnemonic.I_SLTU;
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
                                        "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
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
                                        "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                        }
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct7: " + funct7 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                break;

            case R_TYPE_2:
                switch (funct7) {

                    case 0b0000000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_ADDW;
                                break;

                            default:
                                throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                        }
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct7: " + funct7 + " in mnemonic " + ByteArrayUtil.byteToHex(data));

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
                                "Unknown I_TYPE_1! funct3: " + funct3 + " in mnemonic "
                                        + ByteArrayUtil.byteToHex(data));
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
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_SLTI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    case 0b011:
                        asmLine.mnemonic = Mnemonic.I_SLTIU;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    case 0b100:
                        asmLine.mnemonic = Mnemonic.I_XORI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    case 0b101:
                        switch (shtyp) {
                            case 0x00:
                                asmLine.mnemonic = Mnemonic.I_SRLI;
                                break;
                            case 0x20:
                                asmLine.mnemonic = Mnemonic.I_SRAI;
                                break;
                            default:
                                throw new RuntimeException("Unknown shtyp! " + shtyp);
                        }
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, shamt);
                        break;
                    case 0b110:
                        asmLine.mnemonic = Mnemonic.I_ORI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_31_20);
                        break;
                    case 0b111:
                        asmLine.mnemonic = Mnemonic.I_ANDI;
                        decodeIType_2(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_2! funct3: " + funct3 + " in mnemonic "
                                        + ByteArrayUtil.byteToHex(data));
                }
                break;

            case I_TYPE_3:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_LB;
                        break;
                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_LH;
                        break;
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_LW;
                        break;
                    case 0b011:
                        asmLine.mnemonic = Mnemonic.I_LD;
                        break;
                    case 0b100:
                        asmLine.mnemonic = Mnemonic.I_LBU;
                        break;
                    case 0b101:
                        asmLine.mnemonic = Mnemonic.I_LHU;
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_3! funct3: " + funct3 + " in mnemonic "
                                        + ByteArrayUtil.byteToHex(data));
                }
                decodeIType_3(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                break;

            case I_TYPE_4:
                switch (funct3) {

                    case 0b000:
                        switch (imm_31_20) {

                            case 0x00:
                                asmLine.mnemonic = Mnemonic.I_ECALL;
                                break;

                            case 0x01:
                                asmLine.mnemonic = Mnemonic.I_EBREAK;
                                break;

                            case 0x302:
                                asmLine.mnemonic = Mnemonic.I_MRET;
                                break;

                            default:
                                throw new RuntimeException(
                                        "Unknown mnemonic! imm_31_20 = " + ByteArrayUtil.byteToHex(imm_31_20));
                        }
                        break;

                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_CSRRW;
                        decodeIType_4_Register(asmLine, funct3, rd, rs1, imm_31_20);
                        break;

                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_CSRRS;
                        decodeIType_4_Register(asmLine, funct3, rd, rs1, imm_31_20);
                        break;

                    case 0b101:
                        asmLine.mnemonic = Mnemonic.I_CSRRWI;
                        decodeIType_4_Immediate(asmLine, funct3, rd, rs1, imm_31_20);
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown I_TYPE_4! funct3: " + funct3 + " in mnemonic "
                                        + ByteArrayUtil.byteToHex(data));
                }
                break;

            case I_TYPE_5:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_ADDIW; // 64 bits
                        break;
                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                long signExtendimm_31_20 = NumberParseUtil.sign_extend_12_bit_to_int64_t(imm_31_20);
                decodeIType_1_w(asmLine, funct3, funct7, rd, rs1, signExtendimm_31_20);
                break;

            case S_TYPE:
                switch (funct3) {
                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_SB;
                        break;
                    case 0b001:
                        asmLine.mnemonic = Mnemonic.I_SH;
                        break;
                    case 0b010:
                        asmLine.mnemonic = Mnemonic.I_SW;
                        break;
                    case 0b011:
                        asmLine.mnemonic = Mnemonic.I_SD;
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

                    case 0b111:
                        asmLine.mnemonic = Mnemonic.I_BGEU;
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }

                int imm_12 = (data >> 31) & 0b1;
                imm_11 = (data >> 7) & 0b1;
                int imm_10_5 = (data >> 25) & 0b11_1111;
                int imm4_1 = (data >> 8) & 0b1111;

                imm = (imm_12 << 12) | (imm_11 << 11) | (imm_10_5) << 5 | (imm4_1 << 1);
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

            case V_EXTENSION_OPERATION:

                int upperOpCode = (data >> 26) & 0b111111;
                switch (upperOpCode) {

                    // one of vmadd{.vv, .vx, .vi}
                    // Vector single-width integer add.
                    // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vadd
                    case 0b000000:
                        switch (funct3) {
                            case 0b000:
                                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);
                                asmLine.register_2 = RISCVRegister.fromIntRVV(rs1);

                                // masking enabled / disabled
                                vm = (data >> 25) & 0b1;
                                if (vm == 1) {
                                    asmLine.rvvMasking = true;
                                }

                                asmLine.mnemonic = Mnemonic.I_VADD_VV;
                                break;

                            case 0b111:
                                asmLine.register_0 = RISCVRegister.fromInt(rd);
                                asmLine.register_1 = RISCVRegister.fromInt(rs1);

                                int zimm = (data >> 20) & 0b111_1111_1111;

                                //int zimm_10_0 = ((vma & 0b1) << 7) | ((vta & 0b1) << 6) | ((sew & 0b111) << 3) | ((lmul & 0b111) << 0);

                                int lmul = (zimm >> 0) & 0b111;
                                int sew = (zimm >> 3) & 0b111;
                                int vta = (zimm >> 6) & 0b1;
                                int vma = (zimm >> 7) & 0b1;
                                int vill = (data >> 31) & 0b1;

                                // mask agnostic
                                //byte vma = (byte) (asmLine.rvvMask.equalsIgnoreCase("ma") ? 1 : 0);
                                String maskAgnostic = (vma == 1) ? "ma" : "mu";
                                logger.info("maskAgnostic: " + maskAgnostic);
                                asmLine.rvvMask = maskAgnostic;

                                // tail agnostic
                                //byte vta = (byte) (asmLine.rvvTail.equalsIgnoreCase("ta") ? 1 : 0);
                                String tailAgnostic = (vta == 1) ? "ta" : "tu";
                                logger.info("tailAgnostic: " + tailAgnostic);
                                asmLine.rvvTail = tailAgnostic;

                                // selected element width (SEW)
                                // byte sew = 4;
                                // if (asmLine.rvvSew.equalsIgnoreCase("e8")) {
                                //     sew = 0;
                                // } else if (asmLine.rvvSew.equalsIgnoreCase("e16")) {
                                //     sew = 1;
                                // } else if (asmLine.rvvSew.equalsIgnoreCase("e32")) {
                                //     sew = 2;
                                // } else if (asmLine.rvvSew.equalsIgnoreCase("e64")) {
                                //     sew = 3;
                                // }
                                String selectedElementWidth = "";
                                switch (sew) {
                                    case 0:
                                        selectedElementWidth = "e8";
                                        break;
                                    case 1:
                                        selectedElementWidth = "e16";
                                        break;
                                    case 2:
                                        selectedElementWidth = "e32";
                                        break;
                                    case 3:
                                        selectedElementWidth = "e64";
                                        break;
                                }
                                logger.info("selectedElementWidth: " + selectedElementWidth);
                                asmLine.rvvSew = selectedElementWidth;

                                // register grouping or fractions of register (LMUL)
                                // byte lmul = 0; // default value
                                // if (asmLine.rvvLmul != null) {
                                //     if (asmLine.rvvLmul.equalsIgnoreCase("mf8")) { // multiplier fractional
                                //         lmul = 5;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("mf4")) { // multiplier fractional
                                //         lmul = 6;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("mf2")) { // multiplier fractional
                                //         lmul = 7;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("m1")) { // grouped
                                //         lmul = 0;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("m2")) { // grouped
                                //         lmul = 1;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("m4")) { // grouped
                                //         lmul = 2;
                                //     } else if (asmLine.rvvLmul.equalsIgnoreCase("m8")) { // grouped
                                //         lmul = 3;
                                //     }
                                // }
                                String lmulMultiplier = "";
                                switch (lmul) {
                                    case 5:
                                        lmulMultiplier = "mf8";
                                        break;
                                    case 6:
                                        lmulMultiplier = "mf4";
                                        break;
                                    case 7:
                                        lmulMultiplier = "mf2";
                                        break;
                                    case 0:
                                        lmulMultiplier = "m1";
                                        break;
                                    case 1:
                                        lmulMultiplier = "m2";
                                        break;
                                    case 2:
                                        lmulMultiplier = "m4";
                                        break;
                                    case 3:
                                        lmulMultiplier = "m8";
                                        break;
                                }
                                logger.info("lmulMultiplier: " + lmulMultiplier);
                                asmLine.rvvLmul = lmulMultiplier;

                                logger.info("vill: " + vill);

                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("vsetvli ");
                                stringBuilder.append(Register.toStringAbi(asmLine.register_0)).append(", ");
                                stringBuilder.append(Register.toStringAbi(asmLine.register_1)).append(", ");
                                stringBuilder.append(selectedElementWidth).append(", ");
                                stringBuilder.append(lmulMultiplier).append(", ");
                                stringBuilder.append(maskAgnostic).append(", ");
                                stringBuilder.append(tailAgnostic);
                                logger.info(stringBuilder.toString());

                                asmLine.mnemonic = Mnemonic.I_VSETVLI;
                                break;

                            default:
                                throw new RuntimeException("No implemented yet!");
                        }
                        break;

                    // one of vmsne{.vv, .vx, .vi}
                    // Integer compare instruction to set bit if not equal.
                    // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
                    case 0b011001:
                        switch (funct3) {
                            case 0b011:
                                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                long imm_19_15 = (data >> 15) & 0b11111;
                                asmLine.numeric_2 = imm_19_15;

                                asmLine.rvvMasking = (((data >> 25) & 0b1) == 1);

                                asmLine.mnemonic = Mnemonic.I_VMSNE_VI;
                                break;

                            default:
                                throw new RuntimeException("No implemented yet!");
                        }
                        break;

                    case 0b010111:
                        switch (funct3) {
                            case 0b011:
                                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                long imm_19_15 = (data >> 15) & 0b11111;
                                asmLine.numeric_1 = imm_19_15;

                                asmLine.rvvMasking = (((data >> 25) & 0b1) == 1);

                                asmLine.mnemonic = Mnemonic.I_VMV_V_I;
                                break;

                            default:
                                throw new RuntimeException("No implemented yet!");
                        }
                        break;

                    default: 
                        // I_VSETVLI - https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
                        decode_RVV_VSETVLI(data, asmLine, rd, rs1);
                        break;
                }
                break;

            case V_EXTENSION_LOAD:

                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                asmLine.register_1 = RISCVRegister.fromInt(rs1);

                // masking enabled / disabled
                vm = (data >> 25) & 0b1;
                if (vm == 1) {
                    //asmLine.register_2 = RISCVRegister.fromInt(rs1);
                }

                switch (funct3) {
                    case 0b110:
                        asmLine.mnemonic = Mnemonic.I_VLE32_V;
                        break;
                    default:
                        throw new RuntimeException("Not implemented yet!");
                }
                break;

            case V_EXTENSION_STORE:
                logger.trace("RVV store " + asmLine);
                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                asmLine.register_1 = RISCVRegister.fromInt(rs1);

                // masking enabled / disabled
                vm = (data >> 25) & 0b1;
                if (vm == 1) {
                    //asmLine.register_2 = RISCVRegister.fromInt(rs1);
                }

                switch (funct3) {
                    case 0b110:
                        asmLine.mnemonic = Mnemonic.I_VSE32_V;
                        break;
                    default:
                        throw new RuntimeException("Not implemented yet!");
                }
                break;

            default:
                throw new RuntimeException("Decoding HEX: " + ByteArrayUtil.intToHex("0x%08x", data)
                        + ". Unknown Instruction Type! opcode = " + opcode);

        }

        return asmLine;
    }

    private void decode_RVV_VSETVLI(final int data, AsmLine<Register> asmLine, int rd, int rs1) {

        asmLine.register_1 = RISCVRegister.fromInt(rd);
        asmLine.register_0 = RISCVRegister.fromInt(rs1);

        int vtype = (data >> 20) & 0b111_1111_1111;
        int vlmul = (vtype >> 0) & 0b111;
        int vsew = (vtype >> 3) & 0b111;
        int vta = (vtype >> 6) & 0b1;
        int vma = (vtype >> 7) & 0b1;

        // selected element width (SW)
        switch (vsew) {
            case 0:
                asmLine.rvvSew = "e8";
                break;
            case 1:
                asmLine.rvvSew = "e16";
                break;
            case 2:
                asmLine.rvvSew = "e32";
                break;
            case 3:
                asmLine.rvvSew = "e64";
                break;
            default:
                asmLine.rvvSew = "undef";
                break;
        }

        // mode
        switch (vlmul) {
            case 5:
                asmLine.rvvLmul = "mf8";
                break;
            case 6:
                asmLine.rvvLmul = "mf4";
                break;
            case 7:
                asmLine.rvvLmul = "mf2";
                break;
            case 0:
                asmLine.rvvLmul = "m1";
                break;
            case 1:
                asmLine.rvvLmul = "m2";
                break;
            case 2:
                asmLine.rvvLmul = "m4";
                break;
            case 3:
                asmLine.rvvLmul = "m8";
                break;
            default:
                asmLine.rvvLmul = "undef";
                break;
        }

        // tail agnostic / undisturbed
        asmLine.rvvTail = ((vta == 1) ? "ta" : "tu");

        // mask agnostic / undisturbed
        asmLine.rvvMask = ((vma == 1) ? "ma" : "mu");

        asmLine.mnemonic = Mnemonic.I_VSETVLI;
    }

    private void decodeIType_4_Register(AsmLine<Register> asmLine, int funct3, int rd, int rs2, int imm_31_20) {
        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.numeric_1 = (long) imm_31_20;
        asmLine.register_2 = RISCVRegister.fromInt(rs2);
    }

    private void decodeIType_4_Immediate(AsmLine<Register> asmLine, int funct3, int rd, int rs2, int imm_31_20) {
        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.numeric_1 = (long) imm_31_20;
        asmLine.numeric_2 = (long) rs2;
    }

    /**
     * 32 bit
     * @param asmLine
     * @param funct3
     * @param funct7
     * @param rd
     * @param rs1
     * @param imm
     */
    private static void decodeIType_1(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, int imm) {

        asmLine.register_0 = RISCVRegister.fromInt(rd);
        asmLine.register_1 = RISCVRegister.fromInt(rs1);
        asmLine.numeric_2 = (long) imm;
    }

    /**
     * 64 bit
     * @param asmLine
     * @param funct3
     * @param funct7
     * @param rd
     * @param rs1
     * @param imm
     */
    private static void decodeIType_1_w(AsmLine asmLine, int funct3, int funct7, int rd, int rs1, long imm) {

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
        // if ((imm & 0x800) > 0) {
        if ((imm & 0x1000) > 0) {
            asmLine.numeric_2 = NumberParseUtil.sign_extend_13_bit_to_int32_t(imm);
            // asmLine.numeric_2 = (long) twosComplement(e);
            // asmLine.numeric_2 *= -1;
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
