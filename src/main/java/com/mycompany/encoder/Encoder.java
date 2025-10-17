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
     * @param currentAddress
     * @return
     * @throws IOException
     */
    long encode(AsmLine<?> asmLine, Map<String, Long> labelAddressMap, Map<Long, AsmLine<?>> addressSourceAsmLineMap,
            long currentAddress)
            throws IOException;

    // /**
    //  * Returns the internal buffer that stores the machine code that has been
    //  * assembled from all instructions fed to the encoder
    //  *
    //  * @return
    //  */
    // ByteArrayOutputStream getByteArrayOutStream();

}
