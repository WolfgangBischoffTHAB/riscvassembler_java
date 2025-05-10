package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public interface Encoder {

    long encode(final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap, final long currentAddress)
            throws IOException;

    ByteArrayOutputStream getByteArrayOutStream();

}
