package com.mycompany.memory;

import java.nio.ByteOrder;

import com.mycompany.decoder.Decoder;

public interface Memory {

    /**
     * Will copy sizeInBytes from srcBuffer starting at offsetInSrcBuffer into 
     * this memory object into the address targetAddress.
     * 
     * @param targetAddress address to copy data to within the internal memory
     * @param srcBuffer data is read from here. This is src
     * @param offsetInSrcBuffer offset is applied to src during copy
     * @param sizeInBytes read this many bytes from src
     */
    void copy(long targetAddress, byte[] srcBuffer, int offsetInSrcBuffer, int sizeInBytes);

    int getByte(int addr);
    int readByte(int addr);

    void storeByte(int addr, byte data);

    int readShort(int addr, ByteOrder byteOrder);
    
    int readWord(long addr, ByteOrder byteOrder);
    
    long readLong(long addr, ByteOrder byteOrder);

    MemoryBlock getMemoryBlockForAddress(int addr);

    void print(long startAddress, long endAddress, ByteOrder byteOrder, int highlightAddress);

    void setDecoder(Decoder decoder);

}
