package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

import com.mycompany.data.AsmLine;

public class Encoder {

    public ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();

    public void encode(final AsmLine asmLine) {

        switch (asmLine.getAsmLineType()) {

            case MNEMONIC:
                encodeMnemonic(asmLine);
                break;

            case ASSEMBLER_INSTRUCTION:
                encodeAssemblerInstruction(asmLine);
                break;

            default:
                // throw new RuntimeException("Unknown AsmLine type: " + asmLine);
                System.out.println("Not encoding: " + asmLine);
        }
    }

    private void encodeAssemblerInstruction(final AsmLine asmLine) {
        switch (asmLine.asmInstruction) {

            case WORD:
                encodeWordAssemblerInstruction(asmLine);
                break;

            default:
                throw new RuntimeException("Unknown assembler instruction: " + asmLine);
        }
    }

    private void encodeWordAssemblerInstruction(AsmLine asmLine) {

        for (String dataAsString : asmLine.csvList) {

            long data = 0L;
            if (dataAsString.startsWith("0x")) {
                dataAsString = dataAsString.substring(2);
                data = Long.parseLong(dataAsString, 16);
            } else {
                data = Long.parseLong(dataAsString, 16);
            }

            // for (int i = 0; i < 4; i++) {
            // byteArrayOutStream.write((byte)(data & 0xFF));
            // data = data >> 8;
            // }

            for (int i = 0; i < 4; i++) {
                byteArrayOutStream.write((byte) ((data >> (4 - 1 - i) * 8) & 0xFF));
            }
        }
    }

    private void encodeMnemonic(final AsmLine asmLine) {

        switch (asmLine.mnemonic) {

            case I_ADDI:
                byte funct3 = 0b000;
                byte opcode = 0b0010011;

                // addi sp, sp, 0xFFFFFFE0
                int result = encodeIType(asmLine.numeric_2.shortValue(),
                        (byte) asmLine.register_1.ordinal(),
                        funct3,
                        (byte) asmLine.register_0.ordinal(),
                        opcode);

                for (int i = 0; i < 4; i++) {
                    byteArrayOutStream.write((byte) ((result >> (4 - 1 - i) * 8) & 0xFF));
                }

                break;

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }
    }

    private int encodeIType(short imm, byte rs1, byte funct3, byte rd, byte opcode) {

        int result = ((opcode & 0b1111111) << 0) |
                ((rd & 0b11111) << 7) |
                ((funct3 & 0b111) << (7 + 5)) |
                ((rs1 & 0b11111) << (7 + 5 + 3)) |
                ((imm & 0b111111111111) << (7 + 5 + 3 + 5));

        return result;
    }

}
