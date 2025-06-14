package com.mycompany.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ByteArrayUtil {

	public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static final char[] HEX_ARRAY_LOWERCASE = "0123456789abcdef".toCharArray();

	private ByteArrayUtil() {
		// no instances of this class
	}

	public static String bytesToYat(final byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 6];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;

			hexChars[j * 6 + 0] = '\\';
			hexChars[j * 6 + 1] = 'h';
			hexChars[j * 6 + 2] = '(';
			hexChars[j * 6 + 3] = HEX_ARRAY[v >>> 4];
			hexChars[j * 6 + 4] = HEX_ARRAY[v & 0x0F];
			hexChars[j * 6 + 5] = ')';
		}
		return new String(hexChars);
	}

	public static String bytesToHex(final byte[] bytes) {
		return bytesToHex(bytes, bytes.length, HEX_ARRAY);
	}

    public static String bytesToHexLowerCase(final byte[] bytes) {
		return bytesToHex(bytes, bytes.length, HEX_ARRAY_LOWERCASE);
	}

	public static String bytesToHexUpperCase(final byte[] bytes) {
		return bytesToHex(bytes, bytes.length, HEX_ARRAY);
	}

	public static String bytesToHex(final byte[] bytes, final int length, char[] hex_array) {

		final char[] hexChars = new char[bytes.length * 2];

		final int stopCondition = Math.min(bytes.length, length);
		for (int j = 0; j < stopCondition; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hex_array[v >>> 4];
			hexChars[j * 2 + 1] = hex_array[v & 0x0F];
		}

		return new String(hexChars);
	}

	public static String bytesToHex(final byte[] bytes, final char separator) {
		return bytesToHex(bytes, bytes.length, separator);
	}

	/**
	 * Converts a byte array to a hex string. This version of the function will stop
	 * assembling the hex string after the specified length. This allows you to
	 * create a string with an upper bound in length.
	 *
	 * @param bytes     the byte array to convert into a string
	 * @param length    the max amount of bytes that are converted into the
	 *                  resulting string
	 * @param separator the separator
	 * @return the resulting string
	 */
	public static String bytesToHex(final byte[] bytes, final int length, final char separator) {

		final char[] hexChars = new char[bytes.length * 3];

		final int stopCondition = Math.min(bytes.length, length);

		for (int j = 0; j < stopCondition; j++) {

			final int v = bytes[j] & 0xFF;
			hexChars[j * 3] = HEX_ARRAY[v >>> 4];
			hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];

			if ((j + 1) != stopCondition) {
				hexChars[j * 3 + 2] = separator;
			}
		}

		return new String(hexChars);
	}

	public static String bytesToHex(final int[] bytes) {
		return bytesToHex(bytes, bytes.length);
	}

	public static String byteToHex(final int data) {
		return "0x" + String.format("%1$02X", data);
	}

	public static String byteToHex(final byte data) {
		return "0x" + String.format("%1$02X", data);
	}

	public static String bytesToHex(final int[] bytes, final int length) {

		final char[] hexChars = new char[bytes.length * 2];
		final int stopCondition = Math.min(bytes.length, length);
		for (int j = 0; j < stopCondition; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}

		return new String(hexChars);
	}

	/**
	 * Usage:
	 *
	 * <pre>
	 * static final String LIFETIME_REQUEST_FROM_DCU_TO_DCU112 = "02 31 B1 81 E4 03".replaceAll("\\s+", "");
	 * byte[] data = ByteArrayUtil.hexStringToByteArray(LIFETIME_REQUEST_FROM_DCU_TO_DCU112);
	 * </pre>
	 *
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(final String s) {

		final String temp = s.replaceAll("\\s+", "");
		final int len = temp.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(temp.charAt(i), 16) << 4) + Character.digit(temp.charAt(i + 1), 16));
		}

		return data;
	}

	public static int[] hexStringToIntArray(final String s) {

		final String temp = s.replaceAll("\\s+", "");
		final int len = temp.length();
		final int[] data = new int[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (Character.digit(temp.charAt(i), 16) << 4) + Character.digit(temp.charAt(i + 1), 16);
		}

		return data;
	}

	/**
	 * Parse strings in the format: 02 4c 81 00 00 00 00 81 cf 03
	 *
	 * @param s
	 * @return
	 */
	public static byte[] yatHexStringToByteArray(final String s) {

		final String temp = s.replaceAll("\\s+", "");
		final int len = temp.length();
		final byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(temp.charAt(i), 16) << 4) + Character.digit(temp.charAt(i + 1), 16));
		}

		return data;
	}

	/**
	 * Parse strings in the format: 02h 4ch 81h 00h 00h 00h 00h 81h cfh 03h
	 *
	 * @param s
	 * @return
	 */
	public static byte[] yatWithHPostfixHexStringToByteArray(final String s) {

		final String temp = s.replaceAll("\\s+", "");
		final int len = temp.length();
		final byte[] data = new byte[len / 3];
		for (int i = 0; i < len; i += 3) {
			data[i / 3] = (byte) ((Character.digit(temp.charAt(i), 16) << 4) + Character.digit(temp.charAt(i + 1), 16));
		}

		return data;
	}

	/**
	 * Takes an int, treats it as s single byte and outputs the byte's value as a
	 * hex string.
	 *
	 * @param address
	 * @return
	 */
	public static String intToHex(final int data) {
		return intToHex("%02X", data);
	}

    public static String intToHexLowerCase(final int data) {
		return intToHex("%02x", data);
	}

	public static String intToHex(final String format, final int data) {
		return String.format(format, data);
	}

    public static String longToHex(Long data) {
        return String.format("%08x", data);
    }

	// /**
	//  * Searches 'searchData' within 'data'.
	//  *
	//  * @param data
	//  * @param searchData
	//  */
	// public static boolean contains(final byte[] data, final byte[] searchData) {
	// 	return BoyerMoore.indexOf(data, searchData) >= 0;
	// }

	public static boolean compare(final byte[] lhs, final byte[] rhs) {
		return Arrays.equals(lhs, rhs);
	}

	public static int twoByteToInt(final byte high, final byte low) {
		return (high & 0xFF) << 8 | (low & 0xFF);
	}

	public static byte[] intToTwoByte_reverse(final int data) {

		final byte[] result = new byte[2];
		result[0] = (byte) (data & 0xFF);
		result[1] = (byte) ((data >> 8) & 0xFF);

		return result;
	}

	public static byte[] intToTwoByte(final int data) {

		final byte[] result = new byte[2];
		result[0] = (byte) ((data >> 8) & 0xFF);
		result[1] = (byte) (data & 0xFF);

		return result;
	}

	public static byte[] intToFourByte(final int i, final ByteOrder byteOrder) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.order(byteOrder);
		byteBuffer.putInt(i);

		return byteBuffer.array();
	}

	public static int fourByteToInt(final byte[] data, final ByteOrder byteOrder) {
		final ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		byteBuffer.order(byteOrder);

		return byteBuffer.getInt();
	}

    public static int fourByteToInt(final byte a, final byte b, final byte c, final byte d, final ByteOrder byteOrder) {
		byte[] data = new byte[4];
		data[0] = a;
		data[1] = b;
		data[2] = c;
		data[3] = d;

		return fourByteToInt(data, byteOrder);
    }

}
