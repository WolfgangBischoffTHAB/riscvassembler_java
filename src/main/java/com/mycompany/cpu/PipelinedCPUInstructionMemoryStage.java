package com.mycompany.cpu;

public class PipelinedCPUInstructionMemoryStage {

    private int memoryAddress;
    // private int value;


    public int step_read(final PipelinedCPU cpu, final EX_MEM ex_mem, final MEM_WB mem_wb) {
        // skip
        if (ex_mem.asmLine == null) {
            return 0;
        }

        // store the memory address in the pipeline storage before the next instruction
        // erases it

        switch (ex_mem.asmLine.mnemonic) {

            case I_OR:
            case I_XOR:
            case I_AND:
            case I_SUB:
            case I_ADD:
                mem_wb.rd_value = ex_mem.rd_value;
                break;

            case I_LB:
                memoryAddress = ex_mem.memoryAddress;
                break;

            case I_SB:
                // value = mem_wb.value;
                memoryAddress = ex_mem.memoryAddress;
                break;

            default:
                // throw new RuntimeException("Unknown mnemonic! " + ex_mem.asmLine.mnemonic);
                break;
        }

        return 0;
    }



    public int step_write(final PipelinedCPU cpu, final EX_MEM ex_mem, final MEM_WB mem_wb) {

        // skip
        if (ex_mem.asmLine == null) {
            return 0;
        }

        // https://projectf.io/posts/riscv-cheat-sheet/
        // https://msyksphinz-self.github.io/riscv-isadoc/html/rvi.html

        switch (ex_mem.asmLine.mnemonic) {

            case I_LB:

                System.out.println("LB: " + ex_mem.asmLine);

                // int registerValue = 0;
                // if (ex_mem.forwardingMap.containsKey(ex_mem.asmLine.register_1)) {
                // registerValue = ex_mem.forwardingMap.get(ex_mem.asmLine.register_1);
                // } else if (mem_wb.forwardingMap.containsKey(ex_mem.asmLine.register_1)) {
                // registerValue = mem_wb.forwardingMap.get(ex_mem.asmLine.register_1);
                // } else {
                // registerValue = cpu.registerFile[ex_mem.asmLine.register_1.getIndex()];
                // }

                // int offset = (int) ex_mem.asmLine.offset;

                // int memAddress = (int) (ex_mem.memoryAddress + offset);
                // byte data = cpu.memory[ex_mem.memoryAddress];
                byte data = cpu.memory[memoryAddress];

                System.out.println("result: " + data);

                mem_wb.value = data;
                mem_wb.rd_value = data;
                break;

            case I_SB:
                System.out.println("SB: " + ex_mem.asmLine);

                // mem_wb.value = cpu.memory[ex_mem.memoryAddress];
                // cpu.memory[ex_mem.memoryAddress] = (byte) (mem_wb.value & 0xFF);
                cpu.memory[memoryAddress] = (byte) (mem_wb.value & 0xFF);
                break;

            default:
                // throw new RuntimeException("Unknown mnemonic! " + ex_mem.asmLine.mnemonic);
                break;
        }

        return 0;

    }



}
