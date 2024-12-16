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

    public static int encodeStringResolveEscapedCharacters(ByteArrayOutputStream byteArrayOutStream,
            String stringValue, boolean zeroTerminate) {

        int length = 0;
        boolean decode = false;
        for (char data : stringValue.toCharArray()) {

            if (data == '\\') {
                decode = true;
                continue;
            }

            if (data == 'n' && decode) {
                byteArrayOutStream.write(0x0A);
                length++;
                decode = false;
                continue;
            } else if (decode) {
                byteArrayOutStream.write('\\');
                length++;
                byteArrayOutStream.write(data);
                length++;
            } else {
                byteArrayOutStream.write(data);
                length++;
            }
        }
        if (zeroTerminate) {
            byteArrayOutStream.write(0);
            length++;
        }

        return length;
    }

}
