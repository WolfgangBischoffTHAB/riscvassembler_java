package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class AsmInstructionEncoder {

    public int encodeAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        switch (asmLine.asmInstruction) {

            case BYTE:
                return encodeByteAssemblerInstruction(byteArrayOutStream, asmLine);

            case WORD:
                return encodeWordAssemblerInstruction(byteArrayOutStream, asmLine);

            case ASCII:
                return encodeAsciiAssemblerInstruction(byteArrayOutStream, asmLine);

            case ASCIZ:
            case STRING:
                return encodeStringAssemblerInstruction(byteArrayOutStream, asmLine);

            case FILE:
            case TEXT:
            case DATA:
            case GLOBAL:
            case SECTION:
            case EQU:
                // nop
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }

        return 0;
    }

    private int encodeStringAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, true);
    }

    private int encodeAsciiAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, false);
    }

    private int encodeByteAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        int length = 0;
        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 10);
            }

            EncoderUtils.convertToUint8_t(byteArrayOutStream, (int) data);
            length++;
        }

        return length;
    }

    private int encodeWordAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        int length = 0;
        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 10);
            }

            EncoderUtils.convertToUint32_t(byteArrayOutStream, (int) data);
            length += 4;
        }

        return length;
    }

}
