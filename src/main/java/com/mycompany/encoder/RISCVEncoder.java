package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;
import com.mycompany.data.Section;

public class RISCVEncoder implements Encoder {

    private EmulatorExtensionEncoder emulatorExtensionEncoder = new EmulatorExtensionEncoder();

    public MnemonicEncoder mnemonicEncoder;

    public AsmInstructionEncoder asmInstructionEncoder;

    public Map<String, Section> sectionMap;

    public Section currentSection;

    /**
     * returns the length in bytes use to encode this entity
     */
    @Override
    public long encode(final AsmLine<?> asmLine, final Map<String, Long> labelAddressMap,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap)
            throws IOException {

        ByteArrayOutputStream byteArrayOutStream = asmLine.section.byteArrayOutStream;

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                // asmInstructionEncoder.endStringMode(byteArrayOutStream);
                return mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine, labelAddressMap,
                        addressSourceAsmLineMap);

            case ASSEMBLER_INSTRUCTION:
                // asmInstructionEncoder.startStringMode();
                return asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine,
                        addressSourceAsmLineMap);

            case EMULATOR_EXTENSION:
                return emulatorExtensionEncoder.encodeEmulatorExtension(byteArrayOutStream, asmLine,
                        addressSourceAsmLineMap);

            default:
                return 0;

        }

    }

    public void finalize(ByteArrayOutputStream byteArrayOutStream) throws IOException {
        // flush potential pending string buffer
        // asmInstructionEncoder.endStringMode(byteArrayOutStream);
    }
}
