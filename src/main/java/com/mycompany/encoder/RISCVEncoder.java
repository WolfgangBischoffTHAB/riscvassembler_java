package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class RISCVEncoder implements Encoder {

    private AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();

    private EmulatorExtensionEncoder emulatorExtensionEncoder = new EmulatorExtensionEncoder();

    private MnemonicEncoder mnemonicEncoder = new RISCVMnemonicEncoder();

    /**
     * returns the length in bytes use to encode this entity
     */
    @Override
    public long encode(final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap, final long currentAddress)
            throws IOException {

        ByteArrayOutputStream byteArrayOutStream = asmLine.section.byteArrayOutStream;

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                return mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine, labelAddressMap,
                        addressSourceAsmLineMap,
                        currentAddress);

            case ASSEMBLER_INSTRUCTION:
                return asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine,
                        addressSourceAsmLineMap, currentAddress);

            case EMULATOR_EXTENSION:
                return emulatorExtensionEncoder.encodeEmulatorExtension(byteArrayOutStream, asmLine,
                        addressSourceAsmLineMap, currentAddress);

            default:
                return 0;
        }
    }

    // @Override
    // public ByteArrayOutputStream getByteArrayOutStream() {
    //     return byteArrayOutStream;
    // }
}
