package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class MIPSEncoder implements Encoder {

    @Override
    public long encode(AsmLine<?> asmLine, Map<String, Long> labelAddressMap, long currentAddress) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'encode'");
    }

    @Override
    public ByteArrayOutputStream getByteArrayOutStream() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByteArrayOutStream'");
    }

}
