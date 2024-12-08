package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class AsmInstructionEncoder {

    public void encodeAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        switch (asmLine.asmInstruction) {

            case WORD:
                encodeWordAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }
    }

    private void encodeWordAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 16);
            }

            EncoderUtils.convertToUint32_t(byteArrayOutStream, (int) data);
        }
    }

}
