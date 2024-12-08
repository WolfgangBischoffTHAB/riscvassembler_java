package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;

public class EncoderUtils {

    public static void convertToUint8_t(final ByteArrayOutputStream byteArrayOutStream, final int result) {
        byteArrayOutStream.write((byte) result);
    }

    public static void convertToUint32_t(final ByteArrayOutputStream byteArrayOutStream, final int result) {
        for (int i = 0; i < 4; i++) {
            byteArrayOutStream.write((byte) ((result >> i * 8) & 0xFF));
        }
    }

    public static void encodeString(final ByteArrayOutputStream byteArrayOutStream, final String stringValue) {
        for (char data : stringValue.toCharArray()) {
            byteArrayOutStream.write(data);
        }
        byteArrayOutStream.write(0);
    }

}
