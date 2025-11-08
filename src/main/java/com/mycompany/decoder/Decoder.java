package com.mycompany.decoder;

import java.nio.ByteOrder;
import java.util.List;

import com.mycompany.data.AsmLine;

public interface Decoder {

    public static final ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
    // public static final ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    List<AsmLine<?>> decode(long address);

}
