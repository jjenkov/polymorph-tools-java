package com.plmph.hex;

public class HexUtil {

    public static final char[] hexChars = new char[16];
    static {
        hexChars[0] = '0';
        hexChars[1] = '1';
        hexChars[2] = '2';
        hexChars[3] = '3';
        hexChars[4] = '4';
        hexChars[5] = '5';
        hexChars[6] = '6';
        hexChars[7] = '7';
        hexChars[8] = '8';
        hexChars[9] = '9';
        hexChars[10] = 'A';
        hexChars[11] = 'B';
        hexChars[12] = 'C';
        hexChars[13] = 'D';
        hexChars[14] = 'E';
        hexChars[15] = 'F';
    }

    public static void convert(byte[] source, int offset, int length, byte[] dest, int destOffset){
        for(int i=offset, n=offset + length; i < n; i++) {
            int byteVal = 0xFF & source[i];

            int leftDigit  = byteVal >> 4;
            int rightDigit = byteVal & 0x0F;

            char leftChar  = hexChars[leftDigit];
            char rightChar = hexChars[rightDigit];

            dest[destOffset++] = (byte) (0xFF & leftChar);
            dest[destOffset++] = (byte) (0xFF & rightChar);
        }
    }

    public static StringBuffer convert(byte[] source, int offset, int length, StringBuffer dest) {
        for(int i=offset, n=offset + length; i < n; i++) {
            int byteVal = 0xFF & source[i];

            int leftDigit  = byteVal >> 4;
            int rightDigit = byteVal & 0x0F;

            char leftChar  = hexChars[leftDigit];
            char rightChar = hexChars[rightDigit];

            dest.append(leftChar);
            dest.append(rightChar);
        }
        return dest;
    }


}
