package com.mycompany.memory;

import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.ByteArrayUtil;
import com.mycompany.decoder.Decoder;

public class DefaultMemory implements Memory<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMemory.class);

    public Map<Long, MemoryBlock> memoryBlocksByAddress = new HashMap<>();

    private Decoder decoder;

    @Override
    public void copy(Integer targetAddress, byte[] srcBuffer, Integer offsetInSrcBuffer, Integer sizeInBytes) {
        
        // memory align address to MB
        long addressAligned = targetAddress & 0xFFFFFFFFFFF00000L;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlock.decoder = decoder;
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        // address is the location in the destination buffer. 
        // It is a local offset from the start of the current buffet
        long addr = targetAddress - addressAligned;

        // if the data does not fit in the buffer, explode like its 1995
        if ((addr + sizeInBytes) > memoryBlock.size) {
            throw new RuntimeException("Data does not fit!");
        }
        
        System.arraycopy(srcBuffer, offsetInSrcBuffer, memoryBlock.memory, (int) addr, sizeInBytes);
    }

    @Override
    public int getByte(Integer addr) {

        try {

            // memory align address to MB
            long addressAligned = addr & 0xFFFF_FFFF_FFF0_0000L;

            MemoryBlock memoryBlock = null;
            if (!memoryBlocksByAddress.containsKey(addressAligned)) {
                memoryBlock = new MemoryBlock();
                memoryBlock.decoder = decoder;
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
    public int readByte(Integer addr) {
        return getByte(addr);
    }

    @Override
    public void storeByte(Integer addr, byte data) {

        // memory align address to MB
        long addressAligned = addr & 0xFFFFFFFFFFF00000L;

        MemoryBlock memoryBlock = null;
        if (!memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = new MemoryBlock();
            memoryBlock.decoder = decoder;
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        } else {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        }

        memoryBlock.memory[(int) (addr - addressAligned)] = data;
    }

    @Override
    public int readShort(Integer addr, ByteOrder byteOrder) {

        logger.trace(ByteArrayUtil.byteToHex(addr) + "(" + addr + ")");

        MemoryBlock memoryBlock = retrieveMemoryBlockByAddress(addr);

        int offsetAddress = (int)(addr - memoryBlock.address);
        logger.trace("offsetAddress: " + ByteArrayUtil.byteToHex(offsetAddress) + "(" + offsetAddress + ")");

        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 0]) + " (" + memoryBlock.memory[offsetAddress + 0] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 1]) + " (" + memoryBlock.memory[offsetAddress + 1] + ")");

        final int data = ByteArrayUtil.twoByteToInt(
            memoryBlock.memory[offsetAddress + 0], 
            memoryBlock.memory[offsetAddress + 1], 
            byteOrder);

        logger.trace(ByteArrayUtil.byteToHex(data));

        return data;
    }

    @Override
    public int readWord(Integer addr, ByteOrder byteOrder) {

        logger.trace(ByteArrayUtil.byteToHex(addr) + "(" + addr + ")");

        MemoryBlock memoryBlock = retrieveMemoryBlockByAddress(addr);

        int offsetAddress = (int)(addr - memoryBlock.address);
        logger.trace("offsetAddress: " + ByteArrayUtil.byteToHex(offsetAddress) + "(" + offsetAddress + ")");

        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 0]) + " (" + memoryBlock.memory[offsetAddress + 0] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 1]) + " (" + memoryBlock.memory[offsetAddress + 1] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 2]) + " (" + memoryBlock.memory[offsetAddress + 2] + ")");
        logger.trace(ByteArrayUtil.byteToHex(memoryBlock.memory[offsetAddress + 3]) + " (" + memoryBlock.memory[offsetAddress + 3] + ")");

        final int data = ByteArrayUtil.fourByteToInt(
            memoryBlock.memory[offsetAddress + 0], 
            memoryBlock.memory[offsetAddress + 1], 
            memoryBlock.memory[offsetAddress + 2],
            memoryBlock.memory[offsetAddress + 3], byteOrder);

        logger.trace(ByteArrayUtil.byteToHex(data));

        return data;
    }

    @Override
    public long readLong(Integer addr, ByteOrder byteOrder) {
        MemoryBlock memoryBlock = retrieveMemoryBlockByAddress(addr);
        int offsetAddress = (int)(addr - memoryBlock.address);
        final long data = ByteArrayUtil.eightByteToLong(
            memoryBlock.memory[offsetAddress + 0], 
            memoryBlock.memory[offsetAddress + 1], 
            memoryBlock.memory[offsetAddress + 2],
            memoryBlock.memory[offsetAddress + 3], 
            memoryBlock.memory[offsetAddress + 4], 
            memoryBlock.memory[offsetAddress + 5], 
            memoryBlock.memory[offsetAddress + 6],
            memoryBlock.memory[offsetAddress + 7],            
            byteOrder);

        logger.trace(ByteArrayUtil.byteToHex(data));

        return data;
    }

    private MemoryBlock retrieveMemoryBlockByAddress(Integer addr) {
        // memory align address to MB
        long addressAligned = addr & 0xFFFFFFFFFFF00000L;

        logger.trace(ByteArrayUtil.byteToHex(addressAligned));

        MemoryBlock memoryBlock = null;
        if (memoryBlocksByAddress.containsKey(addressAligned)) {
            memoryBlock = memoryBlocksByAddress.get(addressAligned);
        } else {
            memoryBlock = new MemoryBlock();
            memoryBlock.decoder = decoder;
            memoryBlock.address = addressAligned;
            memoryBlocksByAddress.put(addressAligned, memoryBlock);
        }
        return memoryBlock;
    }

    @Override
    public MemoryBlock getMemoryBlockForAddress(Integer addr) {
        // memory align address to MB
        long addressAligned = addr & 0xFFF00000;
        MemoryBlock memoryBlock = memoryBlocksByAddress.get(addressAligned);

        return memoryBlock;
    }

    public void print(Integer startAddress, long endAddress, ByteOrder byteOrder, long highlightAddress) {

        // logger.info(ByteArrayUtil.byteToHex(startAddress));
        // logger.info(ByteArrayUtil.byteToHex(endAddress));

        long s = startAddress & 0x00000000ffffffffL;
        long e = endAddress & 0x00000000ffffffffL;
        
        // logger.info(ByteArrayUtil.byteToHex(s));
        // logger.info(ByteArrayUtil.byteToHex(e));

        if (e <= s) {
            throw new RuntimeException("Invalid interval!");
        }

        MemoryBlock memoryBlock = null;

        // try to retrieve a memory block between startAddress and endAddress.
        // This is necessary because an interval of addresses might start before
        // the first memory block that was actually transferred to memory.
        // This mostly happens when one of the first addresses of the application
        // are printed
        long addr = s;
        while ((memoryBlock == null) && (addr <= e)) {
            memoryBlock = getMemoryBlockForAddress((int) addr);
            if (memoryBlock != null) {
                break;
            }
            addr += 4;
        }
        if (memoryBlock != null) {
            memoryBlock.decoder = decoder;
            memoryBlock.print((int) addr, (int) endAddress, byteOrder, highlightAddress);
        }
    }

    @Override
    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    //
    // 64 bit
    //

    @Override
    public void storeLong(Integer addr, long data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeLong'");
    }

    @Override
    public void readLong(byte[] rvvReg, int rdOffset, long addr, ByteOrder byteOrder) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readLong'");
    }    

}
