package com.mycompany.cpu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.decoder.DelegatingDecoder;
import com.mycompany.filehandling.FileHandling;
import com.mycompany.memory.Memory;

public class SingleCycle32BitCPU extends AbstractCPU {

    private static final int RVV_CSR_VLENB = 0xFFF;

    private static final Logger logger = LoggerFactory.getLogger(SingleCycle64BitCPU.class);

    public int pc;

    public Memory memory;

    public DelegatingDecoder decoder = new DelegatingDecoder();

    public FileHandling fileHandling = new FileHandling();

    // Zicsr extension register id
    private int heartIdCSRRegister = 0x00;

    private boolean singleStepping;

    private boolean debugASMLineOutput;

    private Random random = new Random();

    public int[] registerFile = new int[32];

    public int readRegisterFile(int index) {

        // register zero is hardcoded zero
        if (index == 0) {
            return 0;
        }

        // set the value
        return registerFile[index];
    }

    public void writeRegisterFile(final int register_index, final int value) {

        // write to zero register has no effect
        if (register_index == RISCVRegister.REG_ZERO.getIndex()) {
            return;
        }

        // set the value
        registerFile[register_index] = value;
    }

    /**
     * ctor
     */
    public SingleCycle32BitCPU() {
        registerFile[0] = 0; // set zero
    }

    /**
     * https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html
     * 
     * @throws IOException
     */
    public boolean step() throws IOException {

        if ((pc == 0xFFFFFFFF) || (pc == 0xCAFEBABE)) {
            logger.info("PC is 0xFFFFFFFF! End of application!");

            return false;
        }

        decoder.memory = memory;

        // DECODE - use decoder to turn 32 bits into an instruction ASM Line including
        // parameters and opcode
        List<AsmLine<?>> asmLines = decoder.decode(pc);

        boolean result = true;

        for (AsmLine<?> asmLine : asmLines) {

            if (asmLine.mnemonic == null) {

                logger.trace(asmLine.toString());
                throw new RuntimeException(
                        "Decoding instruction without mnemonic! " + ByteArrayUtil.byteToHex(asmLine.instruction));
            }

            result &= executeAsmLine(asmLine);
        }

        return result;
    }

    private boolean executeAsmLine(AsmLine<?> asmLine) throws IOException {

        boolean printInstructions = false;

        // // DEBUG output ASM line
        // //debugASMLineOutput = true;
        // debugASMLineOutput = false;
        // if (debugASMLineOutput) {
        // logger.info("PC: " + pc + " (" + ByteArrayUtil.longToHex("%08x", pc) + ")" +
        // ". Loaded Instr: HEX: "
        // + ByteArrayUtil.intToHex("%08x", instruction) + " " + asmLine.toString());
        // }

        // singleStepping = true;
        singleStepping = false;
        if (singleStepping) {
            printMemoryAroundPC(5);
            System.out.println("");
        }

        logger.trace(ByteArrayUtil.byteToHex(pc) + ": [" + ByteArrayUtil.byteToHex(asmLine.instruction, null, "%1$08X")
                + "] " + asmLine.toString());

        // if (pc == 0x1049E) {
        // if (pc == 0x1041c) {
        //if (pc == 0x1043c) {
        // if (pc == 0x10424) {
        //    System.out.println("test");
        //}

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        int addr;
        int value;
        long value_l;
        int register_0_value;
        int register_1_value;
        int register_2_value;
        byte[] let;
        StringBuilder stringBuilder;

        long register_0_value_l;
        long register_1_value_l;
        long register_2_value_l;

        int addressA0;
        int addressA1;
        int addressA2;
        int fileHandle;

        int immValSignExtended;

        int csrId;
        int csrValue;

        switch (asmLine.mnemonic) {

            case I_ADD:
                if (printInstructions) {
                    logger.info("add: " + asmLine);
                }

                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        + readRegisterFile(asmLine.register_2.getIndex()));

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ADDI:
                // rd = rs1 + imm
                if (printInstructions) {
                    logger.info("addi: " + asmLine);
                }

                int first_register_value = readRegisterFile(asmLine.register_1.getIndex());
                int immediate_value = asmLine.numeric_2.intValue();

                logger.trace("1st Register Name: " + asmLine.register_1.toStringAbi());
                logger.trace("1st Register Value: " + first_register_value);
                logger.trace("2nd Immediate Value: " + immediate_value);
                logger.trace("dest Register Name: " + asmLine.register_0.toStringAbi());

                int result = first_register_value + immediate_value;

                logger.trace("New: " + ByteArrayUtil.byteToHex(result));

                writeRegisterFile(asmLine.register_0.getIndex(), result);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_AND:
                if (printInstructions) {
                    logger.info("and: " + asmLine);
                }
                // Performs bitwise AND on registers rs1 and rs2 and place the result in rd
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        & readRegisterFile(asmLine.register_2.getIndex()));
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ANDI:
                if (printInstructions) {
                    logger.info("andi: " + asmLine);
                }
                // Format: andi rd, rs1, imm
                // Description: Performs bitwise AND on register rs1 and the sign-extended
                // 12-bit
                // immediate and place the result in rd. This is the basic bitwise AND operator
                // but
                // instead of using two registers, it uses a 12-bit immediate which is sign
                // extended.
                // Implementation: x[rd] = x[rs1] & sext(immediate)

                register_1_value = readRegisterFile(asmLine.register_1.getIndex());

                // DEBUG - read byte from memory
                int readByte = memory.readByte(register_1_value);
                // System.out.println("" + (char) readByte);

                value = ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                
                // and operation
                value = register_1_value & value;
                
                // write back
                writeRegisterFile(asmLine.register_0.getIndex(), value);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LUI:
                // rd <- imm20 << 12
                if (printInstructions) {
                    logger.info("lui: " + asmLine);
                }

                // LUI (load upper immediate) is used to build 32-bit constants and uses the
                // U-type format.
                // LUI places the 32-bit U-immediate value into the destination register rd,
                // filling
                // in the lowest 12 bits with zeros.
                writeRegisterFile(asmLine.register_0.getIndex(), (asmLine.numeric_1.intValue() << 12L));
                // writeRegisterFile(asmLine.register_0.getIndex(),
                // asmLine.numeric_1.intValue());
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_AUIPC:
                // Add upper immediate to PC (and store the result into rd)
                // auipc rd, imm
                // rd <- PC + imm20 << 12; pc += 4;
                if (printInstructions) {
                    logger.info("auipc: " + asmLine);
                }
                writeRegisterFile(asmLine.register_0.getIndex(), (int) (pc + (asmLine.numeric_1 << 12L)));
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                if (printInstructions) {
                    logger.info("jal: " + asmLine);
                }

                // registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                // System.out.println("instruction: " + ByteArrayUtil.byteToHex(instruction));

                int imm_10_1 = (asmLine.instruction >> (9 + 12)) & 0b1111111111;
                int imm_11 = (asmLine.instruction >> (8 + 12)) & 0b1;
                int imm_19_12 = (asmLine.instruction >> (0 + 12)) & 0b11111111;
                int imm_20 = (asmLine.instruction >> (19 + 12)) & 0b1;

                int jumpDistance = (imm_20 << 20) + (imm_19_12 << 12) + (imm_11 << 11) + (imm_10_1 << 1);
                // jumpDistance <<= 11;

                // int jumpDistance = (int)
                // NumberParseUtil.sign_extend_20_bit_to_int32_t(asmLine.numeric_1.intValue());
                // System.out.println("jumpDistance: " + ByteArrayUtil.byteToHex(jumpDistance));

                jumpDistance = (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(jumpDistance);
                pc += jumpDistance;
                // System.out.println("newPC: " + ByteArrayUtil.byteToHex(pc));
                break;

            case I_JALR:
                // format: jalr rd, imm(rs1)
                // example: jalr x0, 0(x0)
                //
                // format: jalr t0, t1 ---> jalr t0, t1, 0 ---> jalr t0, 0(t1)
                //
                // rd = pc + 4
                // pc = rs1 + imm
                if (printInstructions) {
                    logger.info("jalr: " + asmLine);
                }

                // DEBUG
                logger.trace("register_1 content: "
                        + ByteArrayUtil.byteToHex(readRegisterFile(asmLine.register_1.getIndex())));

                // pc = rs1 + imm
                immValSignExtended = (int) NumberParseUtil
                        .sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());
                int pcReplacement = readRegisterFile(asmLine.register_1.getIndex()) + immValSignExtended;

                // rd = pc + 4;
                // registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                // DEBUG
                logger.trace("Current PC: " + ByteArrayUtil.byteToHex(pc));
                logger.trace("New PC: " + ByteArrayUtil.byteToHex(pcReplacement));

                // increment PC
                pc = pcReplacement;
                break;

            case I_BEQ:
                // Take the branch if registers rs1 and rs2 are equal.
                // if (x[rs1] == x[rs2]) pc += sext(offset)
                if (printInstructions) {
                    logger.info("beq: " + asmLine);
                }

                register_0_value = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value = readRegisterFile(asmLine.register_1.getIndex());

                // logger.trace(register_0_value + " <-> " + register_1_value);

                // System.out.println("" + (char) register_0_value);

                if (register_0_value == register_1_value) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BNE:
                if (printInstructions) {
                    logger.info("bne: " + asmLine);
                }

                register_0_value = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                if (register_0_value != register_1_value) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BLT:
                // if (rs1 < rs2) pc += imm
                if (printInstructions) {
                    logger.info("blt: " + asmLine);
                }
                // DEBUG
                int blt_a = readRegisterFile(asmLine.register_0.getIndex());
                int blt_b = readRegisterFile(asmLine.register_1.getIndex());
                // Implementation
                if (blt_a < blt_b) {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is taken!");
                    pc += asmLine.numeric_2.intValue();
                } else {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is NOT taken!");
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BGE:
                // bge rs1, sr2, imm
                // if (rs1 >= rs2) pc += imm
                if (printInstructions) {
                    logger.info("bge: " + asmLine);
                }

                register_0_value = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                // DEBUG
                logger.trace("bge " + register_0_value + " >= " + register_1_value);
                // Implementation
                if (register_0_value >= register_1_value) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BLTU:
                if (printInstructions) {
                    logger.info("bltu: " + asmLine);
                }

                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#bltu
                // Branch Less Than Unsigned
                //
                // Format: bltu rs1, rs2, offset
                // Description: Take the branch if registers rs1 is less than
                // rs2, using unsigned comparison.
                // Implementation: if (x[rs1] <u x[rs2]) pc += sext(offset)
                //
                // Unsigned means that the register content are interpreted as
                // being two unsigned numbers!
                //
                // Imagine X_LEN is 4
                // 1. Erste Stelle anschauen: wenn Ziffer = 1: Zahl negativ, Ziffer = 0: Zahl
                // positiv.
                // 2. Zahl ist positiv: Umrechnung vom Binärsystem ins Dezimalsystem ist bereits
                // möglich;
                // 3. Zahl ist negativ: Man subtrahiert 1 und negiert die einzelnen Ziffern.
                // (Dieser Schritt lässt sich für den Menschen vereinfachen: Man negiert zuerst
                // die einzelnen Ziffern und addiert hinterher 1, was zum selben Ergebnis
                // führt.)
                // 4. Die entstandene, entsprechend positive Zahl im Binärsystem rechnet man ins
                // Dezimalsystem um.
                // 5. Wenn negativ, ein „−“ vor die Zahl setzen.
                //
                // https://www.exploringbinary.com/twos-complement-converter/
                //
                // 1. 0010
                // 3. 0010 = 2d
                //
                // 1. 1010
                // 3. 1010 - 1 -> 1001 -> 0110 = 6d
                // 5. 6d -> -6d
                //
                // 1. 1001
                // 3. 1001 - 1 -> 1000 -> 0111 = 7d
                // 5. 7d -> -7d
                //
                // Imagine X_LEN is 4.
                //
                // rs1 = 1001 which is signed: -7d and unsigned: 9d
                // rs2 = 0010 which is signed: 2d and unsigned: 2d
                //
                // blt 1001, 0010, <offset> --> blt -7d < 2d is TRUE, branch is taken
                // bltu 1001, 0010, <offset> --> bltu 9d < 2d is FALSE, branch is NOT taken
                //
                // main:
                // li t0, -3
                // lui t1, 2
                // bltu t0, t1, -8
                // blt t0, t1, -12
                //
                // Although both instructions, look at the same operands, bltu does not
                // perform the jump whereas blt performs the jump (creating an endless loop)
                //
                // Why?
                //
                // bltu interprets the operands as unsigned values.
                // -3 (0xFFFFFFFD) as unsigned is not interpreted as twos-complement and
                // becomes a very large positive number (4294967293)
                // 2 remains the value 2.
                // bltu 4294967293, 2, -8 performs the comparison 4294967293 < 2 which is false
                // and the jump is not taken
                //
                // blt interprets the operands as signed values
                // -3 remains -3, 2 remains 2
                // blt -3, 2, -12 performs the comparison -3 < 2 and the jump is taken

                // https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
                register_0_value_l = readRegisterFile(asmLine.register_0.getIndex()) & 0x00000000ffffffffL;
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                if (register_0_value_l < register_1_value_l) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_BGEU:
                if (printInstructions) {
                    logger.info("bgeu: " + asmLine);
                }

                // https://stackoverflow.com/questions/9578639/best-way-to-convert-a-signed-integer-to-an-unsigned-long
                register_0_value_l = readRegisterFile(asmLine.register_0.getIndex()) & 0x00000000ffffffffL;
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                if (register_0_value_l >= register_1_value_l) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += asmLine.encodedLength;
                }
                break;

