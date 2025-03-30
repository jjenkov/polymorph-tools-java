package com.plmph.pde.obj;

import java.lang.reflect.Field;

public interface PdeObjectFieldWriter {

    public int writeKey(byte[] dest, int offset);
    public int writeValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException;
    public int writeKeyAndValue(byte[] dest, int offset, Object srcObj) throws IllegalAccessException;;

    public int writeKeyAndValueIfNotNull(byte[] dest, int offset, Object srcObj) throws IllegalAccessException;;

}
