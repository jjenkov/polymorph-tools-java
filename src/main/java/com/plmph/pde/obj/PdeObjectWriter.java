package com.plmph.pde.obj;

public interface PdeObjectWriter<T> {

    public int writeKeysAndValues(byte[] dest, int offset, T object, int lengthByteCount) throws IllegalAccessException;

}
