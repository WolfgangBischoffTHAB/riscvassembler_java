package com.mycompany.cpu;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;

public class PipelinedCPUInstructionExecuteStage {

    public void step(final PipelinedCPU cpu, final AsmLine asmLine, DE_EX de_ex) {

        // skip
        if (asmLine == null) {
            return;
        }

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        switch (asmLine.mnemonic) {

            case I_LUI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_AUIPC:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;

            case I_JAL:
                // rd = pc+4; pc += imm
                System.out.println("jal");
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.pc + 4;
                cpu.pc += (int) NumberParseUtil.sign_extend_20_bit_to_int32_t(asmLine.numeric_1.intValue());
                break;

            case I_JALR:
                // rd = pc+4; pc = rs1+imm
                System.out.println("jalr");
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.pc + 4;
                cpu.pc = cpu.registerFile[asmLine.register_1.getIndex()] + asmLine.numeric_2.intValue();
                break;

            case I_BEQ:
                // Take the branch if registers rs1 and rs2 are equal.
                // if (x[rs1] == x[rs2]) pc += sext(offset)
                System.out.println("beq");
                if (cpu.registerFile[asmLine.register_0.getIndex()] == cpu.registerFile[asmLine.register_1.getIndex()]) {
                    cpu.pc += asmLine.numeric_2.intValue();
                } else {
                    cpu.pc += 4;
                }
                break;
            case I_BNE:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;
            case I_BLT:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;
            case I_BGE:
                // if(rs1 >= rs2) pc += imm
                System.out.println("bge");
                if (cpu.registerFile[asmLine.register_0.getIndex()] >= cpu.registerFile[asmLine.register_1.getIndex()]) {
                    cpu.pc += asmLine.numeric_2.intValue();
                } else {
                    cpu.pc += 4;
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
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                throw new RuntimeException("Not implemented yet!");
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
                // sw rs2,offset(rs1)
                // M[x[rs1] + sext(offset)] = x[rs2][31:0]
                throw new RuntimeException("Not implemented yet!");
            // pc += 4;
            // break;
            case I_ADDI:
                // rd = rs1 + imm
                System.out.println("addi: " + asmLine);
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.registerFile[asmLine.register_1.getIndex()]
                        + asmLine.numeric_2.intValue();
                cpu.pc += 4;
                break;

            case I_SLTI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;
            case I_SLTIU:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
                break;
            case I_XORI:
                System.out.println("Unknown mnemonic! " + asmLine.mnemonic);
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
                // DEBUG
                System.out.println("add");

                // compute instruction (ALU add operation)
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.registerFile[asmLine.register_1.getIndex()]
                        + cpu.registerFile[asmLine.register_2.getIndex()];

                // pipeline stores result in DE_EX memory for forwarding to prevent pipeline stalls
                de_ex.rd_value = cpu.registerFile[asmLine.register_0.getIndex()];

                // cpu.pc += 4;
                break;

            case I_SUB:
                // Subtracts the register rs2 from rs1 and stores the result
                // in rd. Arithmetic overflow is ignored and the result is
                // simply the low XLEN bits of the result.
                // sub rd,rs1,rs2
                // x[rd] = x[rs1] - x[rs2]
                System.out.println("sub");

                if (de_ex.forwarded) {

                    de_ex.forwarded = false;

                    cpu.registerFile[asmLine.register_0.getIndex()] = de_ex.rd_value
                        - cpu.registerFile[asmLine.register_2.getIndex()];
                }

                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.registerFile[asmLine.register_1.getIndex()]
                        - cpu.registerFile[asmLine.register_2.getIndex()];

                //cpu.pc += 4;
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
                if (cpu.registerFile[asmLine.register_1.getIndex()] < cpu.registerFile[asmLine.register_2.getIndex()]) {
                    cpu.registerFile[asmLine.register_0.getIndex()] = 1;
                } else {
                    cpu.registerFile[asmLine.register_0.getIndex()] = 0;
                }
                cpu.pc += 4;
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
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.registerFile[asmLine.register_1.getIndex()]
                        | cpu.registerFile[asmLine.register_2.getIndex()];
                        cpu.pc += 4;
                break;
            case I_AND:
                System.out.println("and");
                // Performs bitwise AND on registers rs1 and rs2 and place the result in rd
                cpu.registerFile[asmLine.register_0.getIndex()] = cpu.registerFile[asmLine.register_1.getIndex()]
                        & cpu.registerFile[asmLine.register_2.getIndex()];
                cpu.pc += 4;
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

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
        }
    }

}
