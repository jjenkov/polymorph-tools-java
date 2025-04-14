package com.plmph.hex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexUtilTest {


    @Test
    public void testBinToHex() {
        byte[] bytes = new byte[]{0,1,2,3,4,5, -1, -2, -3, -4, -5};

        StringBuffer hexBuffer = HexUtil.convert(bytes, 0, bytes.length, new StringBuffer());

        assertEquals("000102030405FFFEFDFCFB", hexBuffer.toString());
    }
}
