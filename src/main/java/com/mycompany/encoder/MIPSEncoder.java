package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class MIPSEncoder implements Encoder {

    private ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    private AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();

    private MnemonicEncoder mnemonicEncoder = new MIPSMnemonicEncoder();

    @Override
    public long encode(AsmLine<?> asmLine, Map<String, Long> labelAddressMap, long currentAddress) throws IOException {

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                int length = mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine, labelAddressMap,
                        currentAddress);
                return length;

            case ASSEMBLER_INSTRUCTION:
                return asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine);

            default:
                return 0;
        }
    }

    @Override
    public ByteArrayOutputStream getByteArrayOutStream() {
        return byteArrayOutStream;
    }

}
