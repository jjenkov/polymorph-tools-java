package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeUtil;

public class PdeObjectWriterUtil {

    public static final int writeKey(byte[] dest, int offset, byte[] keyBytes) {
        int length = keyBytes.length;
        int lengthLength = 0;

        if (length < 16) {
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_0_BYTES + length));
        } else {
            lengthLength = PdeUtil.byteLengthOfInt64Value(length);
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_15_BYTES + lengthLength));
            for (int i = 0, n = lengthLength * 8; i < n; i += 8) {
                dest[offset++] = (byte) (0xFF & (length >> i));
            }
        }

        System.arraycopy(keyBytes, 0, dest, offset, length);

        return 1 + lengthLength + length;
    }
}
