package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class PdeFloatObjFieldWriter implements PdeObjectFieldWriter{

    private Field  field    = null;
    private byte[] keyBytes = null;

    public PdeFloatObjFieldWriter(Field field) {
        this.field    = field;
        this.keyBytes = field.getName().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int writeKey(byte[] dest, int offset) {
        return PdeObjectWriterUtil.writeKey(dest, offset, this.keyBytes);
    }


    @Override
    public int writeValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        Float value = (Float) this.field.get(srcObj);
        int length = 0;

        if(value == null) {
            dest[offset] = PdeFieldTypes.FLOAT_NULL;
        } else {
            float floatValue = value.floatValue();

            int intBits = Float.floatToIntBits(floatValue);
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.FLOAT_4_BYTES));

            for(int i=0, n=4*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (intBits >> i));
            }
        }

        return 5;
    }

    @Override
    public int writeKeyAndValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        int keyTotalLength   = this.writeKey(dest, offset);
        int valueTotalLength = this.writeValue(dest, offset + keyTotalLength, srcObj);

        return keyTotalLength + valueTotalLength;
    }

    @Override
    public int writeKeyAndValueIfNotNull(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        if(this.field.get(srcObj) ==  null){
            return 0;
        }
        return this.writeKeyAndValue(dest, offset, srcObj);
    }

}
