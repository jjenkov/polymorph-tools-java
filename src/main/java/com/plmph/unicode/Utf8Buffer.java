package com.plmph.unicode;



/**
 * The Utf8Source class represents a textual source (input) for the Assembler. When the Assembler assembles a text
 * into a binary output, it requires a Utf8Source as input.
 */
public class Utf8Buffer {

    public byte[] buffer;
    public int startOffset;
    public int length;
    public int endOffset;

    /** A field that can be used during parsing of the data in this Utf8Source */
    public int tempOffset;

    /* Use if you want to reuse this instance with different byte arrays, or same byte arrays but different start and end index */
    public Utf8Buffer() {}

    public Utf8Buffer(byte [] data, int startOffset, int length) {
        setBuffer(data);
        setOffsets(startOffset, length);
        /*
        this.buffer = data;
        this.startOffset = startOffset;
        this.tempOffset  = startOffset;
        this.length      = length;
        this.endOffset   = startOffset + length;
        */
    }

    public Utf8Buffer setBuffer(byte[] data){
        this.buffer = data;
        return this;
    }

    public Utf8Buffer setOffsets(int startOffset, int length){
        this.startOffset = startOffset;
        this.tempOffset  = startOffset;
        this.length      = length;
        this.endOffset   = startOffset + length;
        return this;
    }

    public Utf8Buffer clear() {
        this.startOffset = 0;
        this.tempOffset = 0;
        this.length     = 0;
        this.endOffset  = 0;
        return this;
    }

    public Utf8Buffer rewind() {
        this.tempOffset = this.startOffset;
        return this;
    }

    public Utf8Buffer calculateLengthAndEndOffset() {
        this.length = this.tempOffset - this.startOffset;
        this.endOffset = this.tempOffset;
        return this;
    }


    public boolean hasMoreBytes() {
        //todo optimize this? No reason to subtract 1 every time hasMoreBytes() is called.
        //return this.tempOffset < this.endOffset - 1;
        return this.tempOffset < this.endOffset;
    }

    public int writeCodepoints(String characters) {
        int bytesWritten = 0;
        for( int i = 0; i < characters.length(); i++){
            bytesWritten += writeCodepoint(characters.codePointAt(i));
        }
        return bytesWritten;
    }

    public int writeCodepoint(int codepoint) {
        if(codepoint < 0x00_00_00_80){
            // This is a one byte UTF-8 char
            buffer[this.tempOffset++] = (byte) (0xFF & codepoint);
            return 1;
        } else if (codepoint < 0x00_00_08_00) {
            // This is a two byte UTF-8 char. Value is 11 bits long (less than 12 bits in value).
            // Get highest 5 bits into first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1100_0000 | (0b0001_1111 & (codepoint >> 6))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & codepoint)));
            this.tempOffset+=2;
            return 2;
        } else if (codepoint < 0x00_01_00_00){
            // This is a three byte UTF-8 char. Value is 16 bits long (less than 17 bits in value).
            // Get the highest 4 bits into the first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1110_0000 | (0b0000_1111 & (codepoint >> 12))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b00111111 & (codepoint >> 6))));
            buffer[this.tempOffset + 2] = (byte) (0xFF & (0b1000_0000 | (0b00111111 & codepoint)));
            this.tempOffset+=3;
            return 3;
        } else if (codepoint < 0x00_11_00_00) {
            // This is a four byte UTF-8 char. Value is 21 bits long (less than 22 bits in value).
            // Get the highest 3 bits into the first byte
            buffer[this.tempOffset]     = (byte) (0xFF & (0b1111_0000 | (0b0000_0111 & (codepoint >> 18))));
            buffer[this.tempOffset + 1] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & (codepoint >> 12))));
            buffer[this.tempOffset + 2] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & (codepoint >> 6))));
            buffer[this.tempOffset + 3] = (byte) (0xFF & (0b1000_0000 | (0b0011_1111 & codepoint)));
            this.tempOffset+=4;
            return 4;
        }
        throw new IllegalArgumentException("Unknown Unicode codepoint: " + codepoint);
    }

    public int nextCodepointAscii(){
        return buffer[tempOffset++];
    }

    public int nextNonWhiteSpaceCodePointAscii() {
        while(hasMoreBytes()) {
            if(this.buffer[tempOffset] > 32) {   // all ASCII characters from 32 and down are considered white space.
                return this.buffer[tempOffset];
            }
            tempOffset++;
        }
        return 0;
    }

    public void skipWhiteSpace() {
        while(hasMoreBytes()){
            if(this.buffer[tempOffset] > 32) {   // all ASCII characters from 32 and down are considered white space.
                return;
            }
            tempOffset++;
        }
    }

    public int nextCodepointUtf8() {
        int firstByteOfChar = 0xFF & buffer[tempOffset];

        if(firstByteOfChar < 128) {
            //this is a single byte UTF-8 char (an ASCII char)
            tempOffset++;
            return firstByteOfChar;
        } else if(firstByteOfChar < 224) {
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0001_1111 & firstByteOfChar; //0x1F
            nextCodepoint <<= 6;
            nextCodepoint |= 0b0011_1111 & (0xFF & buffer[tempOffset + 1]); //0x3F
            tempOffset +=2;
            return  nextCodepoint;
        } else if(firstByteOfChar < 240) {
            //this is a three byte UTF-8 char
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0000_1111 & firstByteOfChar; // 0x0F
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 1];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 2];
            tempOffset +=3;
            return  nextCodepoint;
        } else if(firstByteOfChar < 248) {
            //this is a four byte UTF-8 char
            int nextCodepoint = 0;
            //this is a two byte UTF-8 char
            nextCodepoint = 0b0000_0111 & firstByteOfChar; // 0x07
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 1];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 2];
            nextCodepoint <<= 6;
            nextCodepoint |= 0x3F & buffer[tempOffset + 3];
            tempOffset +=4;
            return  nextCodepoint;
        }

        throw new IllegalStateException("Codepoint not recognized from first byte: " + firstByteOfChar);
    }




}
