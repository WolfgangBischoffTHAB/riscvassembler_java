package com.mycompany.cpu;

import java.nio.ByteOrder;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class CPU {

    public int pc;

    public byte[] memory;

    public int[] registerFile = new int[32];

    public void step() {

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

        final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc + 1], memory[pc + 2], memory[pc + 3], byteOrder);

        System.out.println("HEX: " + ByteArrayUtil.intToHex("%08x", instruction));

        AsmLine asmLine = Decoder.decode(instruction);

        System.out.println(asmLine);

        // https://projectf.io/posts/riscv-cheat-sheet/

        switch (asmLine.mnemonic) {

            case I_LUI:
                break;

            case I_AUIPC:
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                System.out.println("jal");
                registerFile[asmLine.register_0.ordinal()] = pc + 4;
                pc += (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(asmLine.numeric_1.intValue());
                break;

            case I_JALR:
                // rd = pc+4; pc = rs1+imm
                System.out.println("jalr");
                registerFile[asmLine.register_0.ordinal()] = pc + 4;
                pc = registerFile[asmLine.register_1.ordinal()] + asmLine.numeric_2.intValue();
                break;

            case I_BEQ:
                break;
            case I_BNE:
                break;
            case I_BLT:
                break;
            case I_BGE:
                // if(rs1 >= rs2) pc += imm
                System.out.println("bge");
                if (registerFile[asmLine.register_0.ordinal()] >= registerFile[asmLine.register_1.ordinal()]) {
                    pc = asmLine.numeric_2.intValue();
                } else {
                    pc += 4;
                }
                break;
            case I_BLTU:
                break;
            case I_BGEU:
                break;

            case I_LB:
                break;
            case I_LH:
                break;
            case I_LW:
                break;
            case I_LBU:
                break;
            case I_LBW:
                break;
            
            case I_SB:
                break;
            case I_SH:
                break;
            case I_SW:
                break;
            case I_ADDI:
                // rd = rs1 + imm
                System.out.println("addi");
                registerFile[asmLine.register_0.ordinal()] = registerFile[asmLine.register_1.ordinal()] + asmLine.numeric_2.intValue();
                pc += 4;
                break;

            case I_SLTI:
                break;
            case I_SLTIU:
                break;
            case I_XORI:
                break;
            case I_ORI:
                break;
            case I_ANDI:
                break;
            case I_SLLI:
                break;
            case I_SRLI:
                break;
            case I_SRAI:
                break;
            
            case I_ADD:
                System.out.println("add");
                registerFile[asmLine.register_0.ordinal()] = registerFile[asmLine.register_1.ordinal()] + registerFile[asmLine.register_2.ordinal()];
                pc += 4;
                break;

            case I_SUB:
                break;
            case I_SLL:
                break;
            case I_SLT:
                break;
            case I_SLTU:
                break;
            case I_XOR:
                break;
            case I_SRL:
                break;
            case I_SRA:
                break;

            case I_OR:
                break;
            case I_AND:
                break;
            // case I_FENCE:
            //     break;
            // case I_FENCE_I:
            //     break;

            case I_ECALL:
                break;
            // case I_EBREAK:
            //     break;
            // case I_CSRRW:
            //     break;
            // case I_CSRRS:
            //     break;
            // case I_CSRRC:
            //     break;
            // case I_CSRRWI:
            //     break;
            // case I_CSRRSI:
            //     break;
            // case I_CSRRCI:
            //     break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
        }

        
    }
    
}
