package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

public class EncoderUtils {

    public static void convertToUint32_t(final ByteArrayOutputStream byteArrayOutStream, final int result) {
        for (int i = 0; i < 4; i++) {
            byteArrayOutStream.write((byte) ((result >> (4 - 1 - i) * 8) & 0xFF));
        }
    }

}
