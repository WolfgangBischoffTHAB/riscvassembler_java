package com.mycompany.decoder;

import java.util.List;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.data.AsmLine;
import com.mycompany.memory.Memory;
import com.mycompany.memory.MemoryBlock;

public class RawPrintingDecoder implements Decoder {

    public Memory memory;

    @Override
    public List<AsmLine<?>> decode(long address) {
        // MemoryBlock memoryBlock = memory.getMemoryBlockForAddress(address);

        try {
            int data = memory.readWord(address, Decoder.byteOrder);
            System.out.println(ByteArrayUtil.byteToHex(address) + ": " + ByteArrayUtil.byteToHex(data) + " (" + data + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
