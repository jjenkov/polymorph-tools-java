package com.plmph.pdl;


public interface TokenizerListener {


    // Todo Is Utf8Buffer necessary as parameter?
    // Todo Should tokenType just be a byte? Or a short?
    public void token(int fromOffset, int toOffset, int tokenType);

    public void clear();
}
