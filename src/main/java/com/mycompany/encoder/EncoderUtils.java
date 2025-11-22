package com.mycompany.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EncoderUtils {

    public static void convertToUint8_t(final ByteArrayOutputStream byteArrayOutStream, final int data) {
        byteArrayOutStream.write((byte) data);
    }

    public static void convertToUint32_t(final ByteArrayOutputStream byteArrayOutStream, final int data)
            throws IOException {
        convertToUint32_t(byteArrayOutStream, data, ByteOrder.LITTLE_ENDIAN);
    }

    public static void convertToUint32_t(final ByteArrayOutputStream byteArrayOutStream, final int data,
            ByteOrder byteOrder) throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(byteOrder);
        byteBuffer.putInt(data);

        byteArrayOutStream.write(byteBuffer.array());
    }

    public static void convertToUint64_t(ByteArrayOutputStream byteArrayOutStream, long data, ByteOrder byteOrder)
            throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(byteOrder);
        byteBuffer.putLong(data);

        byteArrayOutStream.write(byteBuffer.array());
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
        boolean escape = false;
        for (char data : stringValue.toCharArray()) {

            if (data == '\\') {
                escape = true;
                continue;
            }

            if (data == 'n' && escape) {
                byteArrayOutStream.write(0x0A);
                length++;
                escape = false;
                // continue;
            } else if (escape) {
                // byteArrayOutStream.write('\\');
                // length++;
                // byteArrayOutStream.write(data);
                // length++;
                throw new RuntimeException("");
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
