package com.mycompany.cpu;

import java.nio.ByteOrder;

import com.mycompany.common.ByteArrayUtil;
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

            case I_ADD:
                System.out.println("add");
                registerFile[asmLine.register_0.ordinal()] = registerFile[asmLine.register_1.ordinal()] + registerFile[asmLine.register_2.ordinal()];
                pc += 4;
                break;

            case I_JALR:
                System.out.println("jalr");
                registerFile[asmLine.register_0.ordinal()] = pc + 4;
                pc = registerFile[asmLine.register_1.ordinal()] + asmLine.numeric_2.intValue();
                break;

            default:
                throw new RuntimeException("Unknown mnemonic! " + asmLine.mnemonic);
        }

        
    }
    
}
