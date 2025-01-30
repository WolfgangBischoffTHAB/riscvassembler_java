package com.mycompany.cpu;

public class PipelinedCPUInstructionMemoryStage {

    private int memoryAddress;

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
                byte data = cpu.memory[memoryAddress];
                System.out.println("[MEMOR] LB: " + ex_mem.asmLine + " data = " + data);
                mem_wb.value = data;
                mem_wb.rd_value = data;

                // need to forward into ex_mem pipeline memory becaus a subsequent sub for example
                // will read from ex_mem forwarding Map
                ex_mem.forwardingMap.put(ex_mem.asmLine.register_0, (int) data);
                break;

            case I_SB:
                byte sbData = (byte) (mem_wb.value & 0xFF);
                System.out.println("[MEMOR] SB: " + ex_mem.asmLine + " data = " + sbData);
                cpu.memory[memoryAddress] = sbData;
                break;

            default:
                System.out.println("[MEMOR] N/A");
                break;
        }

        return 0;
    }

}
