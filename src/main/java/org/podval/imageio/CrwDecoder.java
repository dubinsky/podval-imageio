/* $Id$ */

package org.podval.imageio;

/**
 *
 */

public abstract class CrwDecoder {

  public abstract boolean hasNext();


  public abstract CrwDecoder getNext(boolean isOne);


  public abstract int getValue();



  /**
   *
   */
  private static final class Branch extends CrwDecoder {

    public Branch(CrwDecoder zero, CrwDecoder one) {
      this.zero = zero;
      this.one = one;
    }


    public boolean hasNext() {
      return true;
    }


    public CrwDecoder getNext(boolean isOne) {
      return (isOne) ? one : zero;
    }


    public int getValue() {
      throw new UnsupportedOperationException("No values on the branches.");
    }


    private final CrwDecoder zero;


    private final CrwDecoder one;
  }



  /**
   *
   */
  private static final class Leaf extends CrwDecoder {

    public Leaf(int value) {
      this.value = value;
    }


    public boolean hasNext() {
      return false;
    }


    public CrwDecoder getNext(boolean isOne) {
      throw new UnsupportedOperationException("No branches on the leaves.");
    }


    public int getValue() {
      return value;
    }


    private final int value;
  }



  /**
   *
   */
  private static final class CodeIterator {

    public CodeIterator(int[][] source) {
      this.source = source;
    }


    // Codes of length 0 are not possible.
    public int nextValue(int level) {
      int result = -1;

      if ((1 <= level) && (level <= source.length)) {
        if (source[level-1].length > gaveValues[level]) {
          result = source[level-1][gaveValues[level]];
          gaveValues[level]++;
        } else
        if (level == source.length)
          result = 0xff;
      }

      return result;
    }


    private int[][] source;


    private int[] gaveValues = new int[17];
  }



  private static CrwDecoder makeDecoder(int[][] source) {
    CodeIterator iterator = new CodeIterator(source);
    return makeDecoder(iterator, 0);
  }


  private static CrwDecoder makeDecoder(CodeIterator iterator, int level) {
    CrwDecoder result;
    int value = iterator.nextValue(level);
    if (value != -1) {
      result = new Leaf(value);
    } else {
      result = new Branch(
        makeDecoder(iterator, level+1),
        makeDecoder(iterator, level+1)
      );
    }
    return result;
  }


  public static CrwDecoder getInstance(int tableNumber, boolean first) {
    CrwDecoder result = null;

    if (tableNumber > 2) {
      tableNumber = 2;
    }

    if (tableNumber >= 0) {
      result = decoders[tableNumber][(first) ? 0 : 1];
    }

    return result;
  }


  private static final int[][] FIRST_0 = {
    {},
    {0x04},
    {0x03, 0x05, 0x06, 0x02},
    {0x07, 0x01},
    {0x08, 0x09, 0x00},
    {0x0a},
    {0x0b}
  };


  private static final int[][] SECOND_0 = {
    {},
    {0x03, 0x04},
    {0x02, 0x05},
    {0x01, 0x06},
    {0x07},
    {0x08, 0x12, 0x13, 0x11},
    {0x14, 0x09},
    {0x15},
    {0x22, 0x00},
    {0x21, 0x16, 0x0a, 0xf0, 0x23},
    {0x17},
    {0x24},
    {},
    {},
    {},
    {0x31, 0x32, 0x18, 0x19, 0x33, 0x25, 0x41, 0x34, 0x42, 0x35, 0x51, 0x36,
     0x37, 0x38, 0x29, 0x79, 0x26, 0x1a, 0x39, 0x56, 0x57, 0x28, 0x27, 0x52,
     0x55, 0x58, 0x43, 0x76, 0x59, 0x77, 0x54, 0x61, 0xf9, 0x71, 0x78, 0x75,
     0x96, 0x97, 0x49, 0xb7, 0x53, 0xd7, 0x74, 0xb6, 0x98, 0x47, 0x48, 0x95,
     0x69, 0x99, 0x91, 0xfa, 0xb8, 0x68, 0xb5, 0xb9, 0xd6, 0xf7, 0xd8, 0x67,
     0x46, 0x45, 0x94, 0x89, 0xf8, 0x81, 0xd5, 0xf6, 0xb4, 0x88, 0xb1, 0x2a,
     0x44, 0x72, 0xd9, 0x87, 0x66, 0xd4, 0xf5, 0x3a, 0xa7, 0x73, 0xa9, 0xa8,
     0x86, 0x62, 0xc7, 0x65, 0xc8, 0xc9, 0xa1, 0xf4, 0xd1, 0xe9, 0x5a, 0x92,
     0x85, 0xa6, 0xe7, 0x93, 0xe8, 0xc1, 0xc6, 0x7a, 0x64, 0xe1, 0x4a, 0x6a,
     0xe6, 0xb3, 0xf1, 0xd3, 0xa5, 0x8a, 0xb2, 0x9a, 0xba, 0x84, 0xa4, 0x63,
     0xe5, 0xc5, 0xf3, 0xd2, 0xc4, 0x82, 0xaa, 0xda, 0xe4, 0xf2, 0xca, 0x83,
     0xa3, 0xa2, 0xc3, 0xea, 0xc2, 0xe2, 0xe3}
  };


  private static final int[][] FIRST_1 = {
    {},
    {0x03, 0x02},
    {0x04, 0x01},
    {0x05, 0x00, 0x06},
    {0x07},
    {0x09},
    {0x08},
    {0x0a},
    {0x0b}
  };


