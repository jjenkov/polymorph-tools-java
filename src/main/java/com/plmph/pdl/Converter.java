package com.plmph.pdl;

import com.plmph.hex.HexUtil;
import com.plmph.pde.PdeReader;
import com.plmph.pde.PdeUtil;
import com.plmph.pde.PdeWriter;

// Contains all the BOOLEAN_NULL etc. field type constants.
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static com.plmph.pde.PdeFieldTypes.*;

public class Converter {

    private static final byte[] decimalDigits = new byte[] {'0', '1', '2', '3', '4','5', '6', '7', '8', '9' };


    private int[] compositeFieldNestedFieldCountStack = new int[32];
    private int[] compositeFieldStartIndexStack       = new int[32];
    private int   compositeFieldStackIndex = 0;


    /**
     * Converts from PDE to PDL
     */
    public int pdeToPdl(byte[] pdeSource, int sourceOffset, int sourceLength, byte[] pdlDest, int destOffset){
        PdeReader pdeReader = new PdeReader();
        pdeReader.setSource(pdeSource, sourceOffset, sourceLength);
        return pdeToPdl(pdeReader, pdlDest, destOffset);
    }
    public int pdeToPdl(PdeReader pdeReader, byte[] pdlDest, int destOffset){
        byte[] pdeSource = pdeReader.getSource();
        int destStartOffset = destOffset;

        while(pdeReader.hasNext()){
            pdeReader.next();

            switch(pdeReader.fieldType) {
                case BOOLEAN_NULL    -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '!'; pdlDest[destOffset++] = ';'; }
                case BOOLEAN_TRUE    -> { pdlDest[destOffset++] = '!'; pdlDest[destOffset++] = '1'; pdlDest[destOffset++] = ';'; }
                case BOOLEAN_FALSE   -> { pdlDest[destOffset++] = '!'; pdlDest[destOffset++] = '0'; pdlDest[destOffset++] = ';'; }
                case INT_NULL        -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '+'; pdlDest[destOffset++] = ';'; }
                case INT_POS_1_BYTES, INT_POS_2_BYTES, INT_POS_3_BYTES, INT_POS_4_BYTES, INT_POS_5_BYTES, INT_POS_6_BYTES, INT_POS_7_BYTES, INT_POS_8_BYTES -> {
                    long intValue = pdeReader.readInt();
                    destOffset += integerToCharacters(intValue, pdlDest, destOffset);
                    pdlDest[destOffset++] = ';' ;
                }
                case INT_NEG_1_BYTES, INT_NEG_2_BYTES, INT_NEG_3_BYTES, INT_NEG_4_BYTES, INT_NEG_5_BYTES, INT_NEG_6_BYTES, INT_NEG_7_BYTES, INT_NEG_8_BYTES -> {
                    long intValue = pdeReader.readInt();
                    destOffset += integerToCharacters(intValue, pdlDest, destOffset);
                    pdlDest[destOffset++] = ';' ;
                }
                case FLOAT_NULL    -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '/'; pdlDest[destOffset++] = ';'; }
                case FLOAT_4_BYTES -> {
                    float floatValue = pdeReader.readFloat32();
                    String floatStr  = String.valueOf(floatValue);
                    pdlDest[destOffset++] = '%';
                    for(int i=0; i<floatStr.length(); i++){
                        pdlDest[destOffset++] = (byte) ((0xFF) & floatStr.charAt(i));
                    }
                    pdlDest[destOffset++] = ';' ;
                }
                case FLOAT_8_BYTES -> {
                    double doubleValue = pdeReader.readFloat64();
                    String doubleStr   = String.valueOf(doubleValue);
                    pdlDest[destOffset++] = '/';
                    for(int i=0; i<doubleStr.length(); i++){
                        pdlDest[destOffset++] = (byte) ((0xFF) & doubleStr.charAt(i));
                    }
                    pdlDest[destOffset++] = ';' ;
                }
                case BYTES_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '$'; pdlDest[destOffset++] = ';'; }
                case BYTES_0_BYTES -> { pdlDest[destOffset++] = '$'; pdlDest[destOffset++] = ';'; }
                case BYTES_1_BYTES, BYTES_2_BYTES, BYTES_3_BYTES, BYTES_4_BYTES, BYTES_5_BYTES, BYTES_6_BYTES, BYTES_7_BYTES, BYTES_8_BYTES,
                     BYTES_9_BYTES, BYTES_10_BYTES, BYTES_11_BYTES, BYTES_12_BYTES, BYTES_13_BYTES, BYTES_14_BYTES, BYTES_15_BYTES,
                     BYTES_1_LENGTH_BYTES, BYTES_2_LENGTH_BYTES, BYTES_3_LENGTH_BYTES, BYTES_4_LENGTH_BYTES,
                     BYTES_5_LENGTH_BYTES, BYTES_6_LENGTH_BYTES, BYTES_7_LENGTH_BYTES, BYTES_8_LENGTH_BYTES -> {
                    pdlDest[destOffset++] = '$';
                    HexUtil.bytesToHex(pdeSource, pdeReader.offset + 1 + pdeReader.fieldLengthLength, pdeReader.fieldValueLength, pdlDest, destOffset);
                    destOffset += pdeReader.fieldValueLength*2;
                    pdlDest[destOffset++] = ';';
                }
                case ASCII_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '\''; pdlDest[destOffset++] = ';'; }
                case ASCII_1_BYTES, ASCII_2_BYTES, ASCII_3_BYTES, ASCII_4_BYTES, ASCII_5_BYTES, ASCII_6_BYTES, ASCII_7_BYTES, ASCII_8_BYTES,
                     ASCII_9_BYTES, ASCII_10_BYTES, ASCII_11_BYTES, ASCII_12_BYTES, ASCII_13_BYTES, ASCII_14_BYTES, ASCII_15_BYTES,
                     ASCII_1_LENGTH_BYTES, ASCII_2_LENGTH_BYTES, ASCII_3_LENGTH_BYTES, ASCII_4_LENGTH_BYTES,
                     ASCII_5_LENGTH_BYTES, ASCII_6_LENGTH_BYTES, ASCII_7_LENGTH_BYTES, ASCII_8_LENGTH_BYTES   -> {
                    pdlDest[destOffset++] = '\'';
                    System.arraycopy(pdeSource, pdeReader.offset + 1 + pdeReader.fieldLengthLength, pdlDest, destOffset, pdeReader.fieldValueLength);
                    destOffset += pdeReader.fieldValueLength;
                    pdlDest[destOffset++] = ';';
                }
                case UTF_8_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '"'; pdlDest[destOffset++] = ';'; }
                case UTF_8_1_BYTES, UTF_8_2_BYTES, UTF_8_3_BYTES, UTF_8_4_BYTES, UTF_8_5_BYTES, UTF_8_6_BYTES, UTF_8_7_BYTES, UTF_8_8_BYTES,
                     UTF_8_9_BYTES, UTF_8_10_BYTES, UTF_8_11_BYTES, UTF_8_12_BYTES, UTF_8_13_BYTES, UTF_8_14_BYTES, UTF_8_15_BYTES,
                     UTF_8_1_LENGTH_BYTES, UTF_8_2_LENGTH_BYTES, UTF_8_3_LENGTH_BYTES, UTF_8_4_LENGTH_BYTES,
                     UTF_8_5_LENGTH_BYTES, UTF_8_6_LENGTH_BYTES, UTF_8_7_LENGTH_BYTES, UTF_8_8_LENGTH_BYTES   -> {
                    pdlDest[destOffset++] = '"';
                    System.arraycopy(pdeSource, pdeReader.offset + 1 + pdeReader.fieldLengthLength, pdlDest, destOffset, pdeReader.fieldValueLength);
                    destOffset += pdeReader.fieldValueLength;
                    pdlDest[destOffset++] = ';';
                }
                //todo add support for all UTC length variations (2 to 9/10 bytes)
                case UTC_9_BYTES -> {
                    pdlDest[destOffset++] = '@';
                    int valueBaseOffset = pdeReader.offset + 1;
                    long val1 = (0xFF) & pdeSource[valueBaseOffset];
                    long val2 = (0xFF) & pdeSource[valueBaseOffset + 1];
                    long val3 = val1 | (val2 << 8);
                    integerToCharacters(val3, pdlDest, destOffset);
                    destOffset += 4;
                    pdlDest[destOffset++] = '-';

                    val1 = (0xFF) & pdeSource[valueBaseOffset + 2];
                    integerToCharacters(val1, pdlDest, destOffset);
                    destOffset += 2;
                    pdlDest[destOffset++] = '-';
                    val1 = (0xFF) & pdeSource[valueBaseOffset + 3];
                    integerToCharacters(val1, pdlDest, destOffset);
                    destOffset += 2;
                    pdlDest[destOffset++] = 'T';
                    val1 = (0xFF) & pdeSource[valueBaseOffset + 4];
                    integerToCharacters(val1, pdlDest, destOffset);
                    destOffset += 2;
                    pdlDest[destOffset++] = ':';
                    val1 = (0xFF) & pdeSource[valueBaseOffset + 5];
                    integerToCharacters(val1, pdlDest, destOffset);
                    destOffset += 2;
                    pdlDest[destOffset++] = ':';
                    val1 = (0xFF) & pdeSource[valueBaseOffset + 6];
                    integerToCharacters(val1, pdlDest, destOffset);
                    destOffset += 2;

                    pdlDest[destOffset++] = '.';
                    val1 = (0xFF) & pdeSource[valueBaseOffset + 7];
                    val2 = (0xFF) & pdeSource[valueBaseOffset + 8];
                    val3 = val1 | (val2 << 8);
                    integerToCharacters(val3, pdlDest, destOffset);
                    destOffset += 3;

                    pdlDest[destOffset++] = ';';
                }
                case KEY_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '.'; pdlDest[destOffset++] = ';'; }
                case KEY_0_BYTES -> { pdlDest[destOffset++] = '.'; pdlDest[destOffset++] = ';'; }
                case KEY_1_BYTES, KEY_2_BYTES, KEY_3_BYTES, KEY_4_BYTES, KEY_5_BYTES, KEY_6_BYTES, KEY_7_BYTES,
                     KEY_8_BYTES,KEY_9_BYTES,KEY_10_BYTES,KEY_11_BYTES,KEY_12_BYTES,KEY_13_BYTES,KEY_14_BYTES,KEY_15_BYTES,
                     KEY_1_LENGTH_BYTES, KEY_2_LENGTH_BYTES -> {
                    pdlDest[destOffset++] = '.';
                    System.arraycopy(pdeSource, pdeReader.offset + 1 + pdeReader.fieldLengthLength, pdlDest, destOffset, pdeReader.fieldValueLength);
                    destOffset += pdeReader.fieldValueLength;
                    pdlDest[destOffset++] = ';';
                }
                case OBJECT_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '{'; pdlDest[destOffset++] = ';'; }
                case OBJECT_1_LENGTH_BYTES, OBJECT_2_LENGTH_BYTES, OBJECT_3_LENGTH_BYTES, OBJECT_4_LENGTH_BYTES,
                     OBJECT_5_LENGTH_BYTES, OBJECT_6_LENGTH_BYTES, OBJECT_7_LENGTH_BYTES, OBJECT_8_LENGTH_BYTES -> {
                    pdlDest[destOffset++] = '{';
                    pdeReader.moveInto();

                    destOffset += pdeToPdl(pdeReader, pdlDest, destOffset);

                    pdeReader.moveOutOf();
                    pdlDest[destOffset++] = '}';
                }
                case TABLE_NULL -> { pdlDest[destOffset++] = '_'; pdlDest[destOffset++] = '['; pdlDest[destOffset++] = ';'; }
                case TABLE_1_LENGTH_BYTES, TABLE_2_LENGTH_BYTES, TABLE_3_LENGTH_BYTES, TABLE_4_LENGTH_BYTES,
                        TABLE_5_LENGTH_BYTES, TABLE_6_LENGTH_BYTES, TABLE_7_LENGTH_BYTES, TABLE_8_LENGTH_BYTES -> {
                    pdlDest[destOffset++] = '[';
                    pdeReader.moveInto();

                    //skip over PDE int field with row count.
                    pdeReader.next();

                    destOffset += pdeToPdl(pdeReader, pdlDest, destOffset);

                    pdeReader.moveOutOf();
                    pdlDest[destOffset++] = ']';
                }
                case METADATA_1_LENGTH_BYTES, METADATA_2_LENGTH_BYTES, METADATA_3_LENGTH_BYTES, METADATA_4_LENGTH_BYTES,
                        METADATA_5_LENGTH_BYTES, METADATA_6_LENGTH_BYTES, METADATA_7_LENGTH_BYTES, METADATA_8_LENGTH_BYTES -> {
                    pdlDest[destOffset++] = '<';
                    pdeReader.moveInto();

                    destOffset += pdeToPdl(pdeReader, pdlDest, destOffset);

                    pdeReader.moveOutOf();
                    pdlDest[destOffset++] = '>';
                }


                default -> {}


            }
        }


        return destOffset - destStartOffset;
    }

    private int integerToCharacters(long intValue, byte[] pdlDest, int destOffset){
        long tempValue = intValue;

        byte[] chars = new byte[64]; // no integers of more than 64 characters.
        int charCount = 0;

        if(intValue < 0){
            pdlDest[destOffset++] = '-';
            tempValue = -tempValue;
            charCount = 1;
        }

        while(tempValue != 0){
            int digit = (int) tempValue % 10;

            byte digitChar = decimalDigits[digit];
            chars[charCount] = digitChar;

            //System.out.println(digitChar);
            tempValue = tempValue / 10;
            charCount++;
        }

        //copy chars to pdlDest in reverse order.
        for(int i=charCount-1; i >= 0; i--){
            pdlDest[destOffset++] = chars[i];
        }

        return charCount;
    }




    /**
     * Converts from PDL to PDE.
     * @param pdlSource The byte array containing the PDL source bytes to convert to PDE.
     * @param tokenOffsets The long array containing token offset pairs that results from a PDL tokenization of the PDL in the pdlSource byte array.
     * @param tokenCount The number of tokens (elements) in the tokenOffsets long array.
     * @param pdeDest The destination byte array to write the resulting PDE bytes into.
     * @param destOffset The offset in the destination byte array to start writing PDE bytes.
     *
     * @Return How many bytes were written into the pdeDest byte array.
     * @return The number of bytes written into the pdeDest byte array.
     */
    public int pdlToPde(byte[] pdlSource, long[] tokenOffsets, int tokenCount, byte[] pdeDest, int destOffset){
        PdeWriter pdeWriter = new PdeWriter();
        pdeWriter.setCompositeFieldStack(new int[32]);
        pdeWriter.setDest(pdeDest, destOffset);
        return pdlToPde(pdlSource, tokenOffsets, tokenCount, pdeWriter);
    }

    /**
     * Converts from PDL to PDE.
     * @param pdlSource The byte array containing the PDL source bytes to convert to PDE.
     * @param tokenOffsets The long array containing token offset pairs that results from a PDL tokenization of the PDL in the pdlSource byte array.
     * @param tokenCount The number of tokens (elements) in the tokenOffsets long array.
     * @param pdeWriter The PdeWriter to use to write PDE bytes (from the PDL token offsets).
     *
     * @return The number of bytes written into the pdeDest byte array.
     */
    public int pdlToPde(byte[] pdlSource, long[] tokenOffsets, int tokenCount, PdeWriter pdeWriter){
        long lastTokenOffsetPair = tokenOffsets[tokenCount-1];
        int  lastTokenStartOffset = (int) ((0xFFFFFFFF) & lastTokenOffsetPair);
        int  lastTokenEndOffset   = (int) ((0xFFFFFFFF) & (lastTokenOffsetPair >> 32));

        int lengthLength = PdeUtil.byteCountForLength(lastTokenEndOffset); // todo Calculate length of pdlSource bytes as lastTokenEndOffset - firstTokenStartOffset
        int destOffsetStart = pdeWriter.offset;

        for(int i=0; i<tokenCount; i++){
            long tokenOffsetPair = tokenOffsets[i];
            int  tokenStartOffset = (int) ((0xFFFFFFFF) & tokenOffsetPair);
            int  tokenEndOffset   = (int) ((0xFFFFFFFF) & (tokenOffsetPair >> 32));

            int  tokenType = pdlSource[tokenStartOffset];

            switch(tokenType){
                case '#' : { // single-token comment
                    //do nothing - comments are not embedded into PDE. They only exists in PDL.
                    //todo comment out the following print() statement.
                    System.out.println("Single-token comment"); break;
                }
                case '*' : { // multi-token comment
                    //do nothing - comments are not embedded into PDE. They only exists in PDL.
                    //todo comment out the following print() statement.
                    System.out.println("Multi-token comment"); break;
                }
                case '!' : {  // null token
                    boolean value = pdlSource[tokenStartOffset + 1] == '1';
                    pdeWriter.writeBoolean(value);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '-', '+' : {  // negative or positive integer
                    tokenStartOffset++; // jump past token type char
                    // no break - fall through to parsing the number following the token type character.
                }
                case '0','1', '2', '3', '4', '5', '6', '7', '8', '9' : {   // positive integer
                    long value = parseLong(pdlSource, tokenStartOffset, tokenEndOffset);
                    if(tokenType == '-'){
                        value = -value;
                    }
                    pdeWriter.writeInt(value);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '%' : {  // 4 byte floating point
                    tokenStartOffset++; // jump past token type character
                    float value = Float.parseFloat(new String(pdlSource, tokenStartOffset, tokenEndOffset - tokenStartOffset - 1));
                    pdeWriter.writeFloat32(value);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '/' : {  // 8 byte floating point
                    tokenStartOffset++; // jump past token type character
                    double value = Double.parseDouble(new String(pdlSource, tokenStartOffset, tokenEndOffset - tokenStartOffset - 1));
                    pdeWriter.writeFloat64(value);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '$' : {  // bytes in hexadecimal encoding
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeBytesBeginPush(lengthLength);
                    pdeWriter.offset += HexUtil.hexToBytes(pdlSource, tokenStartOffset, tokenEndOffset - tokenStartOffset - 1, pdeWriter.dest, pdeWriter.offset);
                    pdeWriter.writeBytesEndPop();
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                //todo insert case for base64
                case '^' : {  // bytes in UTF-8 encoding
                    tokenStartOffset++; // jump past token type character
                    int tokenLength = tokenEndOffset - tokenStartOffset;
                    pdeWriter.writeBytes(pdlSource, tokenStartOffset, tokenEndOffset-tokenStartOffset-1);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }

                case '\'' : {  // ASCII text
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeAscii(pdlSource, tokenStartOffset, tokenEndOffset-tokenStartOffset-1);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '"' : {  // UTF-8 text
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeUtf8(pdlSource, tokenStartOffset, tokenEndOffset-tokenStartOffset-1);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }
                case '@' : {   // UTC date-time

                    // todo this GregorianCalendar should be reusable across all UTC date time fields. No need to instantiate a new per field.
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                    tokenStartOffset++; // jump past token type character
                    int tokenValueLength = tokenEndOffset - tokenStartOffset -1; // - 1 to not cound the toke end char

                    if(tokenValueLength >= 4){ // there is a year in the date time
                        long year = parseLong(pdlSource, tokenStartOffset, tokenStartOffset+4);
                        calendar.set(GregorianCalendar.YEAR, (int) year);
                        System.out.println("year = " + year);
                    }
                    if(tokenValueLength >=7) { // there is a month in the date time
                        long month = parseLong(pdlSource, tokenStartOffset+5, tokenStartOffset+7);
                        calendar.set(GregorianCalendar.MONTH, (int) (month-1));  // -1 because Calendar uses months from 0 to 11
                        System.out.println("month = " + month);
                    }
                    if(tokenValueLength >=10) { // there is a day in the date time
                        long day = parseLong(pdlSource, tokenStartOffset+8, tokenStartOffset+10);
                        calendar.set(GregorianCalendar.DAY_OF_MONTH, (int) day);
                        System.out.println("day = " + day);
                    }
                    if(tokenValueLength >=13) { // there is an hour in the date time
                        long hour = parseLong(pdlSource, tokenStartOffset+11, tokenStartOffset+13);
                        calendar.set(GregorianCalendar.HOUR_OF_DAY, (int) hour);
                        System.out.println("hour = " + hour);
                    }
                    if(tokenValueLength >=16) { // there is a minute in the date time
                        long minutes = parseLong(pdlSource, tokenStartOffset+14, tokenStartOffset+16);
                        calendar.set(GregorianCalendar.MINUTE, (int) minutes);
                        System.out.println("minutes = " + minutes);
                    }
                    if(tokenValueLength >=19) { // there is a second in the date time
                        long seconds = parseLong(pdlSource, tokenStartOffset+17, tokenStartOffset+19);
                        calendar.set(GregorianCalendar.SECOND, (int) seconds);
                        System.out.println("seconds = " + seconds);
                    }
                    if(tokenValueLength >=23) { // there is a millisecond in the date time
                        long millis = parseLong(pdlSource, tokenStartOffset+20, tokenStartOffset+23);
                        calendar.set(GregorianCalendar.MILLISECOND, (int) millis);
                        System.out.println("millis = " + millis);
                    }

                    pdeWriter.writeUtcMillis(calendar.getTimeInMillis());

                    break;
                }

                case '.' : {  // Key field
                    tokenStartOffset++; // jump past token type character.
                    pdeWriter.writeKey(pdlSource, tokenStartOffset, tokenEndOffset-tokenStartOffset-1);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    break;
                }


                //todo insert case for object, table, key, metadata, copy, reference, id (?)
                case '{' : {  // object begin
                    //todo is it necessary to increment tokenStartOffset ? Do we ever use the incremented value?
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeObjectBeginPush(lengthLength);
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope

                    // push a new composite field counter and table column counter on their stacks.
                    this.compositeFieldStackIndex++;
                    this.compositeFieldNestedFieldCountStack[this.compositeFieldStackIndex] = 0;
                    this.compositeFieldStartIndexStack[this.compositeFieldStackIndex] = i;

                    break;
                }
                case '}' : {  // object end
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeObjectEndPop();
                    this.compositeFieldStackIndex--;
                    break;
                }
                case '[' : {  // table begin
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    // push a new composite field counter and table column counter on their stacks.
                    this.compositeFieldStackIndex++;
                    this.compositeFieldNestedFieldCountStack[this.compositeFieldStackIndex] = 0;
                    this.compositeFieldStartIndexStack[this.compositeFieldStackIndex] = i;
                    tokenStartOffset++;
                    pdeWriter.writeTableBeginPush(lengthLength);
                    break;
                }
                case ']' : {  // table end
                    int tableTokenStartOffset = this.compositeFieldStartIndexStack[this.compositeFieldStackIndex];
                    System.out.println("table token start: " + tableTokenStartOffset);

                    tableTokenStartOffset++; // move past first [ token
                    //now count the key fields ( .xyz; )

                    long localTokenOffsetPair = tokenOffsets[tableTokenStartOffset];
                    int  localTokenStartOffset = (int) ((0xFFFFFFFF) & localTokenOffsetPair);
                    int  localTokenType = pdlSource[localTokenStartOffset];

                    System.out.println("first local token type char: " + (char) localTokenType);

                    int keyFieldCount = 0;
                    while(localTokenType == '.'){
                        keyFieldCount++;
                        tableTokenStartOffset++;

                        localTokenOffsetPair = tokenOffsets[tableTokenStartOffset];
                        localTokenStartOffset = (int) ((0xFFFFFFFF) & localTokenOffsetPair);
                        localTokenType = pdlSource[localTokenStartOffset];
                    }
                    System.out.println("Table key field count: " + keyFieldCount);

                    int totalTableFieldCount =
                            this.compositeFieldNestedFieldCountStack[this.compositeFieldStackIndex] - keyFieldCount;

                    System.out.println("Total table field count: " + totalTableFieldCount);

                    if(keyFieldCount == 0){
                        keyFieldCount = 1; // 0 keys means table is just an array => each element is 1 row.
                    }
                    int totalTableRowCount = totalTableFieldCount / keyFieldCount;

                    System.out.println("Total table row count: " + totalTableRowCount);

                    pdeWriter.writeTableEndPop(totalTableRowCount);

                    this.compositeFieldStackIndex--;
                    break;
                }
                case '<' : { // metadata begin
                    compositeFieldNestedFieldCountStack[compositeFieldStackIndex]++; // increment number of fields in this composite field scope
                    // push a new composite field counter and table column counter on their stacks.
                    this.compositeFieldStackIndex++;
                    this.compositeFieldNestedFieldCountStack[this.compositeFieldStackIndex] = 0;
                    this.compositeFieldStartIndexStack[this.compositeFieldStackIndex] = i;

                    //todo is it necessary to increment tokenStartOffset ? Do we ever use the incremented value?
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeMetadataBeginPush(lengthLength);
                    break;
                }
                case '>' : { // metadata end
                    tokenStartOffset++; // jump past token type character
                    pdeWriter.writeMetadataEndPop();

                    this.compositeFieldStackIndex--;
                    break;
                }

                default : {}
            }
        }

        return pdeWriter.offset - destOffsetStart;
    }

    private static long parseLong(byte[] pdlSource, int tokenStartOffset, int tokenEndOffset) {
        long value = 0;
        while(tokenStartOffset < tokenEndOffset){
            int nextChar = pdlSource[tokenStartOffset++];
            switch(nextChar) {
                case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : {
                    value = value * 10;
                    value += nextChar - 48;
                }
                default : {
                    //do nothing - ignore that character - or maybe throw an error saying it is not a valid number ?
                }
            }
        }
        return value;
    }


}
