package com.plmph.pdl;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdlTokenizerTest {

    private static String pdlString
            = """
              #single-token comment;
              *multi-token  comment;~
              !0;
              123;
              -789;
              %123.45;
              /12345.6789;
              $23EF 45A2;
              |QmFzZTY0IGRhdGE=;
              ^UTF-8 binary;       
              'ASCII token;             
              "UTF-8 token;
              @2030-12-31T23:59:59.999;
              :obj1;
              =obj1;
              &obj1;
              .key1;
              {
              }
              [
              ]
              <
              >
              namedToken( )              
              """;
    private static byte[] pdlBytes = pdlString.getBytes(StandardCharsets.UTF_8);


    @Test
    public void testTokenize() {

        long[] tokenOffsets = new long[1024];

        int tokenCount = PdlTokenizer.tokenize(pdlBytes, 0, pdlBytes.length, tokenOffsets);

        assertEquals(25, tokenCount);
        assertOffsets( 0,  22, tokenOffsets[0]);
        assertOffsets(23,  46, tokenOffsets[1]);
        assertOffsets(47,  50, tokenOffsets[2]);
        assertOffsets(51,  55, tokenOffsets[3]);
        assertOffsets(56,  61, tokenOffsets[4]);
        assertOffsets(62,  70, tokenOffsets[5]);
        assertOffsets(71,  83, tokenOffsets[6]);
        assertOffsets(84,  95, tokenOffsets[7]);
        assertOffsets(96,  114, tokenOffsets[8]);
        assertOffsets(115, 129, tokenOffsets[9]);
        assertOffsets(130, 143, tokenOffsets[10]);
        assertOffsets(144, 157, tokenOffsets[11]);
        assertOffsets(158, 183, tokenOffsets[12]);

        assertOffsets(184, 190, tokenOffsets[13]);
        assertOffsets(191, 197, tokenOffsets[14]);
        assertOffsets(198, 204, tokenOffsets[15]);
        assertOffsets(205, 211, tokenOffsets[16]);

        assertOffsets(212, 213, tokenOffsets[17]);
        assertOffsets(214, 215, tokenOffsets[18]);
        assertOffsets(216, 217, tokenOffsets[19]);
        assertOffsets(218, 219, tokenOffsets[20]);
        assertOffsets(220, 221, tokenOffsets[21]);
        assertOffsets(222, 223, tokenOffsets[22]);

        assertOffsets(224, 235, tokenOffsets[23]);
        assertOffsets(236, 237, tokenOffsets[24]);


    }

    private void assertOffsets(int tokenStartOffset, int tokenEndOffset, long tokenOffsetPair){
        assertEquals(tokenStartOffset, extractTokenStartOffset(tokenOffsetPair));
        assertEquals(tokenEndOffset  , extractTokenEndOffset(tokenOffsetPair));
    }

    private static int extractTokenStartOffset(long tokenOffsetPair){
        return (int) ((0xFFFFFFFFL) & tokenOffsetPair);
    }

    private static int extractTokenEndOffset(long tokenOffsetPair){
        return (int) ((0xFFFFFFFFL) & (tokenOffsetPair >> 32));
    }

}
