package com.mycompany.decoder;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.antlr.v4.parse.v3TreeGrammarException;
import org.antlr.v4.parse.ANTLRParser.blockEntry_return;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.app.App;
import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.Mnemonic;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Register;
import com.mycompany.memory.Memory;

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

    public Memory memory;

    //
    // Uncompressed instructions (bit [0:1] are 11)
    //

    /** 0b1111111 */
    private static final int CUSTOM                 = 0b1111111;

    /** 0b0001111 */
    private static final int FENCE_TYPE             = 0b0001111;

    /** 0b0110011 */
    private static final int R_TYPE_1               = 0b0110011;

    /** 0b0111011 */
    private static final int R_TYPE_2               = 0b0111011;

    private static final int I_TYPE_1               = 0b1100111;
    private static final int I_TYPE_2               = 0b0010011;
    /** 0b0000011 */
    private static final int I_TYPE_3               = 0b0000011;
    /** 0b1110011 */
    private static final int I_TYPE_4               = 0b1110011;
    /** 0b0011011 = 27dec = 0x1B */
    private static final int I_TYPE_5               = 0b0011011;

    /** 0b0100011 */
    private static final int S_TYPE                 = 0b0100011;

    private static final int B_TYPE                 = 0b1100011;

    private static final int U_TYPE_1               = 0b0010111;
    private static final int U_TYPE_2               = 0b0110111;

    private static final int J_TYPE                 = 0b1101111;

    /** 0b1010111 */
    private static final int V_EXTENSION_OPERATION  = 0b1010111;
    /** 0b0000111 */
    private static final int V_EXTENSION_LOAD       = 0b0000111;
    /** 0b0100111 */
    private static final int V_EXTENSION_STORE      = 0b0100111;

    //
    // NEORV32 - XTEA extensions
    //

    private static final int NEORV32_XTEA_EXTENSION_I_TYPE      = 0b0101011;
    private static final int NEORV32_XTEA_EXTENSION_R_TYPE      = 0b0001011;

    // unprivileged spec, page 175
    private static final int QUADRANT_0 = 0b00;
    private static final int QUADRANT_1 = 0b01;
    private static final int QUADRANT_2 = 0b10;

    //
    // Compressed instructions (bit [0:1] are not 11)
    //

    private int xlen;

    /**
     * ctor
     * 
     * @param xlen
     */
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
    public List<AsmLine<?>> decode(final long address) {

        logger.trace("PC: " + ByteArrayUtil.byteToHex(address));

        int instruction = 0;

        if (App.XLEN == 32) {
            // for 32 bit cast to int
            instruction = memory.readWord((int)address, Decoder.byteOrder);
        }
        if (App.XLEN == 64) {
            // for 64 bit
            instruction = memory.readWord(address, byteOrder);
        }

        if ((instruction == 0x00000000) || (instruction == 0xFFFFFFFF)) {
            logger.info("instruction is 0x00 or 0xFF. Aborting CPU run!");

            // abort CPU
            // return new ArrayList<>();
            return null;
        }

        // DEBUG
        logger.trace("instruction: " + ByteArrayUtil.byteToHex(instruction, null, "%1$08X"));

        List<AsmLine<?>> result = new ArrayList<>();

        if ((xlen != 32) && (xlen != 64)) {
            throw new RuntimeException("unknown XLEN length!");
        }

        // decode 0 to NOP
        if (instruction == 0) {

            AsmLine<Register> asmLine = new AsmLine<>();
            result.add(asmLine);

            asmLine.instruction = instruction;
            asmLine.encodedLength = 4;

            asmLine.mnemonic = Mnemonic.I_NOP;

            return result;
        }

        // detect custom breakpoint instruction
        if (instruction == 0x1f1f1f1f) {

            AsmLine<Register> asmLine = new AsmLine<>();
            result.add(asmLine);

            asmLine.instruction = instruction;
            asmLine.encodedLength = 4;

            asmLine.mnemonic = Mnemonic.I_BRK;

            return result;
        }

        // DEBUG
        if (logger.isTraceEnabled()) {
            logger.trace("Decoding HEX: " + ByteArrayUtil.intToHex("%08x", instruction));
        }

        //
        // Compressed - if the lowermost two bits are set, the instruction is a full, 
        // uncompressed instruction (not compressed)
        //

        int opcode_c = instruction & 0b11;

        if (opcode_c == 0b11) {

            // instruction
            AsmLine<Register> asmLine = new AsmLine<>();
            result.add(asmLine);

            asmLine.instruction = instruction;
            asmLine.encodedLength = 4;
            processUncompressedInstruction(asmLine, instruction);

        } else {

            // first instruction
            AsmLine<Register> asmLine = new AsmLine<>();
            result.add(asmLine);

            int firstInstruction = (instruction >> 0) & 0b1111111111111111;

            asmLine.instruction = firstInstruction;
            asmLine.encodedLength = 2;
            processCompressedInstruction(asmLine, firstInstruction);

            // second instruction
            asmLine = new AsmLine<>();
            result.add(asmLine);

            int secondInstruction = (instruction >> 16) & 0b1111111111111111;

            if ((secondInstruction & 0b11) == 0b11) {

                // a non compressed instruction, now the decoder needs to pull in two bytes to
                // complete the instruction

                int instructionPart = 0;

                if (App.XLEN == 32) {
                    // for 32 bit cast to int
                    instructionPart = memory.readShort((int) (address + 4), byteOrder);
                }
                if (App.XLEN == 64) {
                    // for 64 bit
                    instructionPart = memory.readShort((address + 4), byteOrder);
                }

                // System.out.println(ByteArrayUtil.byteToHex(instructionPart));
                // System.out.println(ByteArrayUtil.byteToHex(secondInstruction));

                asmLine.instruction = (instructionPart << 16) | secondInstruction;

                // System.out.println(ByteArrayUtil.intToHex("0x%08x", asmLine.instruction));

                asmLine.encodedLength = 4;

                processUncompressedInstruction(asmLine, asmLine.instruction);

            } else {

                asmLine.instruction = secondInstruction;

                asmLine.encodedLength = 2;
                processCompressedInstruction(asmLine, secondInstruction);

            }

        }

        return result;
    }

    private void processCompressedInstruction(AsmLine<Register> asmLine, int data) {

        asmLine.machineCode = data;

        // boolean debugOutput = true;
        boolean debugOutput = false;
        if (debugOutput) {
            logger.info("");
            logger.info("");
            logger.info("***********************************************");
            logger.info("Pulling: " + ByteArrayUtil.byteToHex(data));
        }

        int opcode = data & 0b11;
        int funct3 = (data >> 13) & 0b111;
        int funct6 = (data >> 10) & 0b111111;
        int funct2 = (data >> 5) & 0b11;

        int imm_12 = (data >> 12) & 0b1;
        int imm_11_7 = (data >> 7) & 0b11111;
        int imm_9_7 = (data >> 7) & 0b111;
        int imm_6_2 = (data >> 2) & 0b11111;

        int imm_4_2 = (data >> 2) & 0b111;

        int imm_8 = (data >> 12) & 0b1;
        int imm_7_6 = (data >> 5) & 0b11;
        int imm_5 = (data >> 2) & 0b1;
        int imm_4_3 = (data >> 10) & 0b11;
        int imm_2_1 = (data >> 3) & 0b11;

        long immediate_8_1_combined = (imm_8 << 8) | (imm_7_6 << 6) | (imm_5 << 5) | (imm_4_3 << 3) | (imm_2_1 << 1);

        int imm_4_0 = (data >> 2) & 0b11111;

        long immediate_5_0_combined = (imm_12 << 5) | (imm_4_0 << 0);

        int imm_6 = (data >> 5) & 0b1;
        int imm_5_3 = (data >> 10) & 0b111;
        int imm_2 = (data >> 6) & 0b1;

        long imm_6_2_combined = (imm_6 << 6) | (imm_5_3 << 3) | (imm_2 << 2);

        int imm_9 = (data >> 12) & 0b1;
        int imm_4 = (data >> 6) & 0b1;
        int imm_8_7 = (data >> 3) & 0b11;

        int imm_9_4 = (imm_9 << 9) | (imm_8_7 << 7) | (imm_6 << 6) | (imm_5 << 5) | (imm_4 << 4);

        int imm_5_2_c = (data >> 9) & 0b1111;
        int imm_7_6_c = (data >> 7) & 0b11;

        int imm_7_2_c = (imm_7_6_c << 6) | (imm_5_2_c << 2);

        int imm_5_4_c = (data >> 11) & 0b11;
        int imm_9_6_c = (data >> 7) & 0b1111;
        int imm_3 = (data >> 5) & 0b1;

        int imm_9_2_c = (imm_9_6_c << 6) | (imm_5_4_c << 4) | (imm_3 << 3) | (imm_2 << 2);

        int imm_3_1 = (data >> 3) & 0b111;
        int imm_7 = (data >> 6) & 0b1;
        int imm_4_c = (data >> 11) & 0b1;
        int imm_6_c = (data >> 7) & 0b1;
        int imm_10_c = (data >> 8) & 0b1;
        int imm_9_8 = (data >> 9) & 0b11;
        int imm_11 = (data >> 12) & 0b1;

        int imm_11_1_c = (imm_11 << 11) | (imm_10_c << 10) | (imm_9_8 << 8) | (imm_7 << 7) | (imm_6_c << 6) | (imm_5 << 5) | (imm_4_c << 4) | (imm_3_1 << 1);

        int imm_4_2_c = (data >> 4) & 0b111;
        int imm_7_6_c_2 = (data >> 2) & 0b11;
        int imm_5_c = (data >> 12) & 0b1;

        int imm_7_2 = (imm_7_6_c_2 << 6) | (imm_5_c << 5) | (imm_4_2_c << 2);

        int imm_16_12_c = (data >> 2) & 0b11111;
        int imm_17 = (data >> 12) & 0b1;

        int imm_17_12 = (imm_17 << 17) | (imm_16_12_c << 12);

        int imm_11_10 = (data >> 10) & 0b11;

        int uimm_7_6 = 0;

        switch (opcode) {

            case QUADRANT_0:

                switch (funct3) {

                    case 0b000:
                        // c.addi4spn - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-addi4spn
                        // Expansion: addi rd', x2, nzuimm
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.register_1 = RISCVRegister.REG_SP;
                        asmLine.numeric_2 = (long) imm_9_2_c;
                        asmLine.mnemonic = Mnemonic.I_ADDI;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    case 0b001:
                        // c.fld - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-fld
                        throw new RuntimeException("Not implemented! c.fld");

                    case 0b010:
                        // c.lw - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-lw
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.offset_1 = imm_6_2_combined;
                        asmLine.mnemonic = Mnemonic.I_LW;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    case 0b011:
                        // For RV32: c.flw https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-flw
                        // For RV64: c.ld https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-ld

                        // these two instructions are encoded exactly the same!
                        // For RV32 C.FLW is decoded!
                        // For RV64 C.LD is decoded! 
                        // see https://docs.riscv.org/reference/isa/unpriv/c-st-ext.html 
                        // Figure 1 - Instruction Listing for RVC, Quadrant 0

                        //throw new RuntimeException("Not implemented! c.flw or c.ld");

                        if (App.XLEN == 32) {
                            throw new RuntimeException("Not implemented! c.flw for RV32");
                        }
                        if (App.XLEN == 64) {
                            // for 64 bit
                            uimm_7_6 = (data >> 5) & 0b11;
                            int uimm_5_3 = (data >> 10) & 0b111;
                            long uimm = (uimm_7_6 << 6) + (uimm_5_3 << 3);

                            int rs1 = (data >> 7) & 0b111;
                            int rs2 = (data >> 2) & 0b111;

                            asmLine.register_0 = RISCVRegister.fromInt(rs1);
                            asmLine.register_1 = RISCVRegister.fromInt(rs2);
                            asmLine.offset_1 = uimm;

                            asmLine.mnemonic = Mnemonic.I_LD;
                        }

                        break;

                    case 0b101:
                        // c.fsd - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-fsd
                        throw new RuntimeException("Not implemented! c.fsd");

                    case 0b110:
                        // c-sw - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-sw
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.offset_1 = imm_6_2_combined;
                        asmLine.mnemonic = Mnemonic.I_SW;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    case 0b111:
                        // RV32 - c.fsw - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-fsw
                        // RV32 - c.sd - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-sd

                        if (App.XLEN == 32) {
                            // for 32 bit
                            throw new RuntimeException("Not implemented! c.flw for RV32");
                        }
                        if (App.XLEN == 64) {
                            // for 64 bit
                            // c.sd - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-sd
                            // https://docs.riscv.org/reference/isa/unpriv/c-st-ext.html
                            //throw new RuntimeException("Not implemented! c.sd");

                            // C.SD is an RV64C-only instruction that stores a 64-bit value in register rs2′ 
                            // to memory. It computes an effective address by adding the zero-extended offset
                            // (uimm), scaled by 8 (therefore only uimm[7:6] uimm[5:3] and uimm[1:0] == 0 because
                            // shifting left by two is a multiplication by 8), to the base address in register rs1′. 
                            // It expands to sd rs2′, offset(rs1′).

                            // M[x[8+rs1'] + uimm][63:0] = x[8+rs2']

                            // Expansion: sd rs2′, offset(rs1′)

                            uimm_7_6 = (data >> 5) & 0b11;
                            int uimm_5_3 = (data >> 10) & 0b111;
                            long uimm = (uimm_7_6 << 6) + (uimm_5_3 << 3);

                            int rs1 = (data >> 7) & 0b111;
                            int rs2 = (data >> 2) & 0b111;

                            asmLine.register_0 = RISCVRegister.fromInt(rs1);
                            asmLine.register_1 = RISCVRegister.fromInt(rs2);
                            asmLine.offset_1 = uimm;

                            asmLine.mnemonic = Mnemonic.I_SD;
                        }
                        break;

                    default:
                        throw new UnsupportedOperationException(
                                "Unknown instruction: " + ByteArrayUtil.byteToHex(data));
                }

                break;

            case QUADRANT_1:

                if (funct6 == 0b100011) {
                    // logger.info("funct6");

                    if (funct2 == 0b00) {

                        // https://msyksphinz-self.github.io/riscv-isadoc/#_c_xor
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_2 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.mnemonic = Mnemonic.I_SUB;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    } else if (funct2 == 0b01) {

                        // https://msyksphinz-self.github.io/riscv-isadoc/#_c_xor
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_2 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.mnemonic = Mnemonic.I_XOR;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    } else if (funct2 == 0b10) {

                        // https://msyksphinz-self.github.io/riscv-isadoc/#_c_or
                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_2 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.mnemonic = Mnemonic.I_OR;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    } else if (funct2 == 0b11) {
                        // logger.info("funct2 == 3 ---- c.and");

                        asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        asmLine.register_2 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        asmLine.mnemonic = Mnemonic.I_AND;
                    }
                } else {

                    switch (funct3) {

                        case 0b000:
                            // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-addi

                            if (imm_11_7 == 0) {

                                asmLine.mnemonic = Mnemonic.I_NOP;

                            } else if (imm_11_7 != 0) {

                                // https://msyksphinz-self.github.io/riscv-isadoc/#_c_addi
                                asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                                asmLine.register_1 = RISCVRegister.fromInt(imm_11_7);
                                asmLine.numeric_2 = NumberParseUtil.sign_extend_6_bit_to_int32_t(immediate_5_0_combined);
                                asmLine.mnemonic = Mnemonic.I_ADDI;
                                if (debugOutput) {
                                    logger.info(asmLine.toString());
                                }

                            } else {

                                throw new UnsupportedOperationException(
                                        "Unknown instruction: " + ByteArrayUtil.byteToHex(data));

                            }
                            break;

                        case 0b001:
                            // c-jal - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-jal
                            // c-addiw - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-addiw
                            // imm[11|4|9:8|10|6|7|3:1|5]
                            // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20addi16sp#c-jal
                            asmLine.register_0 = RISCVRegister.REG_X1;
                            asmLine.numeric_1 = (long) imm_11_1_c;
                            // Expansion: jal x1, offset[11:1]
                            asmLine.mnemonic = Mnemonic.I_JAL;
                            if (debugOutput) {
                                logger.info(">> Pseudo: " + asmLine.toString());
                            }
                            break;

                        case 0b010:
                            // c.li - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-li
                            // li pseudo instruction - https://msyksphinz-self.github.io/riscv-isadoc/#_c_li
                            asmLine.register_0 = RISCVRegister.fromInt((int) imm_11_7);
                            asmLine.numeric_1 = NumberParseUtil.sign_extend_6_bit_to_int32_t(immediate_5_0_combined);
                            asmLine.mnemonic = Mnemonic.I_LI;
                            if (debugOutput) {
                                logger.info(">> Pseudo: " + asmLine.toString());
                            }

                            // addi rd, x0, imm[5:0]
                            asmLine.register_1 = RISCVRegister.REG_ZERO;
                            asmLine.numeric_2 = asmLine.numeric_1;
                            asmLine.numeric_1 = null;
                            asmLine.mnemonic = Mnemonic.I_ADDI;
                            if (debugOutput) {
                                logger.info("<< Resolved: " + asmLine.toString());
                            }
                            break;

                        case 0b011:

                            if (imm_11_7 == 0b00010) {

                                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-addi16sp

                                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20addi16sp#c-addi16sp
                                // c.addi16sp
                                //
                                // https://stackoverflow.com/questions/78900543/what-does-n-stand-for-in-c-addi4spn
                                // c.addi16sp is the destructive operation on the stack pointer register.
                                // There is also a non-desctructive version called c.addi4spn

                                // logger.info("<< Resolved: " + asmLine.toString());
                                long immResult = NumberParseUtil.sign_extend_9_bit_to_int32_t(imm_9_4);
                                // logger.info("" + immResult);

                                // c.addi16sp imm
                                asmLine.numeric_0 = immResult;
                                asmLine.mnemonic = Mnemonic.I_C_ADDI16SP;

                                // Expansion addi x2, x2, nzimm[9:4]
                                asmLine.register_0 = RISCVRegister.REG_SP;
                                asmLine.register_1 = RISCVRegister.REG_SP;
                                asmLine.numeric_2 = immResult;
                                asmLine.numeric_1 = null;
                                asmLine.mnemonic = Mnemonic.I_ADDI;
                                if (debugOutput) {
                                    logger.info("<< Resolved: " + asmLine.toString());
                                }

                            } else {

                                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20addi16sp#c-lui

                                asmLine.register_0 = RISCVRegister.fromInt((int) imm_11_7);
                                asmLine.numeric_1 = NumberParseUtil.sign_extend_17_bit_to_int32_t(imm_17_12);
                                asmLine.mnemonic = Mnemonic.I_LUI;
                                if (debugOutput) {
                                    logger.info("<< Resolved: " + asmLine.toString());
                                }

                                // throw new UnsupportedOperationException(
                                //         "Unknown instruction: " + ByteArrayUtil.byteToHex(data));

                            }
                            break;

                        // case 0b100:
                        //     // https://msyksphinz-self.github.io/riscv-isadoc/#_c_or
                        //     asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        //     asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                        //     asmLine.register_2 = RISCVRegister.fromIntCompressedInstruction((int) imm_4_2);
                        //     asmLine.mnemonic = Mnemonic.I_OR;
                        //     if (debugOutput) {
                        //         logger.info(asmLine.toString());
                        //     }
                        //     break;

                        case 0b100:

                            if (imm_11_10 == 0b00) {

                                // c.srli -- https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20sw#c-srli
                                logger.info("c.srli");

                                // int a = imm_9_7;
                                int b = (imm_12 << 5) | (imm_6_2 << 0);

                                asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                                asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                                asmLine.numeric_2 = (long) b;
                                asmLine.mnemonic = Mnemonic.I_SRLI;

                            } else if (imm_11_10 == 0b01) {

                                // c.srai -- https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20sw#c-srai
                                logger.info("c.srai");

                            } else if (imm_11_10 == 0b10) {

                                // c.andi -- https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20sw#c-andi
                                // logger.info("c.andi");

                                int b = (imm_12 << 5) | (imm_6_2 << 0);

                                asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                                asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                                asmLine.numeric_2 = (long) NumberParseUtil.sign_extend_6_bit_to_int32_t(b);
                                asmLine.mnemonic = Mnemonic.I_ANDI;

                            }
                            break;

                        case 0b101:
                            // c.j
                            // imm[11|4|9:8|10|6|7|3:1|5]
                            // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20sw#c-j
                            asmLine.register_0 = RISCVRegister.REG_X0;
                            // asmLine.numeric_1 = (long) imm_11_1_c;
                            asmLine.numeric_1 = (long) NumberParseUtil.sign_extend_12_bit_to_int32_t(imm_11_1_c);
                            // Expansion: jal x0, offset[11:1]
                            asmLine.mnemonic = Mnemonic.I_JAL;
                            if (debugOutput) {
                                logger.info(">> Pseudo: " + asmLine.toString());
                            }
                            break;

                        case 0b110:
                            // https://msyksphinz-self.github.io/riscv-isadoc/#_c_beqz
                            // c.beqz rs1', offset --> beq rs1', x0, offset[8:1]
                            asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction((int) imm_9_7);
                            asmLine.register_1 = RISCVRegister.REG_ZERO;
                            asmLine.numeric_2 = immediate_8_1_combined;
                            asmLine.mnemonic = Mnemonic.I_BEQ;
                            if (debugOutput) {
                                logger.info(asmLine.toString());
                            }
                            break;

                        case 0b111:
                            // bnez pseudo instruction -
                            // https://msyksphinz-self.github.io/riscv-isadoc/#_c_bnez
                            // asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction(imm_9_7);
                            asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction(imm_9_7);
                            asmLine.numeric_1 = NumberParseUtil.sign_extend_9_bit_to_int32_t(immediate_8_1_combined);
                            asmLine.mnemonic = Mnemonic.I_BNEZ;
                            if (debugOutput) {
                                logger.info(">> Pseudo: " + asmLine.toString());
                            }

                            // bne rs1', x0, offset[8:1]
                            asmLine.register_1 = RISCVRegister.REG_ZERO;
                            asmLine.numeric_2 = asmLine.numeric_1;
                            asmLine.numeric_1 = null;
                            asmLine.mnemonic = Mnemonic.I_BNE;
                            if (debugOutput) {
                                logger.info("<< Resolved: " + asmLine.toString());
                            }
                            break;

                        default:
                            throw new UnsupportedOperationException(
                                    "Unknown instruction: " + ByteArrayUtil.byteToHex(data));
                    }
                }

                break;

            case QUADRANT_2:

                switch (funct3) {

                    case 0b000:
                        // c.slli - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20sw#c-slli
                        // logger.info("c.slli");

                        // Expansion: slli rd, rd, shamt[5:0]
                        asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                        asmLine.register_1 = RISCVRegister.fromInt(imm_11_7);
                        asmLine.numeric_2 = NumberParseUtil.sign_extend_9_bit_to_int32_t(imm_7_2);
                        asmLine.mnemonic = Mnemonic.I_SLLI;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        break;

                    case 0b010:
                        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html?highlight=c%20addi16sp#c-lwsp
                        
                        // Expansion: lw rd, offset[7:2](x2)
                        asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                        asmLine.register_1 = RISCVRegister.REG_X2;
                        asmLine.offset_1 = NumberParseUtil.sign_extend_9_bit_to_int32_t(imm_7_2);
                        asmLine.mnemonic = Mnemonic.I_LW;
                        if (debugOutput) {
                            logger.info(asmLine.toString());
                        }
                        
                        break;

                    case 0b100:

                        if ((imm_12 == 0) && (imm_11_7 != 0) && (imm_6_2 == 0)) {

                            // https://msyksphinz-self.github.io/riscv-isadoc/#_c_jr

                            // jalr x0,rs1,0
                            asmLine.register_0 = RISCVRegister.REG_ZERO;
                            asmLine.register_1 = RISCVRegister.fromInt(imm_11_7);
                            asmLine.numeric_2 = 0x00L;
                            asmLine.mnemonic = Mnemonic.I_JALR;
                            if (debugOutput) {
                                logger.info(asmLine.toString());
                            }

                        } else if ((imm_12 == 0) && (imm_11_7 != 0) && (imm_6_2 != 0)) {

                            // https://msyksphinz-self.github.io/riscv-isadoc/#_c_mv
                            // TODO: mv is a pseudo instruction which has to be replaced by add rd, x0, rs2

                            asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                            asmLine.register_1 = RISCVRegister.fromInt(imm_6_2);
                            asmLine.mnemonic = Mnemonic.I_MV;
                            if (debugOutput) {
                                logger.info(">> Pseudo: " + asmLine.toString());
                            }

                            asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                            asmLine.register_1 = RISCVRegister.fromInt(imm_6_2);
                            asmLine.register_2 = RISCVRegister.REG_ZERO;
                            // asmLine.register_0 = RISCVRegister.fromIntCompressedInstruction(imm_11_7);
                            // asmLine.register_1 = RISCVRegister.fromIntCompressedInstruction(imm_6_2);
                            // asmLine.register_2 = RISCVRegister.REG_ZERO;
                            asmLine.mnemonic = Mnemonic.I_ADD;
                            if (debugOutput) {
                                logger.info("<< Resolved: " + asmLine.toString());
                            }

                        } else if ((imm_12 == 1) && (imm_11_7 == 0) && (imm_6_2 == 0)) {
                            asmLine.mnemonic = Mnemonic.I_EBREAK;
                        } else if ((imm_12 == 1) && (imm_11_7 != 0) && (imm_6_2 == 0)) {
                            asmLine.mnemonic = Mnemonic.I_JALR;
                        } else if ((imm_12 == 1) && (imm_11_7 != 0) && (imm_6_2 != 0)) {

                            // https://msyksphinz-self.github.io/riscv-isadoc/#_c_add

                            asmLine.register_0 = RISCVRegister.fromInt(imm_11_7);
                            asmLine.register_1 = RISCVRegister.fromInt(imm_11_7);
                            asmLine.register_2 = RISCVRegister.fromInt(imm_6_2);

                            asmLine.mnemonic = Mnemonic.I_ADD;
                            if (debugOutput) {
                                logger.info("<< Resolved: " + asmLine.toString());
                            }

                        } else {
                            throw new UnsupportedOperationException(
                                    "Unknown instruction: " + ByteArrayUtil.byteToHex(data));
                        }
                        break;

                    case 0b110:
                        // c.swsp - store word to stack

                        long tem = NumberParseUtil.sign_extend_8_bit_to_int32_t(imm_7_2_c);
                        // logger.info("" + tem);

                        // c.swsp x12, 40

                        // Expansion: sw rs2, offset[7:2](x2)
                        asmLine.register_0 = RISCVRegister.fromInt(imm_6_2);
                        asmLine.register_1 = RISCVRegister.REG_SP;
                        asmLine.offset_1 = tem;
                        asmLine.mnemonic = Mnemonic.I_SW;
                        if (debugOutput) {
                            logger.info("<< Resolved: " + asmLine.toString());
                        }
                        break;

                    case 0b111:
                        // c.fswsp - https://msyksphinz-self.github.io/riscv-isadoc/html/rvc.html#c-fswsp

                        // c.fswsp
                        // Store single-precision value to stack
                        // Stores a single-precision floating-point value in floating-point register 
                        // fs2 to memory. It computes an effective address by adding the zero-extended 
                        // offset, scaled by 4, to the stack pointer, x2. 
                        //
                        // It expands to fsw fs2, offset(x2).
                        
                        //throw new RuntimeException();

                        int fs0 = (data >> 2) & 0b11111;
                        asmLine.register_0 = RISCVRegister.fromIntFloatExtension(fs0);

                        int imm_5_2 = (data >> 9) & 0b1111;
                        imm_7_6 = (data >> 7) & 0b11;
                        long imm = (imm_7_6 << 6) + (imm_5_2 << 2);
                        asmLine.offset_1 = imm;                        

                        asmLine.register_1 = RISCVRegister.REG_SP;

                        asmLine.mnemonic = Mnemonic.I_FSW;

                        // System.out.println(asmLine.toString());
                        break;

                    default:
                        throw new UnsupportedOperationException(
                                "Unknown instruction: " + ByteArrayUtil.byteToHex(data) + " funct3: " + Integer.toBinaryString(funct3));

                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown instruction: " + ByteArrayUtil.byteToHex(data));

        }

    }

    private void processUncompressedInstruction(AsmLine<Register> asmLine, int data) {

        asmLine.machineCode = data;

        //
        // Uncompressed
        //

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
        int imm_30_25 = (data >> 25) & 0b111111;
        int imm_31_26 = (data >> 26) & 0b111111;

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

        // DEBUG ???
        // StringBuilder stringBuilder = new StringBuilder();
        // stringBuilder.append(ByteArrayUtil.intToHex("%08x", data));
        // System.out.println(stringBuilder.toString());

        long imm_19_15 = 0;
        int upperOpCode = 0;

        int vm = 0;
        int vs2 = 0;

        int imm32 = (data >> 31) & 0b1;

        switch (opcode) {

            case CUSTOM:
                switch (funct3) {

                    case 0b000:
                        asmLine.mnemonic = Mnemonic.I_PRINT_REG;
                        asmLine.register_0 = RISCVRegister.fromInt(rd);
                        break;

                    default:
                        throw new RuntimeException(
                                "Unknown funct3: " + funct3 + " in mnemonic " + ByteArrayUtil.byteToHex(data));
                }
                break;

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

                    case 0b1000000:
                        switch (funct3) {

                            case 0b000:
                                asmLine.mnemonic = Mnemonic.I_ADD1;
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
                int imm_10_1 = (data >> 21) & 0b11_1111_1111;
                int imm_20 = (data >> 31) & 0b1;

                imm = (imm_20 << 20) | (imm_19_12 << 12) | (imm_11) << 11 | (imm_10_1 << 1);
                decodeJType(asmLine, rd, imm);

                asmLine.mnemonic = Mnemonic.I_JAL;
                break;

            case V_EXTENSION_OPERATION:

                if (funct3 == 0b111) {

                    if (imm32 == 0) { // VSETVLI
                        asmLine.register_0 = RISCVRegister.fromInt(rd);
                        asmLine.register_1 = RISCVRegister.fromInt(rs1);

                        int zimm = (data >> 20) & 0b111_1111_1111;

                        // int zimm_10_0 = ((vma & 0b1) << 7) | ((vta & 0b1) << 6) | ((sew & 0b111) <<
                        // 3) | ((lmul & 0b111) << 0);

                        int lmul = (zimm >> 0) & 0b111;
                        int sew = (zimm >> 3) & 0b111;
                        int vta = (zimm >> 6) & 0b1;
                        int vma = (zimm >> 7) & 0b1;
                        int vill = (data >> 31) & 0b1;

                        // mask agnostic
                        String maskAgnostic = (vma == 1) ? "ma" : "mu";
                        // logger.info("maskAgnostic: " + maskAgnostic);
                        asmLine.rvvMask = maskAgnostic;

                        // tail agnostic
                        String tailAgnostic = (vta == 1) ? "ta" : "tu";
                        // logger.info("tailAgnostic: " + tailAgnostic);
                        asmLine.rvvTail = tailAgnostic;

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
                        // logger.info("selectedElementWidth: " + selectedElementWidth);
                        asmLine.rvvSew = selectedElementWidth;

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

                        // logger.info("lmulMultiplier: " + lmulMultiplier);

                        asmLine.rvvLmul = lmulMultiplier;

                        // logger.info("vill: " + vill);

                        if (logger.isTraceEnabled()) {

                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("vsetvli ");
                            stringBuilder.append(Register.toStringAbi(asmLine.register_0)).append(", ");
                            stringBuilder.append(Register.toStringAbi(asmLine.register_1)).append(", ");
                            stringBuilder.append(selectedElementWidth).append(", ");
                            stringBuilder.append(lmulMultiplier).append(", ");
                            stringBuilder.append(maskAgnostic).append(", ");
                            stringBuilder.append(tailAgnostic);
                            logger.trace(stringBuilder.toString());
                        }

                        asmLine.mnemonic = Mnemonic.I_VSETVLI;
                    
                    } else if (imm32 == 1) { 

                        if (imm_30_25 == 0b000000) {
                            // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvl
                            throw new RuntimeException("Not implemented yet: vsetvl");
                        } else {
                            // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetivli
                            //throw new RuntimeException("Not implemented yet: vsetivli");

                            asmLine.register_0 = RISCVRegister.fromInt(rd);
                            //asmLine.register_1 = RISCVRegister.fromInt(rs1);
                            asmLine.numeric_1 = (long) rs1;

                            int zimm = (data >> 20) & 0b11_1111_1111;

                            int lmul = (zimm >> 0) & 0b111;
                            int sew = (zimm >> 3) & 0b111;
                            int vta = (zimm >> 6) & 0b1;
                            int vma = (zimm >> 7) & 0b1;
                            int vill = (data >> 31) & 0b1;

                            // mask agnostic
                            String maskAgnostic = (vma == 1) ? "ma" : "mu";
                            // logger.info("maskAgnostic: " + maskAgnostic);
                            asmLine.rvvMask = maskAgnostic;

                            // tail agnostic
                            String tailAgnostic = (vta == 1) ? "ta" : "tu";
                            // logger.info("tailAgnostic: " + tailAgnostic);
                            asmLine.rvvTail = tailAgnostic;

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
                            // logger.info("selectedElementWidth: " + selectedElementWidth);
                            asmLine.rvvSew = selectedElementWidth;

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

                            asmLine.rvvLmul = lmulMultiplier;

                            // logger.info("vill: " + vill);

                            if (logger.isTraceEnabled()) {

                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("vsetivli ");
                                stringBuilder.append(Register.toStringAbi(asmLine.register_0)).append(", ");
                                stringBuilder.append(Register.toStringAbi(asmLine.register_1)).append(", ");
                                stringBuilder.append(selectedElementWidth).append(", ");
                                stringBuilder.append(lmulMultiplier).append(", ");
                                stringBuilder.append(maskAgnostic).append(", ");
                                stringBuilder.append(tailAgnostic);
                                logger.trace(stringBuilder.toString());
                            }

                            asmLine.mnemonic = Mnemonic.I_VSETIVLI;
                        }

                    }

                } else {

                    switch (imm_31_26) { // funct3 is [14-12]

                        case 0b000000:

                            switch (funct3) {
                                
                                // case 0b000000: // VADD
                                //     asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                //     asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                //     imm_19_15 = (data >> 15) & 0b11111;
                                //     asmLine.numeric_1 = imm_19_15;

                                //     asmLine.rvvMasking = (((data >> 25) & 0b1) == 1);

                                //     asmLine.mnemonic = Mnemonic.I_VADD_VV;
                                //     break;

                                // one of vmadd{.vv, .vx, .vi}
                                // Vector single-width integer add.
                                // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vadd
                                case 0b000: // VADD.VV
                                    asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                    asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);
                                    asmLine.register_2 = RISCVRegister.fromIntRVV(rs1);

                                    // masking enabled / disabled
                                    vm = (data >> 25) & 0b1;
                                    if (vm == 1) {
                                        asmLine.rvvMasking = true;
                                    }

                                    // System.out.println("rd = " + rd);

                                    asmLine.mnemonic = Mnemonic.I_VADD_VV;
                                    break;

                                case 0b011:
                                    // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vadd
                                    // vadd.vi vd, vs2, imm, vm
                                    // vadd.vi	v5,v1,8

                                    asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                    asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                    imm_19_15 = (data >> 15) & 0b11111;
                                    asmLine.numeric_2 = imm_19_15;

                                    // masking enabled / disabled
                                    vm = (data >> 25) & 0b1;
                                    if (vm == 1) {
                                        asmLine.rvvMasking = true;
                                    }

                                    asmLine.mnemonic = Mnemonic.I_VADD_VI;
                                    break;

                                default:
                                    throw new RuntimeException("Not implemented yet: func3 = " + Integer.toBinaryString(funct3));

                            }
                        break;

                        case 0b010100:

                            switch (funct3) {

                                case 0b010: // VID
                                    // https://rvv-isadoc.readthedocs.io/en/latest/arith_mask.html#vid
                                    // Vector element index instruction. Writes each element’s index to the destination vector register group, from 0 to vl-1.
                                    // System.out.println("vid!");

                                    imm_19_15 = (data >> 15) & 0b11111;
                                    if (imm_19_15 != 0b10001) {
                                        throw new RuntimeException("Encoding is incorrect! Hardcoded 0b10001 expected!");
                                    }

                                    // masking enabled / disabled
                                    vm = (data >> 25) & 0b1;
                                    asmLine.rvvMasking = (((data >> 25) & 0b1) == 1);
                                    vs2 = rs2;

                                    // System.out.println(imm_19_15);

                                    asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                    asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                    asmLine.mnemonic = Mnemonic.I_VID;

                                    logger.info("vid: " + asmLine.toString());

                                    // throw new RuntimeException("No implemented yet!");
                                break;

                                default:
                                    throw new RuntimeException("");
                                }
                            break;

                        // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
                        case 0b011001: 
                        
                            switch (funct3) {

                                case 0b010:// VMSNE.VI
                                    asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                    asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                    imm_19_15 = (data >> 15) & 0b11111;
                                    asmLine.numeric_2 = imm_19_15;

                                    asmLine.rvvMasking = (((data >> 25) & 0b1) == 1);

                                    asmLine.mnemonic = Mnemonic.I_VMSNE_VI;
                                    break;

                                default:
                                    throw new RuntimeException("");
                            }
                            break;

                        case 0b100101: // vsll + vmul
                            // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vsll

                            switch (funct3) {

                                case 0b000: // vsll.vv vd, vs2, vs1, vm
                                    throw new RuntimeException("000");

                                case 0b100: // vsll.vx vd, vs2, rs1, vm
                                    throw new RuntimeException("100");

                                case 0b011: // vsll.vi vd, vs2, imm, vm
                                    // vsll.vi	v1,v1,1
                                    // throw new RuntimeException("011");

                                    asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                                    asmLine.register_1 = RISCVRegister.fromIntRVV(rs2);

                                    // imm[4:0]
                                    imm_19_15 = (data >> 15) & 0b11111;
                                    asmLine.numeric_2 = imm_19_15;

                                    // masking enabled / disabled
                                    vm = (data >> 25) & 0b1;
                                    if (vm == 1) {
                                        asmLine.rvvMasking = true;
                                    }

                                    asmLine.mnemonic = Mnemonic.I_VSLL_VI;
                                    break;

                                default:
                                    throw new RuntimeException("");

                            }
                            break;

                        // case 0b010:
                        //     upperOpCode = (data >> 26) & 0b111111;
                        //     switch (upperOpCode) {
                                
                        //         default:
                        //             throw new RuntimeException("No implemented yet!");
                        //     }
                        //     break;

                        
                        // case 0b011:

                        //     upperOpCode = (data >> 26) & 0b111111;
                        //     switch (upperOpCode) {

                                
                                
                        //         case 0b100101: // VSSLI



                        //             break;

                                

                        //         default:
                        //             throw new RuntimeException("No implemented yet! upperOpCode = " + Integer.toBinaryString(upperOpCode));
                                
                        //     }

                        

                        // default:
                        //     throw new RuntimeException("No implemented yet! RVV instruction, funct3: " + funct3);
                    }
                }
            break;

                // int upperOpCode = (data >> 26) & 0b111111;
                // switch (upperOpCode) {

                //     // one of vmadd{.vv, .vx, .vi}
                //     // Vector single-width integer add.
                //     // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vadd
                //     case 0b000000:
                        

                //     case 0b010111:
                //         switch (funct3) {
                            

                //             default:
                //                 throw new RuntimeException("No implemented yet!");
                //         }
                //         break;

                //     // one of vmsne{.vv, .vx, .vi}
                //     // Integer compare instruction to set bit if not equal.
                //     // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
                //     case 0b011001:
                //         switch (funct3) {
                            
                //         }
                //         break;

                //     case 0b110011:
                //         // I_VSETVLI -
                //         // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
                //         decode_RVV_VSETVLI(data, asmLine, rd, rs1);
                //         break;

                //     default:
                //         throw new RuntimeException("");
                //         // // I_VSETVLI -
                //         // // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
                //         // decode_RVV_VSETVLI(data, asmLine, rd, rs1);
                //         // break;
                // }
                // break;

            case V_EXTENSION_LOAD:

                asmLine.register_0 = RISCVRegister.fromIntRVV(rd);
                asmLine.register_1 = RISCVRegister.fromInt(rs1);

                // masking enabled / disabled
                vm = (data >> 25) & 0b1;
                if (vm == 1) {
                    // asmLine.register_2 = RISCVRegister.fromInt(rs1);
                }

                // https://rvv-isadoc.readthedocs.io/en/latest/load_and_store.html#vle-eew
                switch (funct3) {
                    case 0b000:
                        // TODO
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VLE8_V;
                        break;
                    case 0b101:
                        // TODO
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VLE16_V;
                        break;
                    case 0b110:
                        // TODO
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VLE32_V;
                        break;
                    case 0b111:
                        // TODO
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VLE64_V;
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
                    // asmLine.register_2 = RISCVRegister.fromInt(rs1);
                }

                // https://rvv-isadoc.readthedocs.io/en/latest/load_and_store.html#vse-eew
                switch (funct3) {
                    case 0b000:
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VSE8_V;
                        break;
                    case 0b101:
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VSE16_V;
                        break;
                    case 0b110:
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VSE32_V;
                        break;
                    case 0b111:
                        logger.error("TODO: Not implemented yet!");
                        asmLine.mnemonic = Mnemonic.I_VSE64_V;
                        break;
                    default:
                        throw new RuntimeException("Not implemented yet!");
                }
                break;

            case NEORV32_XTEA_EXTENSION_I_TYPE:
                // System.out.println("XTEA_EXTENSION I_TYPE");
                switch (funct3) {

                    case 0b000:
                        // logger.info("xtea_key_read");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_KEY_READ;
                        decodeIType_1(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;

                    case 0b001:
                        // logger.info("xtea_key_write");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_KEY_WRITE;
                        decodeIType_1(asmLine, funct3, funct7, rd, rs1, imm_11_0);
                        break;

                    default:
                        throw new RuntimeException("Not implemented yet!");
                }
                break;
            
            case NEORV32_XTEA_EXTENSION_R_TYPE:
                // System.out.println("XTEA_EXTENSION R_TYPE");
                switch (funct3) {

                    case 0b000:
                        // logger.info("xtea_hw_enc_v0_step");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_ENC_V0_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;

                    case 0b001:
                        // logger.info("xtea_hw_enc_v1_step");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_ENC_V1_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;

                    case 0b010:
                        // logger.info("xtea_hw_dec_v0_step");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_DEC_V0_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;

                    case 0b011:
                        // logger.info("xtea_hw_dec_v1_step");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_DEC_V1_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;

                    case 0b100:
                        // logger.info("xtea_hw_init");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_INIT_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;

                    case 0b111:
                        logger.info("xtea_hw_illegal_inst");
                        asmLine.mnemonic = Mnemonic.I_NEORV32_XTEA_ILLEGAL_INST_C;
                        decodeRType(asmLine, funct3, funct7, rd, rs1, rs2);
                        break;
                    
                    default:
                        throw new RuntimeException("Not implemented yet!");
                }
                break;
            
            default:
                throw new RuntimeException("Decoding HEX: " + ByteArrayUtil.intToHex("0x%08x", data)
                        + ". Unknown Instruction Type! opcode = " + opcode);

        }
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
     * 
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
     * 
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
        asmLine.numeric_1 = NumberParseUtil.sign_extend_21_bit_to_int32_t(imm);

        // // first sign extend for a 12 bit immediate to a whole 32 bit value, then make
        // // the number negative if it was negative
        // if ((imm & 0x800) > 0) {
        //     asmLine.numeric_1 = (long) twosComplement(imm | 0xFFFFF000);
        //     asmLine.numeric_1 *= -1;
        // }
    }

}
