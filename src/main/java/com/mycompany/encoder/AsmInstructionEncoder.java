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

            case STRING:
                encodeStringAssemblerInstruction(byteArrayOutStream, asmLine);
                break;

            case GLOBAL:
            case SECTION:
            case EQU:
                // nop
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }
    }

    private void encodeStringAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine asmLine) {

        EncoderUtils.encodeString(byteArrayOutStream, asmLine.stringValue);

        // // int dataArrayLength = asmLine.stringValue.length() + 1;
        // // char[] dataArray = new char[dataArrayLength];
        // // dataArray[dataArrayLength - 1] = 0;

        // int dataArrayLength = asmLine.stringValue.length();
        // char[] dataArray = new char[dataArrayLength];

        // int index = 3;
        // int oldIndex = index;

        // for (char data : asmLine.stringValue.toCharArray()) {

        //     dataArray[index] = data;
        //     if ((index % 4) == 0) {
        //         index = oldIndex + 4;
        //         oldIndex = index;

        //         index += 1;
        //     }
        //     index--;
        // }

        // for (char data : dataArray) {
        //     byteArrayOutStream.write(data);
        // }
        // //byteArrayOutStream.write(0);
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
