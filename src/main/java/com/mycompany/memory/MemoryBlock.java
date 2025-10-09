package com.mycompany.memory;

import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;

public class MemoryBlock {

    private static final Logger logger = LoggerFactory.getLogger(MemoryBlock.class);

    public Integer address;

    public byte[] memory = new byte[1024*1024];

    public void print(int startAddress, int endAddress, ByteOrder byteOrder) {

        logger.info("From " + ByteArrayUtil.byteToHex(startAddress) + " to " + ByteArrayUtil.byteToHex(endAddress));
        
        int tempAddress = startAddress;

        for (int i = 0; i < memory.length / 4; i++) {

            final int instruction = ByteArrayUtil.fourByteToInt(
                memory[(tempAddress - address) + 0], 
                memory[(tempAddress - address) + 1], 
                memory[(tempAddress - address) + 2],
                memory[(tempAddress - address) + 3], byteOrder);

            logger.info(ByteArrayUtil.byteToHex(tempAddress) + ": " + ByteArrayUtil.byteToHex(instruction));

            if (tempAddress >= endAddress) {
                break;
            }

            tempAddress += 4;
        }
    }

}
