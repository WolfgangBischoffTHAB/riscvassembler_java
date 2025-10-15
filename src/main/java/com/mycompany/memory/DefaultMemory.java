package com.mycompany.memory;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;

public class DefaultMemory implements Memory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMemory.class);

    public Map<Long, MemoryBlock> memoryBlocksByAddress = new HashMap<>();

    @Override
    public void copy(long targetAddress, byte[] srcBuffer, int offsetInSrcBuffer, int sizeInBytes) {
        
        // memory align address to MB
        long addressAligned = targetAddress & 0xFFFFFFFFFFF00000L;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        // address is the location in the destination buffer. 
        // sIt is a local offset from the start of the current buffet
        long addr = targetAddress - addressAligned;

        // if the data does not fit in the buffer, explode like its 1995
        if ((addr + sizeInBytes) > memoryBlock.size) {
            throw new RuntimeException("Data does not fit!");
        }
        
        System.arraycopy(srcBuffer, offsetInSrcBuffer, memoryBlock.memory, (int) addr, sizeInBytes);
    }

    @Override
    public int getByte(int addr) {

        try {

            // memory align address to MB
            long addressAligned = addr & 0xFFFFFFFFFF00000L;

            MemoryBlock memoryBlock = null;
            if (!memoryBlocksByAddress.containsKey(addressAligned)) {
                memoryBlock = new MemoryBlock();
                memoryBlock.address = addressAligned;
                memoryBlocksByAddress.put(addressAligned, memoryBlock);
            } else {
                memoryBlock = memoryBlocksByAddress.get(addressAligned);
            }
            int data = (memoryBlock.memory[(int) (addr - addressAligned)] & 0xFF);

            return data;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void storeByte(int address, byte data) {

        // memory align address to MB
        long addressAligned = address & 0xFFFFFFFFFFF00000L;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        memoryBlock.memory[(int) (address - addressAligned)] = data;
    }

    @Override
    public int readWord(long addr, ByteOrder byteOrder) {

        logger.info(ByteArrayUtil.byteToHex(addr) + "(" + addr + ")");

        // memory align address to MB
        long addressAligned = addr & 0xFFFFFFFFFFF00000L;

        logger.info(ByteArrayUtil.byteToHex(addressAligned));

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        int offsetAddress = (int)(addr - memoryBlock.address);
        logger.trace("offsetAddress: " + ByteArrayUtil.byteToHex(offsetAddress) + "(" + offsetAddress + ")");

        //memoryBlock.print(0x80002000, 0x8000200c, byteOrder);

        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 0]) + " (" + memoryBlock.memory[offsetAddress + 0] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 1]) + " (" + memoryBlock.memory[offsetAddress + 1] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 2]) + " (" + memoryBlock.memory[offsetAddress + 2] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 3]) + " (" + memoryBlock.memory[offsetAddress + 3] + ")");

        final int instruction = ByteArrayUtil.fourByteToInt(
            memoryBlock.memory[offsetAddress + 0], 
            memoryBlock.memory[offsetAddress + 1], 
            memoryBlock.memory[offsetAddress + 2],
            memoryBlock.memory[offsetAddress + 3], byteOrder);

        logger.trace(ByteArrayUtil.byteToHex(instruction));

        return instruction;
    }

    @Override
    public MemoryBlock getMemoryBlockForAddress(int addr) {

        // memory align address to MB
        int addressAligned = addr & 0xFFF00000;

        MemoryBlock memoryBlock = memoryBlocksByAddress.get(addressAligned);

        return memoryBlock;
    }

    public void print(int startAddress, int endAddress, ByteOrder byteOrder, int highlightAddress) {
        MemoryBlock memoryBlock = getMemoryBlockForAddress(startAddress);
        if (memoryBlock != null) {
            memoryBlock.print(startAddress, endAddress, byteOrder, highlightAddress);
        }
    }

}
