package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

public class PdeUtf8ObjFieldWriter implements PdeObjectFieldWriter{

    private Field  field    = null;
    private byte[] keyBytes = null;

    public PdeUtf8ObjFieldWriter(Field field) {
        this.field    = field;
        this.keyBytes = field.getName().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public int writeKey(byte[] dest, int offset) {
        return PdeObjectWriterUtil.writeKey(dest, offset, this.keyBytes);
    }


    @Override
    public int writeValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException {
        String value = (String) this.field.get(srcObj);
        int length = 0;
        int lengthByteCount = 0;

        if(value == null) {
            dest[offset] = PdeFieldTypes.UTF_8_NULL;
            return 1;
        }

        byte[] utf8Bytes = value.getBytes(StandardCharsets.UTF_8);
        length = utf8Bytes.length;

        if(utf8Bytes.length < 16){
            dest[offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_0_BYTES + length));

            System.arraycopy(utf8Bytes,0, dest, offset, length);
            return 1 + length;
        }

        lengthByteCount = PdeUtil.byteCountForLength(length);

        dest[offset++] = (byte) (0xFF & (PdeFieldTypes.UTF_8_15_BYTES + lengthByteCount));
        for (int i = 0, n = lengthByteCount * 8; i < n; i += 8) {
            dest[offset++] = (byte) (0xFF & (length >> i));
        }

        System.arraycopy(utf8Bytes,0, dest, offset, length);
        return 1 + lengthByteCount + length;
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
