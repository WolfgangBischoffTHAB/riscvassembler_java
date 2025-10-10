package com.mycompany.memory;

import java.nio.ByteOrder;

public interface Memory {

    /**
     * Will copy sizeInBytes from srcBuffer starting at offsetInSrcBuffer into 
     * this memory object into the address targetAddress.
     * 
     * @param targetAddress address to copy data to
     * @param srcBuffer data is read from here. This is src
     * @param offsetInSrcBuffer offset is applied to src during copy
     * @param sizeInBytes read this many bytes from src
     */
    void copy(int targetAddress, byte[] srcBuffer, int offsetInSrcBuffer, int sizeInBytes);

    int getByte(int i);

    void storeByte(int i, byte b);

    int readWord(int pc, ByteOrder byteOrder);

    MemoryBlock getMemoryBlockForAddress(int i);

    void print(int startAddress, int endAddress, ByteOrder byteOrder, int highlightAddress);

}
