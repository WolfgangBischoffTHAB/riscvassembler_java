package com.mycompany.encoder;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mycompany.common.NumberParseUtil;
import com.mycompany.data.AsmInstruction;
import com.mycompany.data.AsmLine;

/**
 * Encode instructions directed at the assembler: .BYTE, .WORD, ...
 */
public class AsmInstructionEncoder {

    private static final Logger logger = LoggerFactory.getLogger(AsmInstructionEncoder.class);

    private RISCVEncoderMode encoderMode = RISCVEncoderMode.UNKNOWN;

    private ByteArrayOutputStream stringByteArrayOutputStream = new ByteArrayOutputStream();

    /**
     * Places data described by an assembler instruction into memory.
     * 
     * @param byteArrayOutStream
     * @param asmLine
     * @param addressSourceAsmLineMap
     * @param currentAddress
     * @return amount of bytes used to encode the data described by the assembler
     *         instruction
     * @throws IOException
     */
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

        // // start string buffer mode if ASCIZ, ASCIIZ and STRING
        // if ((asmLine.asmInstruction == AsmInstruction.ASCIZ) ||
        //         (asmLine.asmInstruction == AsmInstruction.ASCIIZ) ||
        //         (asmLine.asmInstruction == AsmInstruction.STRING)) {
        //     startStringMode();
        // }

        switch (asmLine.asmInstruction) {

            case ZERO:
                // reserve space and prefill the space with 0x00
                return encodeZeroAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case SPACE:
                return encodeSpaceAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case BYTE:
                return encodeByteAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case WORD:
                return encodeWordAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case QUAD:
                return encodeQuadAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case ASCII:
                return encodeAsciiAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case ASCIZ:
            case ASCIIZ:
            case STRING:
                // do not directly encode into the byteArrayOutStream but buffer all
                // string output into the stringByteArrayOutputStream. The idea is
                // that if there are several, consecutive .string assembler instructions,
                // the strings have to be placed in memory in a consecutive fashion and
                // after the last string, the block is zero padded until 32 bit alignment
                // is restored.
                //
                // example:
                // .string "A"
                // .string "%6d"
                // .string "B"
                // .string "C"
                // .string "DCBA"
                //
                // yields:
                // 36250041
                // 00420064
                // 43440043
                // 00004142
                //
                // In the resulting bytes, you can see that all strings are added into memory
                // in a tighly packed fashion and after the last instruction, the string block
                // is padded with zeroes for alignment.

                return encodeStringAssemblerInstruction(stringByteArrayOutputStream, asmLine);

            case SECTION:
                logger.error("section");
                break;

            case FILE:
            case TEXT:
            case GLOBAL:
            case DATA:
            case EQU:
                logger.error("Assembler instruction is not implemented yet! " + asmLine);
                // nop
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }

        // // keep string buffer mode alive if ASCIZ, ASCIIZ and STRING
        // if ((asmLine.asmInstruction != AsmInstruction.ASCIZ) &&
        //         (asmLine.asmInstruction != AsmInstruction.ASCIIZ) &&
        //         (asmLine.asmInstruction != AsmInstruction.STRING)) {
        //     endStringMode(stringByteArrayOutputStream);
        // }

        // addressSourceAsmLineMap.put(currentAddress, asmLine);

        return 0;
    }

    private int encodeStringAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream,
                asmLine.stringValue, true);
    }

    private int encodeAsciiAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) {
        return EncoderUtils.encodeStringResolveEscapedCharacters(byteArrayOutStream,
                asmLine.stringValue, false);
    }

    private int encodeZeroAssemblerInstruction(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine) {
        int length = asmLine.numeric_0.intValue();
        byte space[] = new byte[length];
        // Java prefills everything with the default value which is zero for byte.
        // Lets prefill anyways because of paranoia
        for (int i = 0; i < length; i++) {
            space[i] = 0x00;
        }
        byteArrayOutStream.writeBytes(space);

        return length;
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

    private int encodeQuadAssemblerInstruction(final ByteArrayOutputStream byteArrayOutStream,
            final AsmLine<?> asmLine) throws IOException {

        int length = 0;
        for (String dataAsString : asmLine.csvList) {
            long data = NumberParseUtil.parseLong(dataAsString);
            EncoderUtils.convertToUint64_t(byteArrayOutStream, data, ByteOrder.LITTLE_ENDIAN);
            length += 8;
        }

        return length;
    }

    public void startStringMode() {
        encoderMode = RISCVEncoderMode.STRING;
    }

    public void endStringMode(final ByteArrayOutputStream byteArrayOutStream) throws IOException {

        if (encoderMode == RISCVEncoderMode.STRING) {

            // // pad with zeroes for proper alignment
            // int length = stringByteArrayOutputStream.size();
            // for (int i = 0; i < (length % 4); i++) {
            //     stringByteArrayOutputStream.write(0);
            // }

            // flush the buffered strings
            byteArrayOutStream.write(stringByteArrayOutputStream.toByteArray());

            // start a new buffer
            stringByteArrayOutputStream = new ByteArrayOutputStream();
        }
        encoderMode = RISCVEncoderMode.NORMAL;
    }

}
