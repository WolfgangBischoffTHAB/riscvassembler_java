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

    public static void encodeStringResolveEscapedCharacters(ByteArrayOutputStream byteArrayOutStream,
        String stringValue) {

        boolean decode = false;
        for (char data : stringValue.toCharArray()) {

            if (data == '\\') {
                decode = true;
                continue;
            }

            if (data == 'n' && decode) {
                byteArrayOutStream.write(0x0A);
                decode = false;
                continue;
            } else if (decode) {
                byteArrayOutStream.write('\\');
                byteArrayOutStream.write(data);
            } else {
                byteArrayOutStream.write(data);
            }
        }
        byteArrayOutStream.write(0);
    }

}
