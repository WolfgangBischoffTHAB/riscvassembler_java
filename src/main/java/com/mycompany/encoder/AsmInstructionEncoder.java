package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class AsmInstructionEncoder {

    public void encodeAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        switch (asmLine.asmInstruction) {

            case BYTE:
                encodeByteAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            case WORD:
                encodeWordAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            case ASCII:
                encodeAsciiAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            case ASCIZ:
            case STRING:
                encodeStringAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

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
    }

    private void encodeStringAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine asmLine) {
        EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, true);
    }

    private void encodeAsciiAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine asmLine) {
        EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, false);
    }

    private void encodeByteAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 10);
            }

            EncoderUtils.convertToUint8_t(byteArrayOutStream, (int) data);
        }
    }

    private void encodeWordAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 10);
            }

            EncoderUtils.convertToUint32_t(byteArrayOutStream, (int) data);
        }
    }

}
