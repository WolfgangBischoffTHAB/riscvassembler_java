package com.mycompany.cpu;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.decoder.DelegatingDecoder;
import com.mycompany.memory.Memory;

public class SingleCycleCPU extends AbstractCPU {

    private static final Logger logger = LoggerFactory.getLogger(SingleCycleCPU.class);

    public int pc;

    // public byte[] memory;
    public Memory memory;

    public DelegatingDecoder delegatingDecoder = new DelegatingDecoder();

    /**
     * ctor
     */
    public SingleCycleCPU() {
        registerFile[0] = 0; // set zero
    }

    /**
     * https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html
     */
    public boolean step() {

        if ((pc == 0xFFFFFFFF) || (pc == 0xCAFEBABE)) {
            logger.info("PC is 0xFFFFFFFF! End of application!");

            return false;
        }

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        // FETCH - use PC to load instruction from memory
        // final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc + 1], memory[pc + 2],
        //         memory[pc + 3], byteOrder);

        final int instruction = memory.readWord(pc, byteOrder);

        if ((instruction == 0x00000000) || (instruction == 0xFFFFFFFF)) {
            // throw new RuntimeException("Done");
            return false;
        }

        // DECODE - use decoder to turn 32 bits into an instruction ASM Line including
        // parameters and opcode
        AsmLine<?> asmLine = delegatingDecoder.decode(instruction);

        // DEBUG output ASM line
        logger.trace("PC: " + pc + " (" + ByteArrayUtil.intToHex("%08x", pc) + ")" + ". Loaded Instr: HEX: "
                + ByteArrayUtil.intToHex("%08x", instruction) + " " + asmLine.toString());
        
        // logger.info(asmLine.toString());

        if (asmLine.mnemonic == null) {
            logger.trace(asmLine.toString());
            throw new RuntimeException("Decoding instruction without mnemonic!");
        }

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        int addr;
        int value;
        int register_0_value;
        int register_1_value;
        byte[] let;
        StringBuilder stringBuilder;

        switch (asmLine.mnemonic) {

            case I_LUI:
                // rd <- imm20 << 12

                // LUI (load upper immediate) is used to build 32-bit constants and uses the U-type format. 
                // LUI places the 32-bit U-immediate value into the destination register rd, filling 
                // in the lowest 12 bits with zeros.
                writeRegisterFile(asmLine.register_0.getIndex(), (asmLine.numeric_1.intValue() << 12L));
                //writeRegisterFile(asmLine.register_0.getIndex(), asmLine.numeric_1.intValue());
                pc += 4;
                break;

            case I_AUIPC:
                // Add upper immediate to PC (and store the result into rd)
                // auipc rd, imm
                // rd <- PC + imm20 << 12; pc += 4;
                logger.trace("auipc");
                // registerFile[asmLine.register_0.getIndex()] = (int) (pc + (asmLine.numeric_1
                // << 12L));
                writeRegisterFile(asmLine.register_0.getIndex(), (int) (pc + (asmLine.numeric_1 << 12L)));
                pc += 4;
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                logger.trace("jal");

                // registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                //System.out.println("instruction: " + ByteArrayUtil.byteToHex(instruction));

                int imm_10_1 = (instruction >> (9 + 12)) & 0b1111111111;
                int imm_11 = (instruction >> (8 + 12)) & 0b1;
                int imm_19_12 = (instruction >> (0 + 12)) & 0b11111111;
                int imm_20 = (instruction >> (19 + 12)) & 0b1;

                int jumpDistance = (imm_20 << 20) + (imm_19_12 << 12) + (imm_11 << 11) + (imm_10_1 << 1);
                //jumpDistance <<= 11;

                //int jumpDistance = (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(asmLine.numeric_1.intValue());
                //System.out.println("jumpDistance: " + ByteArrayUtil.byteToHex(jumpDistance));

                jumpDistance = (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(jumpDistance);

                pc += jumpDistance;

                //System.out.println("newPC: " + ByteArrayUtil.byteToHex(pc));

                break;

            case I_JALR:
                // rd = pc+4; pc = rs1+imm
                logger.trace("jalr");

                // rd = pc+4;
                // registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                logger.trace("register_1 content: " + readRegisterFile(asmLine.register_1.getIndex()));

                // pc = rs1+imm
                int pcReplacement = readRegisterFile(asmLine.register_1.getIndex()) + asmLine.numeric_2.intValue();

                // DEBUG
                logger.trace("Current PC: " + pc);
                logger.trace("New PC: " + pcReplacement);

                pc = pcReplacement;
                break;

            case I_BEQ:
                // Take the branch if registers rs1 and rs2 are equal.
                // if (x[rs1] == x[rs2]) pc += sext(offset)
                logger.trace("beq");
                if (readRegisterFile(asmLine.register_0.getIndex()) == readRegisterFile(
                        asmLine.register_1.getIndex())) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BNE:
                logger.trace("bne");
                if (readRegisterFile(asmLine.register_0.getIndex()) != readRegisterFile(
                        asmLine.register_1.getIndex())) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BLT:
                // if (rs1 < rs2) pc += imm
                // DEBUG
                int blt_a = readRegisterFile(asmLine.register_0.getIndex());
                int blt_b = readRegisterFile(asmLine.register_1.getIndex());
                // Implementation
                if (blt_a < blt_b) {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is taken!");
                    pc += asmLine.numeric_2.intValue();
                } else {
                    logger.trace("blt " + blt_a + " < " + blt_b + ". Branch is NOT taken!");
                    pc += 4;
                }
                break;

            case I_BGE:
                // bge rs1, sr2, imm
                // if (rs1 >= rs2) pc += imm
                register_0_value = readRegisterFile(asmLine.register_0.getIndex());
                register_1_value = readRegisterFile(asmLine.register_1.getIndex());
                // DEBUG
                logger.trace("bge " + register_0_value + " >= " + register_1_value);
                // Implementation
                if (register_0_value >= register_1_value) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BLTU:
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

                long register_0_value_l = readRegisterFile(asmLine.register_0.getIndex()) & 0x00000000ffffffffL;
                long register_1_value_l = readRegisterFile(asmLine.register_1.getIndex()) & 0x00000000ffffffffL;
                if (register_0_value_l < register_1_value_l) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BGEU:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_LB:
                logger.trace(asmLine.toString());
                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                logger.trace("addr: " + addr);
                // read from memory (MEMORY STAGE)
                let = new byte[1];
                let[0] = (byte) memory.getByte(addr + 0);
                // let[1] = memory[addr + 1];
                // let[2] = memory[addr + 2];
                // let[3] = memory[addr + 3];
                // WRITE BACK STAGE
                // place read value into the destination register
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.BIG_ENDIAN);
                // value = ByteArrayUtil.fourByteToInt(let, ByteOrder.LITTLE_ENDIAN);
                writeRegisterFile(asmLine.register_0.getIndex(), (int) let[0]);
                // DEBUG
                stringBuilder = new StringBuilder();
                stringBuilder.append("lb");
                stringBuilder.append(" mem: " + (addr + 0) + " = " + let[0]);
                // stringBuilder.append(", mem: " + (addr + 1) + " = " + let[1]);
                // stringBuilder.append(", mem: " + (addr + 2) + " = " + let[2]);
                // stringBuilder.append(", mem: " + (addr + 3) + " = " + let[3]);
                logger.trace(stringBuilder.toString());
                pc += 4;
                break;

            case I_LH:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_LW:
                logger.trace(asmLine.toString());

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
                pc += 4;
                break;

            case I_LBU:
                try {
                    addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                    // DEBUG
                    logger.trace("addr: " + ByteArrayUtil.byteToHex(addr) + " " + addr);

                    value = memory.getByte(addr);
                    writeRegisterFile(asmLine.register_0.getIndex(), value);
                    pc += 4;
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                break;

            case I_LBW:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SB:
                //throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);

                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // retrieve the value to write into the address
                value = readRegisterFile(asmLine.register_0.getIndex());

                memory.storeByte(addr + 0, (byte) (value & 0xFF));
            
                // Increment PC
                pc += 4;
                break;

            case I_SH:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SW:
                logger.trace("sw : " + asmLine);
                // Store 32-bit, values from the low bits of register rs2 to memory.
                // sw rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][31:0]
                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                // retrieve the value to write into the address
                value = readRegisterFile(asmLine.register_0.getIndex());
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
                pc += 4;
                break;

            case I_ADDI:
                // rd = rs1 + imm
                logger.trace("addi: " + asmLine);
                
                int first_register_value = readRegisterFile(asmLine.register_1.getIndex());
                int immediate_value = asmLine.numeric_2.intValue();
                
                logger.trace("1st Register Name: " + asmLine.register_1.toStringAbi());
                logger.trace("1st Register Value: " + first_register_value);
                logger.trace("2nd Immediate Value: " + immediate_value);
                logger.trace("dest Register Name: " + asmLine.register_0.toStringAbi());
                
                int result = first_register_value + immediate_value;

                // System.out.println("New: " + ByteArrayUtil.byteToHex(result));

                writeRegisterFile(asmLine.register_0.getIndex(), result);

                // increment PC
                pc += 4;
                break;

            case I_SLTI:
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
                immediate_value = asmLine.numeric_2.intValue();
                writeRegisterFile(asmLine.register_0.getIndex(), (value < immediate_value) ? 1 : 0);
                break;

            case I_SLTIU:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_XORI:
                logger.trace("XORI");

                // xori rd,rs1,imm
                // x[rd] = x[rs1] ^ sext(immediate)

                value = readRegisterFile(asmLine.register_1.getIndex())
                        ^ ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += 4;
                break;

            case I_ORI:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_ANDI:
                logger.trace("ANDI");
                // Format: andi rd, rs1, imm
                // Description: Performs bitwise AND on register rs1 and the sign-extended
                // 12-bit
                // immediate and place the result in rd. This is the basic bitwise AND operator
                // but
                // instead of using two registers, it uses a 12-bit immediate which is sign
                // extended.
                // Implementation: x[rd] = x[rs1] & sext(immediate)
                value = readRegisterFile(asmLine.register_1.getIndex())
                        & ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += 4;
                break;

            case I_SLLI:
                // SLLI (Shift Left Logical Immediate)
                // slli rd, rs1, imm # rd = rs1 << imm
                logger.trace("SLLI");

                immediate_value = (int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue());

                value = readRegisterFile(asmLine.register_1.getIndex()) << immediate_value;
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += 4;
                break;

            case I_SRLI:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SRAI:
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
                pc += 4;
                break;

            case I_ADD:
                logger.trace("add");
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // + registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        + readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_SUB:
                // Subtracts the register rs2 from rs1 and stores the result
                // in rd. Arithmetic overflow is ignored and the result is
                // simply the low XLEN bits of the result.
                // sub rd,rs1,rs2
                // x[rd] = x[rs1] - x[rs2]
                logger.trace("sub");
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // - registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        - readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_SLL:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SLT:
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
                pc += 4;
                break;

            case I_SLTU:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_XOR:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SRL:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_SRA:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
            // break;

            case I_OR:
                logger.trace("or");
                // Performs bitwise OR on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // | registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        | readRegisterFile(asmLine.register_2.getIndex()));

                pc += 4;
                break;

            case I_AND:
                logger.trace("and");
                // Performs bitwise AND on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // & registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        & readRegisterFile(asmLine.register_2.getIndex()));

                pc += 4;
                break;

            // case I_FENCE:
            // break;
            // case I_FENCE_I:
            // break;

            case I_ECALL:

            int regA7Value = readRegisterFile(RISCVRegister.REG_A7.getIndex());

            switch (regA7Value) {

                case 0x5D: // 93dec
                    System.out.println("exit()");
                    return false;

                case 0x5E: // 94dec
                    System.out.println("time()");
                    return false;
            
                default:
                    int regA0Value = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                    // System.out.println(ByteArrayUtil.byteToHex(regA0Value));

                    //regA0Value -= 0x10000;
                    //System.out.println(ByteArrayUtil.byteToHex(regA0Value));

                    int c = 'a';
                    do {
                        c = memory.getByte(regA0Value++);
                        if ((c != '\0') && (c != 0x0A)) {
                            System.out.print((char)c);
                        }
                        // System.out.println(ByteArrayUtil.byteToHex(c) + " " + (char)(c));
                    } while ((c != '\0') && (c != 0x0A));
                    System.out.println();

                    //for (int i = 0; i < 10; i++) {
                        // System.out.println(ByteArrayUtil.byteToHex(c) + " " + (char)(c));
                    //}

                    break;
                }
                //throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
                pc += 4;
                break;
            // break;
            // case I_EBREAK:
            // break;
            // case I_CSRRW:
            // break;
            // case I_CSRRS:
            // break;
            // case I_CSRRC:
            // break;
            // case I_CSRRWI:
            // break;
            // case I_CSRRSI:
            // break;
            // case I_CSRRCI:
            // break;

            //
            // multiplication extension
            //

            case I_MUL:
                logger.trace("mul");
                // registerFile[asmLine.register_0.getIndex()] =
                // registerFile[asmLine.register_1.getIndex()]
                // * registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        * readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_NOP:
                // logger.trace("mnemonic: NOP");
                pc += 4;
                break;

            case I_BRK:
                // logger.trace("mnemonic: BRK");
                pc += 4;
                break;

            case I_PUTS:
                logger.info("mnemonic: PUTS");

                // the start address of the zero terminated string is expected in A0
                int startAddress = readRegisterFile(RISCVRegister.REG_A0.getIndex());
                logger.info("startAddress: " + startAddress);

                stringBuilder = new StringBuilder();
                while (true) {
                    int tempByte = memory.getByte(startAddress);
                    if (tempByte == 0x00) {
                        break;
                    } else {
                        stringBuilder.append((char) tempByte);
                    }

                    startAddress++;
                }

                logger.trace(stringBuilder.toString());
                
                pc += 4;
                break;

            //
            // Extension M
            //

            case I_REMU:
                // Unsigned remainder - https://riscv-software-src.github.io/riscv-unified-db/manual/html/isa/isa_20240411/insts/remu.html
                // Calculate the remainder of unsigned division of xs1 by xs2, and store the result in xd.
                logger.trace("mnemonic: REMU");

                int reg1Value = readRegisterFile(asmLine.register_1.getIndex());
                int reg2Value = readRegisterFile(asmLine.register_2.getIndex());

                int remuResult = 0;
                if (reg2Value == 0) {
                    remuResult = 0;
                } else {
                    remuResult = reg1Value % reg2Value;
                }

                writeRegisterFile(asmLine.register_0.getIndex(), remuResult);

                pc += 4;
                break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
        }

        return true;
    }

}
