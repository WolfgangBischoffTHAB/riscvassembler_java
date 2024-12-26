package com.mycompany.cpu;

import java.nio.ByteOrder;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.decoder.Decoder;

public class CPU {

    public int pc;

    public byte[] memory;

    public void step() {

        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

        final int instruction = ByteArrayUtil.fourByteToInt(memory[pc + 0], memory[pc + 1], memory[pc + 2], memory[pc + 3], byteOrder);

        System.out.println("HEX: " + ByteArrayUtil.intToHex("%08x", instruction));

        AsmLine asmLine = Decoder.decode(instruction);

        System.out.println(asmLine);

        pc += 4;
    }
    
}
