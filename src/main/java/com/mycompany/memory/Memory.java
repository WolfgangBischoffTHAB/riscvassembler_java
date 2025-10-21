package com.mycompany.memory;

import java.nio.ByteOrder;

import com.mycompany.decoder.Decoder;

public interface Memory<T> {

    /**
     * Will copy sizeInBytes from srcBuffer starting at offsetInSrcBuffer into 
     * this memory object into the address targetAddress.
     * 
     * @param targetAddress address to copy data to within the internal memory
     * @param srcBuffer data is read from here. This is src
     * @param offsetInSrcBuffer offset is applied to src during copy
     * @param sizeInBytes read this many bytes from src
     */
    void copy(T targetAddress, byte[] srcBuffer, T offsetInSrcBuffer, T sizeInBytes);

    int getByte(T addr);
    int readByte(T addr);

    void storeByte(T addr, byte data);

    int readShort(T addr, ByteOrder byteOrder);
    
    int readWord(T addr, ByteOrder byteOrder);
    
    long readLong(T addr, ByteOrder byteOrder);

    MemoryBlock getMemoryBlockForAddress(T addr);

    void print(T startAddress, long endAddress, ByteOrder byteOrder, long highlightAddress);

    void setDecoder(Decoder decoder);

}
