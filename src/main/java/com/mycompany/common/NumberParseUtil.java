package com.mycompany.common;

public class NumberParseUtil {

    private NumberParseUtil() {
		// no instances of this class
	}

    public static long parseHexToLong(final String data) {
        return parseLong(data);
    }

    public static long parseLong(final String data) {

        String tempData = data;
        int base = 10;
        if (tempData.startsWith("0x")) {
            tempData = tempData.substring(2);
            base = 16;
        }

        return Long.parseLong(tempData, base);
    }

    public static long sign_extend_12_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFF;
        long extended = (unpacked ^ 0x800) - 0x800;

        return extended;
    }

    public static long sign_extend_16_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFFF;
        long extended = (unpacked ^ 0x8000) - 0x8000;

        return extended;
    }

    public static long sign_extend_20_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFFFF;
        long extended = (unpacked ^ 0x80000) - 0x80000;

        return extended;
    }

}
