package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class PdeBooleanObjFieldWriter implements PdeObjectFieldWriter{

    private Field  field    = null;
    private byte[] keyBytes = null;

    public PdeBooleanObjFieldWriter(Field field) {
        this.field    = field;
        this.keyBytes = field.getName().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int writeKey(byte[] dest, int offset) {
        //todo move this to a method where it can be reused across all PdeObjectFieldWriter implementations.

        return PdeObjectWriterUtil.writeKey(dest, offset, this.keyBytes);

        /*
        int length       = this.keyBytes.length;
        int lengthLength = 0;

        if(length < 16){
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_0_BYTES + length));
        } else {
            lengthLength = PdeUtil.byteLengthOfInt64Value(length);
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.KEY_15_BYTES + lengthLength));
            for(int i=0, n=lengthLength*8; i < n; i+=8){
                dest[offset++] = (byte) (0xFF & (length >> i));
            }
        }

        System.arraycopy(this.keyBytes, 0, dest, offset + lengthLength, length);

        return 1 + lengthLength + length;
        */
    }


    @Override
    public int writeValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        Boolean value = (Boolean) this.field.get(srcObj);

        if(value == null) {
            dest[offset] = PdeFieldTypes.BOOLEAN_NULL;
        } else {
            boolean booleanValue = value.booleanValue();
            if(booleanValue) {
                dest[offset] = PdeFieldTypes.BOOLEAN_TRUE;
            } else {
                dest[offset] = PdeFieldTypes.BOOLEAN_FALSE;
            }
        }

        return 1;
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
