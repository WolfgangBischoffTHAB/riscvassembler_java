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

    public static long sign_extend_6_bit_to_int32_t(long value) {
        long unpacked = value & 0x3F;
        long extended = (unpacked ^ 0x20) - 0x20;

        return extended;
    }

    public static long sign_extend_8_bit_to_int32_t(long value) {
        long unpacked = value & 0xFF;
        long extended = (unpacked ^ 0x80) - 0x80;

        return extended;
    }

    public static long sign_extend_9_bit_to_int32_t(long value) {
        long unpacked = value & 0x1FF;
        long extended = (unpacked ^ 0x100) - 0x100;

        return extended;
    }

    public static long sign_extend_12_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFF;
        long extended = (unpacked ^ 0x800) - 0x800;

        return extended;
    }

    public static long sign_extend_13_bit_to_int32_t(long value) {
        long unpacked = value & 0x1FFF;
        long extended = (unpacked ^ 0x1000) - 0x1000;

        return extended;
    }

    public static long sign_extend_16_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFFF;
        long extended = (unpacked ^ 0x8000) - 0x8000;

        return extended;
    }

    public static long sign_extend_17_bit_to_int32_t(long value) {
        long unpacked = value & 0x1FFFF;
        long extended = (unpacked ^ 0x10000) - 0x10000;

        return extended;
    }

    public static long sign_extend_20_bit_to_int32_t(long value) {
        long unpacked = value & 0xFFFFF;
        long extended = (unpacked ^ 0x80000) - 0x80000;

        return extended;
    }

    public static long sign_extend_21_bit_to_int32_t(long value) {
        long unpacked = value & 0x1FFFFF;
        long extended = (unpacked ^ 0x100000) - 0x100000;

        return extended;
    }

    public static long sign_extend_12_bit_to_int64_t(long value) {
        long unpacked = value & 0xFFF;
        long extended = (unpacked ^ 0x800) - 0x800;

        return extended;
    }

    public static long sign_extend_32_bit_to_int64_t(long value) {
        long unpacked = value & 0xFFFFFFFF;
        long extended = (unpacked ^ 0x80000000) - 0x80000000;

        return extended;
    }    

}
