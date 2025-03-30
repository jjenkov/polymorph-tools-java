package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdeUtf8ObjFieldWriterTest {


    @Test
    public void testStringUnder16Chars() throws NoSuchFieldException, IllegalAccessException {
        byte[] dest = new byte[1024];

        Field stringField = Pojo1.class.getField("stringField1");
        PdeUtf8ObjFieldWriter fieldWriter = new PdeUtf8ObjFieldWriter(stringField);

        Pojo1 pojo = new Pojo1();
        int bytesWritten = fieldWriter.writeKeyAndValue(dest, 0, pojo);

        System.out.println(bytesWritten);

        assertEquals(19, bytesWritten);

        int offset = 0;
        assertEquals(PdeFieldTypes.KEY_12_BYTES, 0xFF & dest[offset++]);
        assertEquals('s', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
        assertEquals('r', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('n', 0xFF & dest[offset++]);
        assertEquals('g', 0xFF & dest[offset++]);
        assertEquals('F', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('d', 0xFF & dest[offset++]);
        assertEquals('1', 0xFF & dest[offset++]);

        assertEquals(PdeFieldTypes.UTF_8_5_BYTES, 0xFF & dest[offset++]);
        assertEquals('H', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('o', 0xFF & dest[offset++]);
    }

    @Test
    public void testStrinOver16Chars() throws NoSuchFieldException, IllegalAccessException {
        byte[] dest = new byte[1024];

        Field stringField = Pojo1.class.getField("stringField2");
        PdeUtf8ObjFieldWriter fieldWriter = new PdeUtf8ObjFieldWriter(stringField);

        Pojo1 pojo = new Pojo1();
        int bytesWritten = fieldWriter.writeKeyAndValue(dest, 0, pojo);

        System.out.println(bytesWritten);

        assertEquals(32, bytesWritten);

        int offset = 0;
        assertEquals(PdeFieldTypes.KEY_12_BYTES, 0xFF & dest[offset++]);
        assertEquals('s', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
        assertEquals('r', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('n', 0xFF & dest[offset++]);
        assertEquals('g', 0xFF & dest[offset++]);
        assertEquals('F', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('d', 0xFF & dest[offset++]);
        assertEquals('2', 0xFF & dest[offset++]);

        assertEquals(PdeFieldTypes.UTF_8_1_LENGTH_BYTES, 0xFF & dest[offset++]);
        assertEquals( 17, 0xFF & dest[offset++]);
        assertEquals('H', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('o', 0xFF & dest[offset++]);
        assertEquals(' ', 0xFF & dest[offset++]);
        assertEquals('H', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('o', 0xFF & dest[offset++]);
        assertEquals(' ', 0xFF & dest[offset++]);
        assertEquals('H', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('o', 0xFF & dest[offset++]);
    }



}