            case I_LB:
                if (printInstructions) {
                    logger.info("lb: " + asmLine);
                }

                // logger.trace(asmLine.toString());
                
                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                //addr &= 0x00000000FFFFFFFF;
                // addr = 0x80000000;
                
                //logger.trace("addr: " + addr);
                
                // read from memory (MEMORY STAGE)
                value = (int) NumberParseUtil.sign_extend_8_bit_to_int32_t(memory.getByte(addr));

                // logger.info("" + (char) let[0]);
                // WRITE BACK STAGE
                // place read value into the destination register
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lb");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + value);
                logger.trace(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;                
                break;

            case I_LH:
                if (printInstructions) {
                    logger.info("lh: " + asmLine);
                }

                // LH loads a 16-bit value from memory, then sign-extends to 32-bits before
                // storing in rd

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // read from memory (MEMORY STAGE)
                let = new byte[2];
                let[0] = (byte) memory.getByte(addr + 0);
                let[1] = (byte) memory.getByte(addr + 1);

                // WB: I do not know why it works with BigEndian here although the ELF if little
                // endian!
                int lhVal = ByteArrayUtil.decodeInt16FromArrayBigEndian(let, 0);
                int lhValSignExtended = (int) NumberParseUtil.sign_extend_16_bit_to_int32_t(lhVal);

                // System.out.println(ByteArrayUtil.byteToHex(lhVal));

                // WRITE BACK STAGE
                // place read value into the destination register
                writeRegisterFile(asmLine.register_0.getIndex(), lhValSignExtended);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LHU:
                if (printInstructions) {
                    logger.info("lhu: " + asmLine);
                }

                // LH loads a 16-bit value from memory, then sign-extends to 32-bits before
                // storing in rd

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // read from memory (MEMORY STAGE)
                let = new byte[2];
                let[0] = (byte) memory.getByte(addr + 0);
                let[1] = (byte) memory.getByte(addr + 1);

                // WB: I do not know why it works with BigEndian here although the ELF if little
                // endian!
                int lhuVal = ByteArrayUtil.decodeInt16FromArrayBigEndian(let, 0);

                // logger.info("" + (char) lhuVal);

                // int lhValSignExtended = (int)
                // NumberParseUtil.sign_extend_16_bit_to_int32_t(lhVal);

                // System.out.println(ByteArrayUtil.byteToHex(lhuVal));

                // WRITE BACK STAGE
                // place read value into the destination register
                writeRegisterFile(asmLine.register_0.getIndex(), lhuVal);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LW:
                if (printInstructions) {
                    logger.info("lw: " + asmLine);
                }

                // logger.trace(asmLine.toString());

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("addr: " + addr);

                // read from memory (MEMORY STAGE)
                let = new byte[4];
                let[0] = (byte) memory.getByte(addr + 0);
                let[1] = (byte) memory.getByte(addr + 1);
                let[2] = (byte) memory.getByte(addr + 2);
                let[3] = (byte) memory.getByte(addr + 3);

                // WRITE BACK STAGE
                // place read value into the destination register
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.BIG_ENDIAN);
                value = ByteArrayUtil.fourByteToInt(let, ByteOrder.LITTLE_ENDIAN);

                logger.trace("" + (char) value);

                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lw");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                logger.trace(stringBuilder.toString());

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_LBU:
                if (printInstructions) {
                    logger.info("lbu: " + asmLine);
                }
                try {
                    addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                    
                    // DEBUG
                    if (logger.isTraceEnabled()) {
                        logger.trace("addr: " + ByteArrayUtil.byteToHex(addr) + " " + addr);
                    }

                    // read byte from memory
                    value = memory.getByte(addr);
                    
                    // DEBUG
                    if (logger.isTraceEnabled()) {
                        logger.trace("" + (char) value);
                    }

                    // store value into rd register
                    writeRegisterFile(asmLine.register_0.getIndex(), value);
                    
                    // increment PC
                    pc += asmLine.encodedLength;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                break;

            case I_LBW:
                if (printInstructions) {
                    logger.info("lbw: " + asmLine);
                }
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);

            case I_SB:
                if (printInstructions) {
                    logger.info("sb: " + asmLine);
                }
                logger.trace("GP: " + readRegisterFile(RISCVRegister.REG_GP.getIndex()));

                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("Address: " + ByteArrayUtil.byteToHex(addr));

                // retrieve the value to write into the address
                value = readRegisterFile(asmLine.register_0.getIndex());
                logger.trace("Value: " + ByteArrayUtil.byteToHex(value));

                // memory.print(0x80002000, 0x80002020, ByteOrder.LITTLE_ENDIAN);
                memory.storeByte(addr + 0, (byte) (value & 0xFF));
                // memory.print(0x80002000, 0x80002020, ByteOrder.LITTLE_ENDIAN);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SH:
                if (printInstructions) {
                    logger.info("sh: " + asmLine);
                }
                // Store 16-bit, values from the low bits of register rs2 to memory.
                // sh rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][15:0]
                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                // retrieve the value to write into the address
                value = readRegisterFile(asmLine.register_0.getIndex());
                let = ByteArrayUtil.intToTwoByte(value, ByteOrder.LITTLE_ENDIAN);
                // write value into memory (MEMORY STAGE)
                memory.storeByte(addr + 0, let[0]);
                memory.storeByte(addr + 1, let[1]);
                // Increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SW:
                if (printInstructions) {
                    logger.info("sw: " + asmLine);
                }
                // Store 32-bit, values from the low bits of register rs2 to memory.
                // sw rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][31:0]
                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                // retrieve the value to write into the address
                value = readRegisterFile(asmLine.register_0.getIndex());
                // logger.info("" + (char) value);
                let = ByteArrayUtil.intToFourByte(value, ByteOrder.LITTLE_ENDIAN);
                // write value into memory (MEMORY STAGE)
                memory.storeByte(addr + 0, let[0]);
                memory.storeByte(addr + 1, let[1]);
                memory.storeByte(addr + 2, let[2]);
                memory.storeByte(addr + 3, let[3]);

                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("sw");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                logger.trace(stringBuilder.toString());

                // Increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTI:
                if (printInstructions) {
                    logger.info("slti: " + asmLine);
                }
                // set less than immediate
                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#slti
                //
                // Format: slti rd, rs1, imm
                //
                // Description: Place the constant value 1 (literally a 1)
                // in register rd if register rs1
                // is less than the signextended immediate when both are treated
                // as signed numbers, else 0 is written to rd.
                //
                // Implementation: x[rd] = x[rs1] <s sext(immediate)
                value = readRegisterFile(asmLine.register_1.getIndex());
                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());
                writeRegisterFile(asmLine.register_0.getIndex(), (value < immediate_value) ? 1 : 0);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTIU:
                if (printInstructions) {
                    logger.info("sltiu: " + asmLine);
                }
                // throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/sltiu.html

                // sltiu rd, rs1, imm

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                // register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) &
                // 0x00000000ffffffffL;
                long immediate_value_long = asmLine.numeric_2.intValue() & 0x00000000ffffffffL;

                if (register_1_value_l < immediate_value_long) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_XORI:
                if (printInstructions) {
                    logger.info("xori: " + asmLine);
                }

                // xori rd,rs1,imm
                // x[rd] = x[rs1] ^ sext(immediate)

                value = readRegisterFile(asmLine.register_1.getIndex())
                        ^ ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                writeRegisterFile(asmLine.register_0.getIndex(), value);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_ORI:
                if (printInstructions) {
                    logger.info("ori: " + asmLine);
                }

                long par1 = readRegisterFile(asmLine.register_1.getIndex());
                long par2 = ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));

                logger.trace(ByteArrayUtil.byteToHex((int) par1));
                logger.trace(ByteArrayUtil.byteToHex((int) par2));

                long oriResult = par1 | par2;

                logger.trace(ByteArrayUtil.byteToHex((int) oriResult));

                writeRegisterFile(asmLine.register_0.getIndex(), (int) oriResult);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLLI:
                // SLLI (Shift Left Logical Immediate)
                // slli rd, rs1, imm # rd = rs1 << imm
                if (printInstructions) {
                    logger.info("slli: " + asmLine);
                }

                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                value = readRegisterFile(asmLine.register_1.getIndex()) << immediate_value;
                writeRegisterFile(asmLine.register_0.getIndex(), value);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRLI:
                if (printInstructions) {
                    logger.info("srli: " + asmLine);
                }

                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                value_l = register_1_value_l >> immediate_value;
                writeRegisterFile(asmLine.register_0.getIndex(), (int) value_l);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRAI:
                if (printInstructions) {
                    logger.info("srai: " + asmLine);
                }
                // SRAI (Shift Right Arithmetic Immediate)
                // srai rd, rs1, shamt
                // example: srai a5, a5, 1
                // definition: x[rd] = x[rs1] >>s shamt
                //
                // The difference to the logical form SRLI (Shift Right Logical Immediate)
                // is that SRLI always shifts in zeroes from the left while SRAI shifts in
                // the sign-bit from the left to keep the sign of the number correct
                int registerValue = readRegisterFile(asmLine.register_1.getIndex());
                int signBit = registerValue >= 0 ? 0 : 1;
                for (int i = 0; i < asmLine.numeric_2; i++) {
                    registerValue >>= 1;
                    registerValue |= (signBit << 31);
                }
                writeRegisterFile(asmLine.register_0.getIndex(), registerValue);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SUB:
                if (printInstructions) {
                    logger.info("sub: " + asmLine);
                }
                // Subtracts the register rs2 from rs1 and stores the result
                // in rd. Arithmetic overflow is ignored and the result is
                // simply the low XLEN bits of the result.
                // sub rd,rs1,rs2
                // x[rd] = x[rs1] - x[rs2]

                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // - registerFile[asmLine.register_2.getIndex()];

                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = readRegisterFile(asmLine.register_2.getIndex());
                result = register_1_value - register_2_value;
                
                writeRegisterFile(asmLine.register_0.getIndex(), result);

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLL:
                if (printInstructions) {
                    logger.info("sll: " + asmLine);
                }

                int shiftValue = readRegisterFile(asmLine.register_2.getIndex());

                // immediate_value = (int)
                // NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                value = readRegisterFile(asmLine.register_1.getIndex()) << shiftValue;
                writeRegisterFile(asmLine.register_0.getIndex(), value);
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLT:
                if (printInstructions) {
                    logger.info("slt: " + asmLine);
                }
                // Set if less than
                // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html#slt
                //
                // Place the constant value 1 in register rd if register rs1 is less
                // than register rs2 when both are treated as signed numbers,
                // else 0 is written to rd.
                //
                // slt rd, rs1, rs2
                // x[rd] = x[rs1] <s x[rs2]
                if (readRegisterFile(asmLine.register_1.getIndex()) < readRegisterFile(asmLine.register_2.getIndex())) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }
                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SLTU:
                if (printInstructions) {
                    logger.info("sltu: " + asmLine);
                }
                // https://msyksphinz-self.github.io/riscv-isadoc/#_sltu
                //
                // Place the value 1 in register rd if register rs1 is less than
                // register rs2 when both are treated as unsigned numbers, else 0 is written to
                // rd.
                //
                // sltu rd,rs1,rs2
                // x[rd] = x[rs1] <u x[rs2]

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) & 0x00000000ffffffffL;

                if (register_1_value_l < register_2_value_l) {
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }

                // increment PC
                pc += asmLine.encodedLength;
                break;

            case I_XOR:
                if (printInstructions) {
                    logger.info("xor: " + asmLine);
                }
                // xor rd,rs1,rs2
                // x[rd] = x[rs1] ^ x[rs2]

                value = readRegisterFile(asmLine.register_1.getIndex())
                        ^ readRegisterFile(asmLine.register_2.getIndex());

                logger.trace(ByteArrayUtil.byteToHex(value));

                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += asmLine.encodedLength;
                break;

            case I_SRL:
                if (printInstructions) {
                    logger.info("srl: " + asmLine);
                }

                // Shift right logical
                // format: srl rd, rs1, rs2

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) & 0x00000000ffffffffL;

                logger.trace(ByteArrayUtil.byteToHex((int) register_1_value_l));
                logger.trace(ByteArrayUtil.byteToHex((int) register_2_value_l));

                value_l = register_1_value_l >> register_2_value_l;

                logger.trace(ByteArrayUtil.byteToHex((int) value_l));

                writeRegisterFile(asmLine.register_0.getIndex(), (int) value_l);

                // Increment PC
                pc += asmLine.encodedLength;
                break;

            case I_SRA:
                if (printInstructions) {
                    logger.info("sra: " + asmLine);
                }

                // Shift right arithmetic
                // format: sra rd, rs1, rs2

                value = readRegisterFile(asmLine.register_1.getIndex()) >> readRegisterFile(
                        asmLine.register_2.getIndex());
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                // Increment PC
                pc += asmLine.encodedLength;
                break;

            case I_OR:
                if (printInstructions) {
                    logger.info("or: " + asmLine);
                }
                // Performs bitwise OR on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // | registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        | readRegisterFile(asmLine.register_2.getIndex()));

                pc += asmLine.encodedLength;
                break;

            // case I_FENCE:
            // break;
            // case I_FENCE_I:
            // break;

            case I_ECALL: {
                if (printInstructions) {
                    logger.info("ecall: " + asmLine);
                }

                // a7 describes the service that is called by the ecall
                int regA7Value = readRegisterFile(RISCVRegister.REG_A7.getIndex());
                switch (regA7Value) {

                    // ???
                    case 0x50: // 80dec
                        logger.trace("putchar()");
                        register_1_value = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0xD6: // (214dec)
                        register_1_value = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0x40: // (64dec)
                        register_1_value = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 0:
                        logger.warn("Unknown ECALL 0!");
                        break;

                    case 92: // 92dec (pfnStreamWriteBufFunc)
                        register_0_value_l = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        printStringFromAddress((int) register_0_value_l);
                        break;

                    case 0x5D: // 93dec (exit)
                        System.out.println("exit()");

                        // the unit tests https://github.com/riscv-software-src/riscv-tests
                        // write a value into a0 that describes success or failure.
                        // 0 = success, non-zero = failure
                        register_0_value_l = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        if (register_0_value_l == 0) {
                            logger.info("Unit Test Success!");
                        } else {
                            // The RISCV unit tests write the index of the unit test into GP but
                            // shift it left and add 1 for some wierd reason
                            int globalPointerValue = readRegisterFile(RISCVRegister.REG_GP.getIndex());
                            globalPointerValue >>= 1;
                            throw new RuntimeException("Unit Test " + globalPointerValue + " Failed!");
                        }
                        return false;

                    case 0x5E: // 94dec (time)
                        // System.out.println("time()");

                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("addressA0: " + ByteArrayUtil.byteToHex(addressA0));

                        int dataA0 = memory.readWord(addressA0, ByteOrder.LITTLE_ENDIAN);
                        // System.out.println("dataA0: " + ByteArrayUtil.byteToHex(dataA0));

                        Calendar calendar = Calendar.getInstance();
                        long timeInMillis = calendar.getTimeInMillis();

                        // System.out.println("millis: " + timeInMillis);

                        byte[] timeInMillisAsArray = ByteArrayUtil.longToBytes(timeInMillis);

                        memory.storeByte(addressA0 + 0, (byte) timeInMillisAsArray[7]);
                        // System.out.println("[7]: " + (byte) ((int) (timeInMillisAsArray[7]& 0xFF) ));
                        memory.storeByte(addressA0 + 1, (byte) timeInMillisAsArray[6]);
                        // System.out.println("[6]: " + (byte) ((int) (timeInMillisAsArray[6]) & 0xFF));
                        memory.storeByte(addressA0 + 2, (byte) timeInMillisAsArray[5]);
                        memory.storeByte(addressA0 + 3, (byte) timeInMillisAsArray[4]);
                        memory.storeByte(addressA0 + 4, (byte) timeInMillisAsArray[3]);
                        memory.storeByte(addressA0 + 5, (byte) timeInMillisAsArray[2]);
                        memory.storeByte(addressA0 + 6, (byte) timeInMillisAsArray[1]);
                        memory.storeByte(addressA0 + 7, (byte) timeInMillisAsArray[0]);

                        // memory.storeByte(addressA0 + 0, (byte) 0x78);
                        // memory.storeByte(addressA0 + 1, (byte) 0x56);
                        // memory.storeByte(addressA0 + 2, (byte) 0x34);
                        // memory.storeByte(addressA0 + 3, (byte) 0x12);
                        // memory.storeByte(addressA0 + 4, (byte) 0x78);
                        // memory.storeByte(addressA0 + 5, (byte) 0x56);
                        // memory.storeByte(addressA0 + 6, (byte) 0x34);
                        // memory.storeByte(addressA0 + 7, (byte) 0x12);

                        // writeRegisterFile(RISCVRegister.REG_A0.getIndex(), 18);
                        // writeRegisterFile(RISCVRegister.REG_A1.getIndex(), 18);
                        // writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 18);
                        break;

                    case 0x5F: // 95dec (localtime)
                        // System.out.println("localtime()");

                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("addressA0: " + ByteArrayUtil.byteToHex(addressA0));

                        byte[] timeInMillisAsArray2 = new byte[8];

                        // timeInMillisAsArray2[7] = (byte) (((int) memory.getByte(addressA0 + 0)) &
                        // 0xFF);
                        // System.out.println("[7]: " + (byte) timeInMillisAsArray2[7]);
                        // timeInMillisAsArray2[6] = (byte) (((int) memory.getByte(address_A0 + 1)) &
                        // 0xFF);
                        // System.out.println("[6]: " + (byte) (int) timeInMillisAsArray2[6]);

                        timeInMillisAsArray2[7] = (byte) memory.getByte(addressA0 + 0);
                        timeInMillisAsArray2[6] = (byte) memory.getByte(addressA0 + 1);
                        timeInMillisAsArray2[5] = (byte) memory.getByte(addressA0 + 2);
                        timeInMillisAsArray2[4] = (byte) memory.getByte(addressA0 + 3);
                        timeInMillisAsArray2[3] = (byte) memory.getByte(addressA0 + 4);
                        timeInMillisAsArray2[2] = (byte) memory.getByte(addressA0 + 5);
                        timeInMillisAsArray2[1] = (byte) memory.getByte(addressA0 + 6);
                        timeInMillisAsArray2[0] = (byte) memory.getByte(addressA0 + 7);

                        long timeInMillisUTC = ByteArrayUtil.bytesToLong(timeInMillisAsArray2);
                        // System.out.println("millis: " + timeInMillisUTC);

                        // struct tm
                        // {
                        // int tm_sec; // seconds after the minute - [0, 60] including leap second
                        // int tm_min; // minutes after the hour - [0, 59]
                        // int tm_hour; // hours since midnight - [0, 23]
                        // int tm_mday; // day of the month - [1, 31]
                        // int tm_mon; // months since January - [0, 11]
                        // int tm_year; // years since 1900
                        // int tm_wday; // days since Sunday - [0, 6]
                        // int tm_yday; // days since January 1 - [0, 365]
                        // int tm_isdst; // daylight savings time flag
                        // };

                        // divide the total milliseconds by 3,600,000 to get the total hours, then find
                        // the remainder
                        int hours = (int) (timeInMillisUTC / 3600000L);
                        hours = hours % 24;
                        // System.out.println("hh: " + hours);

                        int mins = (int) (timeInMillisUTC / 60000L);
                        mins = mins % 60;
                        // System.out.println("mm: " + mins);

                        int seconds = (int) (timeInMillisUTC / 1000L);
                        seconds = seconds % 60;
                        // System.out.println("ss: " + seconds);

                        // static memory for the structure to which the localtime() function
                        // returns a pointer
                        int resultAddress = 0x50000;
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), resultAddress);

                        // tm_sec
                        byte[] temp = ByteArrayUtil.intToFourByte(seconds, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte(resultAddress + 0, (byte) temp[0]);
                        memory.storeByte(resultAddress + 1, (byte) temp[1]);
                        memory.storeByte(resultAddress + 2, (byte) temp[2]);
                        memory.storeByte(resultAddress + 3, (byte) temp[3]);

                        // tm_min
                        temp = ByteArrayUtil.intToFourByte(mins, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte(resultAddress + 4, (byte) temp[0]);
                        memory.storeByte(resultAddress + 5, (byte) temp[1]);
                        memory.storeByte(resultAddress + 6, (byte) temp[2]);
                        memory.storeByte(resultAddress + 7, (byte) temp[3]);

                        // tm_sec
                        temp = ByteArrayUtil.intToFourByte(hours, ByteOrder.LITTLE_ENDIAN);
                        memory.storeByte(resultAddress + 8, (byte) temp[0]);
                        memory.storeByte(resultAddress + 9, (byte) temp[1]);
                        memory.storeByte(resultAddress + 10, (byte) temp[2]);
                        memory.storeByte(resultAddress + 11, (byte) temp[3]);

                        // System.out.println("done");
                        break;

                    case 0x60: // 96dec (test_func)
                        System.out.println("test_func()");
                        // https://msyksphinz-self.github.io/riscv-isadoc/#_ecall
                        // writeRegisterFile(RISCVRegister.REG_A0.getIndex(), 15);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 14);
                        break;

                    case 97: // 97dec (fseek)
                        // System.out.println("fseek()");
                        fileHandle = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        int newSeekPosition = readRegisterFile(RISCVRegister.REG_A1.getIndex());
                        // System.out.println("fseek() newSeekPosition = " + newSeekPosition);
                        int seekMode = readRegisterFile(RISCVRegister.REG_A2.getIndex());
                        // System.out.println("fseek() seekMode = " + seekMode);

                        // origin Beschreibung
                        // SEEK_SET (0) Der Start des Streams markiert den Ursprung.
                        // SEEK_CUR Aktuelle Position im Stream setzt den Ursprung.
                        // SEEK_END Das Ende des Streams wird als Ursprung gewählt.

                        int fseekResult = fileHandling.fseek(fileHandle, newSeekPosition, seekMode);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), fseekResult);
                        break;

                    case 98: // 98dec (getc)
                        // System.out.println("getc()");
                        fileHandle = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        // System.out.println("getc() addressA0=" + fileHandle);
                        int resultChar = fileHandling.getc(fileHandle);
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), resultChar);
                        break;

                    case 99: // 99dec (fopen)
                        logger.trace("fopen()");

                        // path to file as string is stored via the address in addressA0
                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        String filepath = toStringFromAddress(addressA0);
                        logger.trace("fopen() filepath=\"" + filepath + "\"");

                        // file mode as string is stored via the address in addressA1
                        addressA1 = readRegisterFile(RISCVRegister.REG_A1.getIndex());
                        String filemode = toStringFromAddress(addressA1);
                        logger.trace("fopen() filemode=\"" + filemode + "\"");

                        fileHandle = fileHandling.fopen(filepath, filemode);

                        // return a FILE handle
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), fileHandle);
                        break;

                    case 100: // 100dec (ftell)
                        logger.trace("ftell()");

                        // first parameter is the fileHandle
                        fileHandle = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        int filePos = fileHandling.ftell(fileHandle);

                        // return a FILE position
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), filePos);
                        break;

                    case 101: // 101dec (putchar)
                        logger.trace("putchar()");
                        register_1_value = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        System.out.print((char) register_1_value);
                        break;

                    case 102: // 102dec (fclose)
                        logger.trace("fclose()");

                        // first parameter is the fileHandle
                        fileHandle = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                        fileHandling.fclose(fileHandle);

                        // return 0 on success, EOF on error
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), 0);
                        break;

                    case 103: // 103dec (fgets)
                        logger.trace("fgets()");

                        // first parameter is the byte buffer to fill
                        addressA0 = readRegisterFile(RISCVRegister.REG_A0.getIndex());

                        // second parameter is the max allowed length defined by the caller
                        addressA1 = readRegisterFile(RISCVRegister.REG_A1.getIndex());

                        // third parameter is the file handle to read from (can also be stdin)
                        addressA2 = readRegisterFile(RISCVRegister.REG_A2.getIndex());

                        if (addressA2 == FileHandling.STDIN) {

                            String inputValue = getInput();
                            int inputValueLength = inputValue.length();

                            inputValue = inputValue.substring(0, Math.min(addressA1, inputValueLength));

                            logger.trace("inputValue: \"" + inputValue + "\"");

                            for (int i = 0; i < inputValue.length(); i++) {
                                char inputValueAsChar = inputValue.charAt(i);
                                memory.storeByte(addressA0 + i, (byte) inputValueAsChar);

                                if (inputValueAsChar == 0) {
                                    break;
                                }
                            }

                        } else {
                            throw new RuntimeException("not implemented yet!");
                        }

                        // return parameter 0
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), addressA0);
                        break;

                    // rand()
                    case 104:
                        int min = 0;
                        int max = 32767;
                        int randomValue = random.nextInt(max + 1 - min) + min;
                        writeRegisterFile(RISCVRegister.REG_A5.getIndex(), randomValue);
                        break;

                    default:
                        singleStepping = true;
                        printMemoryAroundPC(10);
                        throw new RuntimeException("ECALL " + ByteArrayUtil.byteToHex(regA7Value) + " (" + regA7Value
                                + ") NOT IMPLEMENTED!");
                }
            }
                pc += asmLine.encodedLength;
                break;

            case I_FENCE:
                if (printInstructions) {
                    logger.info("fence: " + asmLine);
                }
                pc += asmLine.encodedLength;
                break;

            case I_EBREAK:
                if (printInstructions) {
                    logger.info("ebreak: " + asmLine);
                }

                // singleStepping = true;
                // debugASMLineOutput = true;

                printMemoryAroundPC(5);

                pc += asmLine.encodedLength;
                break;

            case I_NOP:
                if (printInstructions) {
                    logger.info("nop: " + asmLine);
                }
                // logger.trace("mnemonic: NOP");
                pc += asmLine.encodedLength;
                break;

            case I_BRK:
                if (printInstructions) {
                    logger.info("brk: " + asmLine);
                }
                // logger.trace("mnemonic: BRK");
                pc += asmLine.encodedLength;
                break;

            // case I_PUTS:
            // logger.info("mnemonic: PUTS");

            // // the start address of the zero terminated string is expected in A0
            // int startAddress = readRegisterFile(RISCVRegister.REG_A0.getIndex());
            // logger.info("startAddress: " + startAddress);

            // stringBuilder = new StringBuilder();
            // while (true) {
            // int tempByte = memory.getByte(startAddress);
            // if (tempByte == 0x00) {
            // break;
            // } else {
            // stringBuilder.append((char) tempByte);
            // }

            // startAddress++;
            // }

            // logger.trace(stringBuilder.toString());

            // pc += asmLine.encodedLength;
            // break;

            case I_MRET:
                if (printInstructions) {
                    logger.info("mret: " + asmLine);
                }
                logger.warn("I_MRET Not implemented yet!");
                pc += asmLine.encodedLength;
                break;

            //
            // Zifencei Extension
            //

            case I_FENCEI:
                if (printInstructions) {
                    logger.info("fencei: " + asmLine);
                }
                // TODO
                logger.warn("I_FENCEI Not implemented yet!");
                pc += asmLine.encodedLength;
                break;

            //
            // Ziscr Extension
            //

            // break;
            // case I_CSRRW:
            // break;
            // case I_CSRRC:
            // break;
            // case I_CSRRWI:
            // break;
            // case I_CSRRSI:
            // break;
            // case I_CSRRCI:
            // break;

            /**
             * 1. Read the value from the CRS register csr and store it into rd
             * 2. Modify the csr value by performing a bit-wise OR between csr and rd
             * 3. Write back the result from step 2 into the CSR register csr
             */
            case I_CSRRS:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrs.html

                if (printInstructions) {
                    logger.info("I_CSRRS: " + asmLine);
                }

                // System.out.println(asmLine.numeric_1);

                // 1. Read the value from the CRS register csr and store it into rd

                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // 1. Read the value from the CRS register csr and store it into rd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // 2. Modify the csr value by performing a bit-wise OR between csr and rd
                int newCsrValue = csrValue | readRegisterFile(asmLine.register_2.getIndex());

                // 3. Write back the result from step 2 into the CSR register csr
                // writeRegisterFile(asmLine.register_1.getIndex(), newCsrValue);
                heartIdCSRRegister = newCsrValue;

                pc += asmLine.encodedLength;
                break;

            case I_CSRRW:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrw.html

                if (printInstructions) {
                    logger.info("I_CSRRW: " + asmLine);
                }

                // 0. If xd=x0, then the instruction shall not read the CSR and shall not cause
                // any of the side effects that might occur on a CSR read.
                if (asmLine.register_0 == RISCVRegister.REG_X0) {
                    pc += asmLine.encodedLength;
                    break;
                }

                // 1. Read the old value of the CSR, zero-extends the value to XLEN bits
                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // write csrValue to integer register xd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // The initial value in xs1 is written to the CSR.
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());
                writeCSRById(asmLine.numeric_1.intValue(), (int) register_2_value_l);

                pc += asmLine.encodedLength;
                break;

            case I_CSRRWI:
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/csrrwi.html

                if (printInstructions) {
                    logger.info("I_CSRRWI: " + asmLine);
                }

                // 0. If xd=x0, then the instruction shall not read the CSR and shall not cause
                // any of the side effects that might occur on a CSR read.
                if (asmLine.register_0 == RISCVRegister.REG_X0) {
                    pc += asmLine.encodedLength;
                    break;
                }

                // 1. Read the old value of the CSR, zero-extends the value to XLEN bits
                csrId = asmLine.numeric_1.intValue();
                csrValue = readCSRById(csrId);

                // write csrValue to integer register xd
                writeRegisterFile(asmLine.register_0.getIndex(), csrValue);

                // The initial value in xs1 is written to the CSR.
                register_2_value_l = asmLine.numeric_2;
                writeCSRById(asmLine.numeric_1.intValue(), (int) register_2_value_l);

                pc += asmLine.encodedLength;
                break;

            //
            // Extension M
            //

            //
            // multiplication extension
            //

            case I_MUL:
                if (printInstructions) {
                    logger.info("mul: " + asmLine);
                }

                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        * readRegisterFile(asmLine.register_2.getIndex()));
                pc += asmLine.encodedLength;
                break;

            case I_MULH:
                if (printInstructions) {
                    logger.info("mulh: " + asmLine);
                }

                // MUL performs an XLEN-bit×XLEN-bit multiplication of rs1 by rs2 and places the
                // lower XLEN bits in the destination register. MULH, MULHU, and MULHSU perform
                // the same multiplication but return the upper XLEN bits of the full 2×XLEN-bit
                // product, for signed×signed, unsigned×unsigned, and rs1×unsigned rs2
                // multiplication.

                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex());

                long mulhResult = register_1_value_l * register_2_value_l;
                int mulhResultShifted = (int) ((mulhResult >> 32) & 0xFFFFFFFF);

                writeRegisterFile(asmLine.register_0.getIndex(), mulhResultShifted);
                pc += asmLine.encodedLength;
                break;

            case I_DIV:
                if (printInstructions) {
                    logger.info("div: " + asmLine);
                }

                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = readRegisterFile(asmLine.register_2.getIndex());

                int divResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    divResult = 0xFFFFFFFF;
                } else {
                    divResult = register_1_value / register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), divResult);

                pc += asmLine.encodedLength;
                break;

            case I_DIVU:
                if (printInstructions) {
                    logger.info("divu: " + asmLine);
                }

                // register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                // register_2_value = readRegisterFile(asmLine.register_2.getIndex());
                register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                register_2_value_l = readRegisterFile(asmLine.register_2.getIndex()) & 0x00000000ffffffffL;

                // int divuResult = 0;
                long divuResult = 0L;
                // if (register_2_value == 0) {
                if (register_2_value_l == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    divuResult = 0xFFFFFFFF;
                } else {
                    divuResult = register_1_value_l / register_2_value_l;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), (int) divuResult);

                pc += asmLine.encodedLength;
                break;

            case I_REM:
                if (printInstructions) {
                    logger.info("rem: " + asmLine);
                }

                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = readRegisterFile(asmLine.register_2.getIndex());

                int remResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    remResult = 0xFFFFFFFF;
                } else {
                    remResult = register_1_value % register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), remResult);

                pc += asmLine.encodedLength;
                break;

            case I_REMU:
                if (printInstructions) {
                    logger.info("remu: " + asmLine);
                }

                // Unsigned remainder -
                // https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/remu.html
                // Calculate the remainder of unsigned division of xs1 by xs2, and store the
                // result in xd.
                // logger.trace("mnemonic: REMU");

                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                register_2_value = readRegisterFile(asmLine.register_2.getIndex());

                int remuResult = 0;
                if (register_2_value == 0) {
                    // https://projectf.io/posts/riscv-multiply-divide/
                    // RISC-V doesn’t raise an exception on divide by zero.
                    // The result of dividing by zero is all 1s, 0xFFFFFFFF in hexadecimal.
                    // For unsigned numbers, this is the largest integer; for signed numbers, this
                    // is -1.
                    remuResult = 0xFFFFFFFF;
                } else {
                    remuResult = register_1_value % register_2_value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), remuResult);

                pc += asmLine.encodedLength;
                break;

            //
            // V-Extension (RVV Vector Extension)
            //

            // https://rvv-isadoc.readthedocs.io/en/latest/configure.html#vsetvli
            case I_VSETVLI:
                logger.warn("I_VSETVLI not implemented! " + asmLine);
                // int upperOpCode = (instruction >> 25) & 0b111111;
                // switch (upperOpCode) {
                // case 0b011001: // one of vmsne{.vv, .vx, .vi}
                // logger.warn("I_VMSNE not implemented! ASMLine: " + asmLine.toString());
                // break;

                // default: // I_VSETVLI
                // logger.warn("I_VSETVLI not implemented!");
                // break;
                // }
                pc += asmLine.encodedLength;
                break;

            case I_VLE32_V:
                logger.warn("I_VLE32_V not implemented! " + asmLine);
                pc += asmLine.encodedLength;
                break;

            case I_VSE32_V:
                logger.warn("I_VSE32_V not implemented! " + asmLine);
                pc += asmLine.encodedLength;
                break;

            // https://rvv-isadoc.readthedocs.io/en/latest/arith_integer.html#vmsne
            case I_VMSNE_VI:
                logger.warn("I_VMSNE_VI not implemented! " + asmLine);
                pc += asmLine.encodedLength;
                break;

            case I_VADD_VV:
                logger.warn("I_VADD_VV not implemented! " + asmLine);
                pc += asmLine.encodedLength;
                break;

            case I_VMV_V_I:
                logger.warn("I_VMV_V_I not implemented! " + asmLine);
                pc += asmLine.encodedLength;
                break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic + " machine code: "
                        + ByteArrayUtil.byteToHex(asmLine.instruction));
        }

        return true;
    }

    private void writeCSRById(int index, int register_2_value_l) {
        logger.warn("writeCSR Has to be implemented!");
    }

    private int readCSRById(int csrId) {

        int csrValue = 0;

        // https://www.five-embeddev.com/riscv-user-isa-manual/latest-adoc/zicsr.html
        switch (csrId) {

            case 0x180:
                break;

            case 0x300:
                break;

            case 0x302:
                break;

            case 0x303:
                break;

            case 0x304:
                break;

            // mtvec 0x305 == 773dec
            case 0x305:
                break;

            case 0x341:
                break;

            case 0x3A0:
                break;

            case 0x3B0:
                break;

            // Number Privilege Name Description
            // 0xF11 MRO mvendorid Vendor ID.
            case 0xF11:
                break;

            // 0xF12 MRO marchid Architecture ID.
            case 0xF12:
                break;

            // 0xF13 MRO mimpid Implementation ID.
            case 0xF13:
                break;

            // 0xF14 MRO mhartid Hardware thread ID.
            case 0xF14:
                // System.out.println(asmLine.numeric_1);
                csrValue = heartIdCSRRegister;
                break;

            // 0xF15 MRO mconfigptr Pointer to configuration data structure
            case 0xF15:
                break;

            case 0x744:
                break;

            case RVV_CSR_VLENB:
                csrValue = 32;
                break;

            default:
                throw new RuntimeException("Unknown CSR with id: " + ByteArrayUtil.byteToHex(csrId));

        }
        return csrValue;
    }

    private void printMemoryAroundPC(int displayDistance) {
        logger.info("---------------------------------------------------------------------");

        long s = pc & 0x00000000ffffffffL;

        // do not access negative memory address when printing close to address 0x00 of
        // RAM
        long startAddress = Math.max(0x00L, s - displayDistance * 4);
        int endAddress = pc + displayDistance * 4;

        memory.setDecoder(decoder);
        memory.print(startAddress, endAddress, ByteOrder.LITTLE_ENDIAN, pc);
    }

    /**
     * prints a zero terminated string starting at the address
     * 
     * @param startAddress read a zero terminated string starting at this address
     */
    private void printStringFromAddress(int startAddress) {
        int c = 0xFF;
        do {
            c = memory.getByte(startAddress++);
            System.out.print((char) c);
        } while (c != '\0');
    }

    /**
     * prints a zero terminated string starting at the address
     * 
     * @param startAddress read a zero terminated string starting at this address
     */
    private String toStringFromAddress(int startAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        int c = 'a';
        do {
            c = memory.getByte(startAddress++);
            if ((c != '\0') && (c != 0x0A)) {
                stringBuilder.append((char) c);
            }
        } while ((c != '\0') && (c != 0x0A));
        return stringBuilder.toString();
    }

    private static String getInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    @Override
    public long[] getRegisterFile() {
        long[] result = new long[32];
        for (int i = 0; i < 31; i++) {
            result[i] = (long) registerFile[i];
        }
        return result;
    }

}
