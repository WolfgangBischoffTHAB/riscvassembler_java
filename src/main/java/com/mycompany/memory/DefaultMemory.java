package com.mycompany.memory;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.common.ByteArrayUtil;

public class DefaultMemory implements Memory {

    public Map<Integer, MemoryBlock> memoryBlocksByAddress = new HashMap<>();

    @Override
    public void copy(int targetAddress, byte[] srcBuffer, int offsetInSrcBuffer, int sizeInBytes) {
        
        // memory align address to MB
        int addressAligned = targetAddress & 0xFFF00000;
        // addressAligned = addressAligned - 1;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        int addr = targetAddress - addressAligned;
        System.arraycopy(srcBuffer, offsetInSrcBuffer, memoryBlock.memory, addr, sizeInBytes);
    }

    @Override
    public int getByte(int addr) {

        // memory align address to MB
        int addressAligned = addr & 0xFFF00000;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        // for (int i = 0; i < 10; i++) {
        //     System.out.println(ByteArrayUtil.byteToHex(memoryBlock.memory[i]));
        // }

        // for (int i = 0; i < 10; i++) {
        //     System.out.println(ByteArrayUtil.byteToHex(memoryBlock.memory[addr + i]) + " " + (char)(memoryBlock.memory[addr + i]));
        // }
        int data = (memoryBlock.memory[addr] & 0xFF);

        // System.out.println(Character.toString((char) data));

        return data;
    }

    @Override
    public void storeByte(int address, byte data) {

        // memory align address to MB
        int addressAligned = address & 0xFFF00000;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        memoryBlock.memory[address - addressAligned] = data;
    }

    @Override
    public int readWord(int addr, ByteOrder byteOrder) {

        // memory align address to MB
        int addressAligned = addr & 0xFFF00000;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        final int instruction = ByteArrayUtil.fourByteToInt(
            memoryBlock.memory[(addr - addressAligned) + 0], 
            memoryBlock.memory[(addr - addressAligned) + 1], 
            memoryBlock.memory[(addr - addressAligned) + 2],
            memoryBlock.memory[(addr - addressAligned) + 3], byteOrder);

        return instruction;
    }

}
