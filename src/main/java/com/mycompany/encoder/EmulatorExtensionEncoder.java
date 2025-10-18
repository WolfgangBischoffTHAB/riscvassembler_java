package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

/**
 * Because it is simple, I added special treatment for calls to special
 * functions such as puts to output a zero terminated string.
 * 
 * The Assembler will convert "call puts" instruction into a special
 * emulator extension instruction which can then be implemented in 
 * emulator code and not within the RISC-V, MIPS or other CPU.
 */
public class EmulatorExtensionEncoder {

    public long encodeEmulatorExtension(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap, final long currentAddress) throws IOException {
        
        switch (asmLine.mnemonic) {

            // case I_PUTS:
            //     EncoderUtils.convertToUint32_t(byteArrayOutStream, 0x11111111);
            //     return 4;

            default:
                throw new RuntimeException("Unknown Assembler Extension!");

        }
    }
    
}
