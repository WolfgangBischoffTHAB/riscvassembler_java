package com.mycompany.common;

public class StringUtils {

    public static int stringLengthWithEscape(String stringValue) {

        int length = 0;
        boolean escape = false;
        for (char data : stringValue.toCharArray()) {

            if (data == '\\') {
                escape = true;
                continue;
            }

            if (data == 'n' && escape) {
                length++;
                escape = false;
            } else if (data == 't' && escape) {
                length++;
                escape = false;
            } else if (escape) {
                throw new RuntimeException("");
            } else {
                length++;
            }
        }

        return length;
    }

}
