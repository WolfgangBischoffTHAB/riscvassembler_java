package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class RISCVEncoder implements Encoder {

    private ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    private AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();

    private MnemonicEncoder mnemonicEncoder = new RISCVMnemonicEncoder();

    @Override
    public long encode(final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap, final long currentAddress)
            throws IOException {

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                int length = mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine, labelAddressMap,
                        addressSourceAsmLineMap,
                        currentAddress);
                return length;

            case ASSEMBLER_INSTRUCTION:
                return asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine,
                        addressSourceAsmLineMap, currentAddress);

            default:
                return 0;
        }
    }

    @Override
    public ByteArrayOutputStream getByteArrayOutStream() {
        return byteArrayOutStream;
    }
}
