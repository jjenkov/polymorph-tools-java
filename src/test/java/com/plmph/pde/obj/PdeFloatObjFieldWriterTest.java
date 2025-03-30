package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdeFloatObjFieldWriterTest {


    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        byte[] dest = new byte[1024];

        Field floatField = Pojo1.class.getField("floatField");
        PdeFloatObjFieldWriter fieldWriter = new PdeFloatObjFieldWriter(floatField);

        Pojo1 pojo = new Pojo1();
        int bytesWritten = fieldWriter.writeKeyAndValue(dest, 0, pojo);

        System.out.println(bytesWritten);

        assertEquals(16, bytesWritten);

        int offset = 0;
        assertEquals(PdeFieldTypes.KEY_10_BYTES, 0xFF & dest[offset++]);
        assertEquals('f', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('o', 0xFF & dest[offset++]);
        assertEquals('a', 0xFF & dest[offset++]);
        assertEquals('t', 0xFF & dest[offset++]);
        assertEquals('F', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('e', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('d', 0xFF & dest[offset++]);

        assertEquals(PdeFieldTypes.FLOAT_4_BYTES, 0xFF & dest[offset++]);

        int intBits = Float.floatToIntBits(pojo.floatField);
        assertEquals(intBits & 0xFF, 0xFF & dest[offset++]);
        assertEquals((intBits >>  8 ) & 0xFF, 0xFF & dest[offset++]);
        assertEquals((intBits >> 16 ) & 0xFF, 0xFF & dest[offset++]);
        assertEquals((intBits >> 24 ) & 0xFF, 0xFF & dest[offset++]);
        assertEquals(0, 0xFF & dest[offset++]);  // no more in the dest buffer



    }


}
