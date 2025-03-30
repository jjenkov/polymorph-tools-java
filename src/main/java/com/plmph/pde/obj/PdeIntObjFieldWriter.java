package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class PdeIntObjFieldWriter implements PdeObjectFieldWriter{

    private Field  field    = null;
    private byte[] keyBytes = null;

    public PdeIntObjFieldWriter(Field field) {
        this.field    = field;
        this.keyBytes = field.getName().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int writeKey(byte[] dest, int offset) {
        return PdeObjectWriterUtil.writeKey(dest, offset, this.keyBytes);
    }


    @Override
    public int writeValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        Integer value = (Integer) this.field.get(srcObj);
        int length = 0;


        if(value == null) {
            dest[offset] = PdeFieldTypes.INT_NULL;
        } else {
            int intValue = value.intValue();
            if(intValue >= 0) {
                length = PdeUtil.byteLengthOfInt32Value(intValue);
                dest[offset++] = (byte) (0xFF & (PdeFieldTypes.INT_NULL + length));
                for(int i=0, n=length*8; i < n; i+=8){
                    dest[offset++] = (byte) (0xFF & (intValue >> i));
                }
            } else {
                intValue = -(intValue +1);
                length = PdeUtil.byteLengthOfInt32Value(intValue);
                dest[offset++] = (byte) (0xFF & (PdeFieldTypes.INT_POS_8_BYTES + length));
                for(int i=0, n=length*8; i < n; i+=8){
                    dest[offset++] = (byte) (0xFF & (intValue >> i));
                }
            }
        }

        return 1 + length;
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
