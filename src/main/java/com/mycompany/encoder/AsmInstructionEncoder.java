package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmLine;

/**
 * Encode instructions directed at the assembler: .BYTE, .WORD, ...
 */
public class AsmInstructionEncoder {

    public int encodeAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream, final AsmLine<?> asmLine,
            final Map<Long, AsmLine<?>> addressSourceAsmLineMap, final long currentAddress)
            throws IOException {

        // System.out.println(currentAddress + " -> " + asmLine);
        // addressSourceAsmLineMap.put(currentAddress, asmLine);

        if (asmLine.pseudoInstructionAsmLine != null) {
            System.out.println(currentAddress + " -> " + asmLine.pseudoInstructionAsmLine);
            addressSourceAsmLineMap.put(currentAddress, asmLine.pseudoInstructionAsmLine);
        } else {
            System.out.println(currentAddress + " -> " + asmLine);
            addressSourceAsmLineMap.put(currentAddress, asmLine);
        }

        switch (asmLine.asmInstruction) {

            case SPACE:
                return encodeSpaceAssemblerInstruction(byteArrayOutStream, asmLine);

            case BYTE:
                return encodeByteAssemblerInstruction(byteArrayOutStream, asmLine);

            case WORD:
                return encodeWordAssemblerInstruction(byteArrayOutStream, asmLine);

            case ASCII:
                return encodeAsciiAssemblerInstruction(byteArrayOutStream, asmLine);

            case ASCIZ:
            case ASCIIZ:
            case STRING:
                return encodeStringAssemblerInstruction(byteArrayOutStream, asmLine);

            case FILE:
            case TEXT:
            case DATA:
            case GLOBAL:
            case SECTION:
            case EQU:
                System.out.println("Assembler Instruction is not implemented yet! " + asmLine);
                // nop
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }

        addressSourceAsmLineMap.put(currentAddress, asmLine);

        return 0;
    }

    private int encodeStringAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, true);
    }

    private int encodeAsciiAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream, asmLine.stringValue, false);
    }

    private int encodeSpaceAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {

        String sizeAsString = asmLine.csvList.get(0);
        long sizeInBytes = NumberParseUtil.parseLong(sizeAsString);
        int length = (int) sizeInBytes;
        byte space[] = new byte[length];
        byteArrayOutStream.writeBytes(space);

        return length;
    }

    private int encodeByteAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {

        int length = 0;
        for (String dataAsString : asmLine.csvList) {
            long data = NumberParseUtil.parseLong(dataAsString);
            EncoderUtils.convertToUint8_t(byteArrayOutStream, (int) data);
            length++;
        }

        return length;
    }

    /**
     * This function inserts the bytes right into the code section.
     *
     * @param byteArrayOutStream output stream
     * @param asmLine            the line to encode
     * @return the length, amount of bytes used by this instruction
     * @throws IOException
     */
    private int encodeWordAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) throws IOException {

        int length = 0;
        for (String dataAsString : asmLine.csvList) {
            long data = NumberParseUtil.parseLong(dataAsString);
            EncoderUtils.convertToUint32_t(byteArrayOutStream, (int) data, ByteOrder.LITTLE_ENDIAN);
            length += 4;
        }

        return length;
    }

}
