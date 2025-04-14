package com.plmph.pdl;

import com.plmph.pde.PdeWriter;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConverterTest {


    @Test
    public void test() {
        byte[] pdeSource = new byte[1024];
        PdeWriter pdeWriter = new PdeWriter().setCompositeFieldStack(new int[16]);
        pdeWriter.setDest(pdeSource);

        pdeWriter.writeBooleanObj(null);
        pdeWriter.writeBoolean(false);
        pdeWriter.writeBoolean(true);

        pdeWriter.writeIntObj((Long)null);
        pdeWriter.writeInt(123);
        pdeWriter.writeInt(1234);
        pdeWriter.writeInt(123456);
        pdeWriter.writeInt(-123);
        pdeWriter.writeInt(-4567);

        pdeWriter.writeFloat32Obj(null);
        pdeWriter.writeFloat64Obj(null);
        pdeWriter.writeFloat32(123.45f);
        pdeWriter.writeFloat32Obj(123.45f);
        pdeWriter.writeFloat64(12345.456789d);
        pdeWriter.writeFloat64Obj(12345.456789d);

        //bytes
        pdeWriter.writeBytes(null);
        pdeWriter.writeBytes(new byte[]{0,1,2,3,-128});
        pdeWriter.writeBytes(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,-128});

        //ascii
        pdeWriter.writeAscii(null);
        pdeWriter.writeAscii("These are ASCII characters".getBytes(StandardCharsets.US_ASCII));

        //utf-8
        pdeWriter.writeUtf8(null);
        pdeWriter.writeUtf8("These are UTF-8 characters æøå".getBytes(StandardCharsets.UTF_8));

        //utc
        pdeWriter.writeUtc(2099, 12, 31, 23, 59, 59, 999);

        //objects with keys
        pdeWriter.writeObjectBeginPush(2);
        pdeWriter.writeKeyAscii("f1");
        pdeWriter.writeAsciiString("Hi");
        pdeWriter.writeKeyAscii("f2");
        pdeWriter.writeAsciiString("Hey");
        pdeWriter.writeObjectEndPop();

        //objects without keys
        pdeWriter.writeObjectBeginPush(2);
        pdeWriter.writeObjectEndPop();

        pdeWriter.writeObjectNull();


        //todo null tables?
        pdeWriter.writeTableNull();

        //tables with columns
        pdeWriter.writeTableBeginPush(2);
        pdeWriter.writeKeyAscii("col1");
        pdeWriter.writeKeyAscii("col2");
        pdeWriter.writeAsciiString("val1_1");
        pdeWriter.writeAsciiString("val1_2");
        pdeWriter.writeAsciiString("val2_1");
        pdeWriter.writeAsciiString("val2_2");
        pdeWriter.writeTableEndPop(2);

        //tables without columns
        pdeWriter.writeTableBeginPush(2);
        pdeWriter.writeAsciiString("val1");
        pdeWriter.writeAsciiString("val2");
        pdeWriter.writeAsciiString("val3");
        pdeWriter.writeTableEndPop(3);

        //metadata
        pdeWriter.writeMetadataBeginPush(2);
        pdeWriter.writeKeyAscii("f1");
        pdeWriter.writeAsciiString("Hi");
        pdeWriter.writeKeyAscii("f2");
        pdeWriter.writeAsciiString("Hey");
        pdeWriter.writeMetadataEndPop();

        System.out.println("pdeWriter.offset = " + pdeWriter.offset);

        byte[] pdlDest = new byte[1024];
        Converter converter = new Converter();
        int pdlLength = converter.pdeToPdl(pdeSource, 0, pdeWriter.offset, pdlDest, 0);
        System.out.println(new String(pdlDest, 0, pdlLength, StandardCharsets.UTF_8));


        assertEq(pdlDest, 0, "_!;");
        assertEq(pdlDest, 3, "!0;");
        assertEq(pdlDest, 6, "!1;");

        assertEq(pdlDest, 9, "_+;");
        assertEq(pdlDest, 12, "123;");
        assertEq(pdlDest, 16, "1234;");
        assertEq(pdlDest, 21, "123456;");
        assertEq(pdlDest, 28, "-123;");
        assertEq(pdlDest, 33, "-4567;");

        assertEq(pdlDest, 39, "_/;");
        assertEq(pdlDest, 42, "_/;");
        assertEq(pdlDest, 45, "%123.45;");
        assertEq(pdlDest, 53, "%123.45;");

        assertEq(pdlDest, 61, "/12345.456789;");
        assertEq(pdlDest, 75, "/12345.456789;");

        assertEq(pdlDest, 89, "_$;");
        assertEq(pdlDest, 92, "$0001020380;");
        assertEq(pdlDest, 104,"$000102030405060708090A0B0C0D0E0F1080;");

        assertEq(pdlDest, 142, "_';");
        assertEq(pdlDest, 145, "'These are ASCII characters;");

        assertEq(pdlDest, 173, "_\";");
        assertEq(pdlDest, 176, "\"These are UTF-8 characters ");
        assertEq(pdlDest, 204, "æøå;");

        assertEq(pdlDest, 211, "@2099-12-31T23:59:59.999");

        assertEq(pdlDest, 236, '{');
        assertEq(pdlDest, 237, ".f1;");
        assertEq(pdlDest, 241, "'Hi;");
        assertEq(pdlDest, 245, ".f2;");
        assertEq(pdlDest, 249, "'Hey;");
        assertEq(pdlDest, 254, '}');

        assertEq(pdlDest, 255, "{}");
        assertEq(pdlDest, 257, "_{;");

        assertEq(pdlDest, 260, "_[;");

        assertEq(pdlDest, 263, '[');
        assertEq(pdlDest, 264, ".col1;");
        assertEq(pdlDest, 270, ".col2;");
        assertEq(pdlDest, 276, "'val1_1;");
        assertEq(pdlDest, 284, "'val1_2;");
        assertEq(pdlDest, 292, "'val2_1;");
        assertEq(pdlDest, 300, "'val2_2;");
        assertEq(pdlDest, 308, ']');

        assertEq(pdlDest, 309, '[');
        assertEq(pdlDest, 310, "'val1;");
        assertEq(pdlDest, 316, "'val2;");

        assertEq(pdlDest, 322, "'val3;");
        assertEq(pdlDest, 328, ']');

        assertEq(pdlDest, 329, '<');
        assertEq(pdlDest, 330, ".f1;");
        assertEq(pdlDest, 334, "'Hi;");
        assertEq(pdlDest, 338, ".f2;");
        assertEq(pdlDest, 342, "'Hey;");
        assertEq(pdlDest, 347, '>');

        // Just a debug breakpoint handle ... will be removed when the test is complete.
        System.out.println("Converter test finished");

    }

    public void assertEq(byte[] pdlDest, int offset, String characters){
        assertEq(pdlDest, offset, characters.getBytes(StandardCharsets.UTF_8));
    }

    public void assertEq(byte[] pdlDest, int offset, char ... chars){
        for(int i=0, n = chars.length; i<n; i++){
            assertEquals(chars[i], (char) pdlDest[offset + i]);
        }
    }
    public void assertEq(byte[] pdlDest, int offset, byte[] utf8Chars){
        for(int i=0, n = utf8Chars.length; i<n; i++){
            assertEquals(utf8Chars[i], pdlDest[offset + i]);
        }
    }
}
