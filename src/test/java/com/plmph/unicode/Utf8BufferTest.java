package com.plmph.unicode;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Utf8BufferTest {

    @Test
    public void test() {
        Utf8Buffer utf8Buffer = new Utf8Buffer(new byte[1024], 0, 0);

        // 1 byte UTF-8 char
        utf8Buffer.writeCodepoint(0x7F);
        utf8Buffer.calculateLengthAndEndOffset();

        assertEquals(1, utf8Buffer.length);
        assertEquals(1, utf8Buffer.tempOffset);
        assertEquals(0x7F, 0xFF & utf8Buffer.buffer[0]);

        // 2 byte UTF-8 char
        utf8Buffer.writeCodepoint(0x80);
        utf8Buffer.calculateLengthAndEndOffset();

        assertEquals(3, utf8Buffer.length);
        assertEquals(3, utf8Buffer.tempOffset);
        assertEquals(0b1100_0010, 0xFF & utf8Buffer.buffer[1]);
        assertEquals(0b1000_0000, 0xFF & utf8Buffer.buffer[2]);

        // 3 byte UTF-8 char
        utf8Buffer.writeCodepoint(0x00_00_F7_81);
        utf8Buffer.calculateLengthAndEndOffset();

        assertEquals(6, utf8Buffer.length);
        assertEquals(6, utf8Buffer.tempOffset);
        assertEquals(0b1110_1111, 0xFF & utf8Buffer.buffer[3]);
        assertEquals(0b1001_1110, 0xFF & utf8Buffer.buffer[4]);
        assertEquals(0b1000_0001, 0xFF & utf8Buffer.buffer[5]);

        // 4 byte UTF-8 char
        utf8Buffer.writeCodepoint(0x00_01_77_77);
        utf8Buffer.calculateLengthAndEndOffset();

        assertEquals(10, utf8Buffer.length);
        assertEquals(10, utf8Buffer.tempOffset);
        assertEquals(0b1111_0000, 0xFF & utf8Buffer.buffer[6]);
        assertEquals(0b1001_0111, 0xFF & utf8Buffer.buffer[7]);
        assertEquals(0b1001_1101, 0xFF & utf8Buffer.buffer[8]);
        assertEquals(0b1011_0111, 0xFF & utf8Buffer.buffer[9]);

        utf8Buffer.rewind();
        assertEquals(10, utf8Buffer.length);
        assertEquals(0, utf8Buffer.tempOffset);

        int nextCodePoint = utf8Buffer.nextCodepointUtf8();
        assertEquals(0x7F, nextCodePoint);
        assertEquals(1, utf8Buffer.tempOffset);

        nextCodePoint = utf8Buffer.nextCodepointUtf8();
        assertEquals(0x80, nextCodePoint);
        assertEquals(3, utf8Buffer.tempOffset);

        nextCodePoint = utf8Buffer.nextCodepointUtf8();
        assertEquals(0x00_00_F7_81, nextCodePoint);
        assertEquals(6, utf8Buffer.tempOffset);

        nextCodePoint = utf8Buffer.nextCodepointUtf8();
        assertEquals(0x00_01_77_77, nextCodePoint);
        assertEquals(10, utf8Buffer.tempOffset);
    }
}
