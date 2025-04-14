package com.plmph.pdl;

import com.plmph.hex.HexUtil;
import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.PdeReader;

// Contains all the BOOLEAN_NULL etc. field type constants.
import static com.plmph.pde.PdeFieldTypes.*;

public class Converter {

    private static final byte[] decimalDigits = new byte[] {'0', '1', '2', '3', '4','5', '6', '7', '8', '9' };

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
                    HexUtil.convert(pdeSource, pdeReader.offset + 1 + pdeReader.fieldLengthLength, pdeReader.fieldValueLength, pdlDest, destOffset);
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
     * Converts from PDL to PDE
     */
    public void pdlToPde(){

    }


}
