package com.plmph.pdl;

import com.plmph.unicode.Utf8Buffer;


public class PdlTokenizer {

    private static final byte[] tokenEndCharacters = new byte[128];

    static {
        // default end character is ;
        for(int i=0; i<tokenEndCharacters.length; i++) {
            tokenEndCharacters[i] = ';';
        }
        // single-character tokens end with themselves as end character.
        tokenEndCharacters['{'] = '{';
        tokenEndCharacters['}'] = '}';
        tokenEndCharacters['['] = '[';
        tokenEndCharacters[']'] = ']';
        tokenEndCharacters['<'] = '<';
        tokenEndCharacters['>'] = '>';
        tokenEndCharacters['('] = '(';
        tokenEndCharacters[')'] = ')';
        tokenEndCharacters['*'] = '~';

        // tokens that start with a letter (a-z + A-Z) are named tokens ending with a ( as end character.
        for(int i='a'; i<'z'; i++){
            tokenEndCharacters[i] = '(';
        }
        for(int i='A'; i<'Z'; i++){
            tokenEndCharacters[i] = '(';
        }
    }


    //todo create a simplified tokenize() method that takes a byte array + offset + length, and a long array to write token offsets into

    public static int tokenizeMinified(byte[] source, int offset, int length, int[] tokenOffsets){

        return 0; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static int tokenize(byte[] source, int offset, int length, long[] tokenOffsets){
        int tokenOffsetsIndex = 0;
        int endOffset = offset + length;

        //skip whitespace
        while(offset < endOffset){
            if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
            offset++;
        }

        while(offset < endOffset){
            int tokenStartOffset  = offset;
            int tokenEndCharacter = tokenEndCharacters[source[tokenStartOffset]];

            while(offset < endOffset){
                if(source[offset] == tokenEndCharacter) { break; }
                offset++;
            }

            offset++; // We want the index right after the last character in the token.
            long tokenOffsetPair = (long) ((long) offset << 32 | (long) tokenStartOffset);
            tokenOffsets[tokenOffsetsIndex++] = tokenOffsetPair;

            //skip whitespace
            while(offset < endOffset){
                if(source[offset] > 32 ) { break; } // All ASCII values from 32 and down are considered white space
                offset++;
            }
        }

        return tokenOffsetsIndex; // return number of token offsets written into tokenOffset (=number of tokens found)
    }

    public static void tokenize(Utf8Buffer buffer, TokenizerListener listener) {
        buffer.skipWhiteSpace();
        while(buffer.hasMoreBytes()){
            int tokenStartOffset = buffer.tempOffset;

            int endCharacter = tokenEndCharacters[buffer.buffer[tokenStartOffset]];

            while(buffer.hasMoreBytes()){
                //todo experiment with using a local variable as index into buffer, instead of a variable stored in the buffer object - in RAM.
                if(buffer.nextCodepointAscii() == endCharacter){
                    break;
                }
            }

            // todo Write token start offset + token end offset to a long array (long[]) instead of calling a listener.
            //      Or make a version of this method that does that.
            listener.token(tokenStartOffset, buffer.tempOffset, buffer.buffer[tokenStartOffset]);
            buffer.skipWhiteSpace();
        }
    }
}
