package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public interface Encoder {

    /**
     * Encodes an assembler instruction into an internal buffer
     *
     * @param asmLine
     * @param labelAddressMap
     * @param addressSourceAsmLineMap
     * @return
     * @throws IOException
     */
    long encode(AsmLine<?> asmLine, Map<String, Long> labelAddressMap,
            Map<Long, AsmLine<?>> addressSourceAsmLineMap)
            throws IOException;

    /**
     *
     * @param byteArrayOutStream
     * @throws IOException
     */
    void finalize(ByteArrayOutputStream byteArrayOutStream) throws IOException;

}
