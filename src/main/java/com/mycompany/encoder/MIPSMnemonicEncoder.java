package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.mycompany.data.AsmLine;

public class MIPSMnemonicEncoder implements MnemonicEncoder {

    @Override
    public int encodeMnemonic(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine,
            Map<String, Long> labelAddressMap, long currentAddress) throws IOException {

        switch (asmLine.mnemonic) {

            case I_ADD:
                return encodeADD(byteArrayOutStream, asmLine, labelAddressMap, currentAddress);

            case I_UNKNOWN:
            default:
                throw new RuntimeException("Unknown mnemonic: " + asmLine);
        }

    }

    private int encodeADD(ByteArrayOutputStream byteArrayOutStream, AsmLine<?> asmLine,
            Map<String, Long> labelAddressMap, long currentAddress) throws IOException {

        int special = 0b000000;
        int rs = asmLine.register_1.getIndex();
        int rt = asmLine.register_2.getIndex();
        int rd = asmLine.register_0.getIndex();
        int unknown = 0;
        int opcode = 0b100000;

        int result = encodeRType(special, rs, rt, rd, unknown, opcode);
        System.out.println(asmLine + " -> " + String.format("%08X", result));
        EncoderUtils.convertToUint32_t(byteArrayOutStream, result);

        return 4;
    }

    private int encodeRType(int special, int rs, int rt, int rd, int unknown, int opcode) {
        int result = (special << 6 + 5 + 5 + 5 + 5) | (rs << 6 + 5 + 5 + 5) | (rt << 6 + 5 + 5) | (rd << 6 + 5)
                | (unknown << 6) | (opcode << 0);
        return result;
    }

}
