package com.mycompany.cpu;

import java.nio.ByteOrder;

import com.mycompany.common.ByteArrayUtil;

public class PipelinedCPUInstructionFetchStage {

    public int step(final int pc, final byte[] memory) {

        if (pc >= memory.length) {
            return 0;
        }

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

        final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc + 1], memory[pc + 2],
                memory[pc + 3], byteOrder);

        System.out.println("HEX: " + ByteArrayUtil.intToHex("%08x", instruction));

        return instruction;
    }

    public void step_write(int pc, byte[] memory) {
        // nop
    }

    public int step_read(int pc, byte[] memory) {
        return step(pc, memory);
    }

}
