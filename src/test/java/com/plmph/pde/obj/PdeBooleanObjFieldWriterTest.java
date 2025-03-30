package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdeBooleanObjFieldWriterTest {


    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {

        byte[] dest = new byte[1024];

        Field isValid = Pojo1.class.getField("isValid");
        PdeBooleanObjFieldWriter fieldWriter = new PdeBooleanObjFieldWriter(isValid);

        Pojo1 pojo = new Pojo1();
        int bytesWritten = fieldWriter.writeKeyAndValue(dest, 0, pojo);

        System.out.println(bytesWritten);

        assertEquals(9, bytesWritten);

        int offset = 0;
        assertEquals(PdeFieldTypes.KEY_7_BYTES, 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('s', 0xFF & dest[offset++]);
        assertEquals('V', 0xFF & dest[offset++]);
        assertEquals('a', 0xFF & dest[offset++]);
        assertEquals('l', 0xFF & dest[offset++]);
        assertEquals('i', 0xFF & dest[offset++]);
        assertEquals('d', 0xFF & dest[offset++]);

        assertEquals(PdeFieldTypes.BOOLEAN_FALSE, 0xFF & dest[offset++]);

    }
}
