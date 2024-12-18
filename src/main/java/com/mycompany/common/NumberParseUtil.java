package com.mycompany.common;

public class NumberParseUtil {

    private NumberParseUtil() {
		// no instances of this class
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

}