  private static final int[][] SECOND_1 = {
    {},
    {0x02, 0x03},
    {0x01, 0x04},
    {0x05},
    {0x12, 0x11, 0x06, 0x13},
    {0x07},
    {0x08, 0x14, 0x22, 0x09},
    {0x21},
    {0x00, 0x23, 0x15},
    {0x31, 0x32, 0x0a},
    {0x16},
    {},
    {},
    {},
    {},
    {0xf0, 0x24, 0x33, 0x41, 0x42, 0x19, 0x17, 0x25, 0x18, 0x51, 0x34, 0x43,
     0x52, 0x29, 0x35, 0x61, 0x39, 0x71, 0x62, 0x36, 0x53, 0x26, 0x38, 0x1a,
     0x37, 0x81, 0x27, 0x91, 0x79, 0x55, 0x45, 0x28, 0x72, 0x59, 0xa1, 0xb1,
     0x44, 0x69, 0x54, 0x58, 0xd1, 0xfa, 0x57, 0xe1, 0xf1, 0xb9, 0x49, 0x47,
     0x63, 0x6a, 0xf9, 0x56, 0x46, 0xa8, 0x2a, 0x4a, 0x78, 0x99, 0x3a, 0x75,
     0x74, 0x86, 0x65, 0xc1, 0x76, 0xb6, 0x96, 0xd6, 0x89, 0x85, 0xc9, 0xf5,
     0x95, 0xb4, 0xc7, 0xf7, 0x8a, 0x97, 0xb8, 0x73, 0xb7, 0xd8, 0xd9, 0x87,
     0xa7, 0x7a, 0x48, 0x82, 0x84, 0xea, 0xf4, 0xa6, 0xc5, 0x5a, 0x94, 0xa4,
     0xc6, 0x92, 0xc3, 0x68, 0xb5, 0xc8, 0xe4, 0xe5, 0xe6, 0xe9, 0xa2, 0xa3,
     0xe3, 0xc2, 0x66, 0x67, 0x93, 0xaa, 0xd4, 0xd5, 0xe7, 0xf8, 0x88, 0x9a,
     0xd7, 0x77, 0xc4, 0x64, 0xe2, 0x98, 0xa5, 0xca, 0xda, 0xe8, 0xf3, 0xf6,
     0xa9, 0xb2, 0xb3, 0xf2, 0xd2, 0x83, 0xba, 0xd3}
  };


  private static final int[][] FIRST_2 = {
    {},
    {},
    {0x06, 0x05, 0x07, 0x04, 0x08, 0x03},
    {0x09, 0x02, 0x00},
    {0x0a},
    {0x01},
    {0x0b}
  };


  private static final int[][] SECOND_2 = {
    {},
    {},
    {0x04, 0x05, 0x03, 0x06, 0x02, 0x07},
    {0x01, 0x08},
    {0x09},
    {0x12, 0x13, 0x14},
    {0x11, 0x15, 0x0a},
    {0x16, 0x17},
    {0xf0, 0x00, 0x22, 0x21, 0x18},
    {0x23},
    {0x19, 0x24},
    {0x32, 0x31},
    {0x25, 0x33, 0x38, 0x37, 0x34, 0x35, 0x36, 0x39},
    {0x79, 0x57, 0x58, 0x59, 0x28, 0x56, 0x78, 0x27, 0x41, 0x29},
    {},
    {0x77, 0x26, 0x42, 0x76, 0x99, 0x1a, 0x55, 0x98, 0x97, 0xf9, 0x48, 0x54,
     0x96, 0x89, 0x47, 0xb7, 0x49, 0xfa, 0x75, 0x68, 0xb6, 0x67, 0x69, 0xb9,
     0xb8, 0xd8, 0x52, 0xd7, 0x88, 0xb5, 0x74, 0x51, 0x46, 0xd9, 0xf8, 0x3a,
     0xd6, 0x87, 0x45, 0x7a, 0x95, 0xd5, 0xf6, 0x86, 0xb4, 0xa9, 0x94, 0x53,
     0x2a, 0xa8, 0x43, 0xf5, 0xf7, 0xd4, 0x66, 0xa7, 0x5a, 0x44, 0x8a, 0xc9,
     0xe8, 0xc8, 0xe7, 0x9a, 0x6a, 0x73, 0x4a, 0x61, 0xc7, 0xf4, 0xc6, 0x65,
     0xe9, 0x72, 0xe6, 0x71, 0x91, 0x93, 0xa6, 0xda, 0x92, 0x85, 0x62, 0xf3,
     0xc5, 0xb2, 0xa4, 0x84, 0xba, 0x64, 0xa5, 0xb3, 0xd2, 0x81, 0xe5, 0xd3,
     0xaa, 0xc4, 0xca, 0xf2, 0xb1, 0xe4, 0xd1, 0x83, 0x63, 0xea, 0xc3, 0xe2,
     0x82, 0xf1, 0xa3, 0xc2, 0xa1, 0xc1, 0xe3, 0xa2, 0xe1}
  };


  private static CrwDecoder[][] decoders = {
    {makeDecoder(FIRST_0), makeDecoder(SECOND_0)},
    {makeDecoder(FIRST_1), makeDecoder(SECOND_1)},
    {makeDecoder(FIRST_2), makeDecoder(SECOND_2)}
  };
}
