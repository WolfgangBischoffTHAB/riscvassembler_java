package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public interface MnemonicEncoder {

    int encodeMnemonic(ByteArrayOutputStream byteArrayOutStream,
            AsmLine<?> asmLine, Map<String, Long> labelAddressMap, Map<Long, AsmLine<?>> addressSourceAsmLineMap)
            throws IOException;

}
