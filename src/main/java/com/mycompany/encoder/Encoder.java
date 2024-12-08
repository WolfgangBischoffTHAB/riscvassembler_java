package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class Encoder {

    public ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    private AsmInstructionEncoder asmInstructionEncoder = new AsmInstructionEncoder();

    private MnemonicEncoder mnemonicEncoder = new MnemonicEncoder();

    public void encode(final AsmLine asmLine) {

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                mnemonicEncoder.encodeMnemonic(byteArrayOutStream, asmLine);
                break;

            case ASSEMBLER_INSTRUCTION:
                asmInstructionEncoder.encodeAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            default:
                System.out.println("Not encoding: " + asmLine);
        }
    }








}
