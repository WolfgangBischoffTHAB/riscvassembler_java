package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class Encoder {

    public ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    private AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();

    private MnemonicEncoder mnemonicEncoder = new MnemonicEncoder();

    public long encode(final AsmLine asmLine, final Map<String, Long> labelAddressMap, final long currentAddress) {

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                int length = mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine, labelAddressMap, currentAddress);
                //System.out.println(asmLine + " -> " + length);
                return length;

            case ASSEMBLER_INSTRUCTION:
                return asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine);

            default:
                //System.out.println("Not encoding: " + asmLine);
                return 0;
        }
    }








}
