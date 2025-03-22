package com.mycompany.cpu;

import java.nio.ByteOrder;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class SingleCycleCPU implements CPU {

    public int pc;

    public byte[] memory;

    public int[] registerFile = new int[32];

    /**
     * ctor
     */
    public SingleCycleCPU() {
        registerFile[0] = 0; // set zero
    }

    public void step() {

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

        final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc + 1], memory[pc + 2],
                memory[pc + 3], byteOrder);

        System.out.println("\nLoaded Instr: HEX: " + ByteArrayUtil.intToHex("%08x", instruction));

        AsmLine asmLine = Decoder.decode(instruction);

        System.out.println(asmLine);

        if (asmLine.mnemonic == null) {
            System.out.println(asmLine);
            throw new RuntimeException("Decoding instruction without mnemonic!");
        }

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        int addr;
        int value;
        byte[] let;

        switch (asmLine.mnemonic) {

            case I_LUI:
                //System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                writeRegisterFile(asmLine.register_0.getIndex(), asmLine.numeric_1.intValue() << 12L);
                pc += 4;
                break;

            case I_AUIPC:
                // Add upper immediate to PC (and store the result into rd)
                // auipc rd, imm
                // rd <- PC + imm20 << 12; pc += 4;
                System.out.println("auipc");
                //registerFile[asmLine.register_0.getIndex()] = (int) (pc + (asmLine.numeric_1 << 12L));
                writeRegisterFile(asmLine.register_0.getIndex(), (int) (pc + (asmLine.numeric_1 << 12L)));
                pc += 4;
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                System.out.println("jal");
                //registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);
                pc += (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(asmLine.numeric_1.intValue());
                break;

            case I_JALR:
                // rd = pc+4; pc = rs1+imm
                System.out.println("jalr");
                //registerFile[asmLine.register_0.getIndex()] = pc + 4;
                writeRegisterFile(asmLine.register_0.getIndex(), pc + 4);

                int pcReplacement = readRegisterFile(asmLine.register_1.getIndex()) + asmLine.numeric_2.intValue();

                System.out.println("Current PC: " + pc);
                System.out.println("New PC: " + pcReplacement);

                pc = pcReplacement;
                break;

            case I_BEQ:
                // Take the branch if registers rs1 and rs2 are equal.
                // if (x[rs1] == x[rs2]) pc += sext(offset)
                System.out.println("beq");
                if (readRegisterFile(asmLine.register_0.getIndex()) == readRegisterFile(asmLine.register_1.getIndex())) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BNE:
                System.out.println("bne");
                if (readRegisterFile(asmLine.register_0.getIndex()) != readRegisterFile(asmLine.register_1.getIndex())) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BLT:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_BGE:
                // if (rs1 >= rs2) pc += imm
                System.out.println("bge " + readRegisterFile(asmLine.register_0.getIndex()) + " >= " + readRegisterFile(asmLine.register_1.getIndex()));
                if (readRegisterFile(asmLine.register_0.getIndex()) >= readRegisterFile(asmLine.register_1.getIndex())) {
                    pc += asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;

            case I_BLTU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_BGEU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_LB:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_LH:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_LW:
                System.out.println(asmLine);

                // compute memory address to load from (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));
                System.out.println("addr: " + addr);

                // read from memory (MEMORY STAGE)

                //let = ByteArrayUtil.intToFourByte(value, ByteOrder.BIG_ENDIAN);

                let = new byte[4];

                let[0] = memory[addr + 0];
                System.out.println("mem: " + (let[0]) + " = " + memory[addr + 0]);
                let[1] = memory[addr + 1];
                System.out.println("mem: " + (let[1]) + " = " + memory[addr + 1]);
                let[2] = memory[addr + 2];
                System.out.println("mem: " + (let[2]) + " = " + memory[addr + 2]);
                let[3] = memory[addr + 3];
                System.out.println("mem: " + (let[3]) + " = " + memory[addr + 3]);

                // WRITE BACK STAGE

                value = ByteArrayUtil.fourByteToInt(let, ByteOrder.BIG_ENDIAN);

                //registerFile[asmLine.register_0.getIndex()] = value;
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += 4;
                break;

            // pc += 4;
            // break;

            case I_LBU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_LBW:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SB:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SH:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SW:
                System.out.println("sw : " + asmLine);
                // Store 32-bit, values from the low bits of register rs2 to memory.
                // sw rs2, offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][31:0]
                //throw new RuntimeException("Not implemented yet!");

                // compute memory address to store to (EXECUTE STAGE)
                addr = (int) (asmLine.offset_1 + readRegisterFile(asmLine.register_1.getIndex()));

                // write into memory (MEMORY STAGE)
                value = readRegisterFile(asmLine.register_0.getIndex());
                let = ByteArrayUtil.intToFourByte(value, ByteOrder.BIG_ENDIAN);
                memory[addr + 0] = let[0];
                System.out.println("mem: " + (addr + 0) + " = " + let[0]);
                memory[addr + 1] = let[1];
                System.out.println("mem: " + (addr + 1) + " = " + let[1]);
                memory[addr + 2] = let[2];
                System.out.println("mem: " + (addr + 2) + " = " + let[2]);
                memory[addr + 3] = let[3];
                System.out.println("mem: " + (addr + 3) + " = " + let[3]);

                pc += 4;
                break;

            case I_ADDI:
                // rd = rs1 + imm
                System.out.println("addi: " + asmLine);
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
                //         + asmLine.numeric_2.intValue();
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                    + asmLine.numeric_2.intValue());
                pc += 4;
                break;

            case I_SLTI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SLTIU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_XORI:
                // System.out.println("Unknown mnemonic! " + asmLine.mnemonic);

                // xori rd,rs1,imm
                // x[rd] = x[rs1] ^ sext(immediate)

                value = readRegisterFile(asmLine.register_1.getIndex()) ^ ((int) NumberParseUtil.sign_extend_12_bit_to_int32_t(asmLine.numeric_2.intValue()));
                writeRegisterFile(asmLine.register_0.getIndex(), value);

                pc += 4;
                break;

            case I_ORI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_ANDI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SLLI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SRLI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SRAI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_ADD:
                System.out.println("add");
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
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
                System.out.println("sub");
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
                //         - registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        - readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_SLL:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SLT:
                // Place the value 1 in register rd if register rs1 is less
                // than register rs2 when both are treated as signed numbers,
                // else 0 is written to rd.
                // slt rd, rs1, rs2
                // x[rd] = x[rs1] <s x[rs2]
                if (readRegisterFile(asmLine.register_1.getIndex()) < readRegisterFile(asmLine.register_2.getIndex())) {
                    // writeRegisterFile[asmLine.register_0.getIndex()] = 1;
                    writeRegisterFile(asmLine.register_0.getIndex(), 1);
                } else {
                    //registerFile[asmLine.register_0.getIndex()] = 0;
                    writeRegisterFile(asmLine.register_0.getIndex(), 0);
                }
                pc += 4;
                break;

            case I_SLTU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_XOR:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SRL:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_SRA:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_OR:
                System.out.println("or");
                // Performs bitwise OR on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
                //         | registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        | readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_AND:
                System.out.println("and");
                // Performs bitwise AND on registers rs1 and rs2 and place the result in rd
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
                //         & registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                        & readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            // case I_FENCE:
            // break;
            // case I_FENCE_I:
            // break;

            case I_ECALL:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;
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
                System.out.println("mul");
                // registerFile[asmLine.register_0.getIndex()] = registerFile[asmLine.register_1.getIndex()]
                //         * registerFile[asmLine.register_2.getIndex()];
                writeRegisterFile(asmLine.register_0.getIndex(), readRegisterFile(asmLine.register_1.getIndex())
                    * readRegisterFile(asmLine.register_2.getIndex()));
                pc += 4;
                break;

            case I_NOP:
                System.out.println("mnemonic: NOP");
                break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
        }

    }

        private int readRegisterFile(int index) {

            // register zero is hardcoded zero
            if (index == 0) {
                return 0;
            }

            // set the value
            return registerFile[index];
        }

        private void writeRegisterFile(int index, int value) {

        // write to zero register has no effect
        if (index == 0) {
            return;
        }

        // set the value
        registerFile[index] = value;
    }

}
