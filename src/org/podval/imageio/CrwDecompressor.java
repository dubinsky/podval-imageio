package org.podval.imageio;

import java.io.File;
import java.io.IOException;

import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageOutputStream;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;

import java.awt.Transparency;
import java.awt.image.DataBuffer;
import java.awt.Point;

import java.awt.color.ColorSpace;


/*
      26 3024436 Image
 3024433           Decode table?
 3024462   64598 Thumbnail
 3089060    5252 Small thumbnail
 3094312         ?
 3095358    2048 ?
 3097406    2048 ?
 3099454         ?

  The following information from the metadata is used:
  cameraObject/modelName/model (name)
  imageProperties/canonRawProperties/sensor/width  (!= imageWidth)
  imageProperties/canonRawProperties/sensor/height (!= imageHeight)
  imageProperties/canonRawProperties/decodeTable/decodeTable-1 (table number: 0..2)
*/


/**
 */

public class CrwDecompressor {

  private CrwDecompressor(ImageInputStream in, int decodeTableNumber, int width, int height)
    throws IOException
  {
    this.in = in;
    initDecoders(decodeTableNumber);
    this.width = width;
    this.height = height;
//    in.seek(540 + lowbits*height*width/4); // And not where the image starts!
    in.seek(540);
  }


  /**
   *
   */
  private static class DecodeNode {
  }


  private static class Branch extends DecodeNode {
    public Branch(DecodeNode zero, DecodeNode one) {
      this.zero = zero;
      this.one = one;
    }
    private final DecodeNode zero;
    private final DecodeNode one;
  }


  private static class Leaf extends DecodeNode {
    public Leaf(int value) {
      this.value = value;
    }
    private final int value;
  }



  /**
   *
   */
  private static class CodeIterator {

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



  private static DecodeNode makeDecoder(int[][] source) {
    CodeIterator iterator = new CodeIterator(source);
    return makeDecoder(iterator, 0);
  }


  private static DecodeNode makeDecoder(CodeIterator iterator, int level) {
    DecodeNode result;
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



  private DecodeNode firstDecoder;


  private DecodeNode secondDecoder;


  private void initDecoders(int decodeTableNumber) {
    if (decodeTableNumber > 2)
      decodeTableNumber = 2;

    switch (decodeTableNumber) {
    case 0:
      firstDecoder  = makeDecoder(FIRST_0 );
      secondDecoder = makeDecoder(SECOND_0);
      break;
    case 1:
      firstDecoder  = makeDecoder(FIRST_1 );
      secondDecoder = makeDecoder(SECOND_1);
      break;
    case 2:
      firstDecoder  = makeDecoder(FIRST_2 );
      secondDecoder = makeDecoder(SECOND_2);
      break;
    }
  }


  private final ImageInputStream in;


  private final int width;


  private final int height;


  private static final int BLOCK_LENGTH = 64;


  private final int[] block = new int[BLOCK_LENGTH];


  private int carry = 0;


  private final int[] base = new int[2];


  private int pixel = 0;


  private void decompressBlock() throws IOException {
    clearBlock();
    readBlock();

    block[0] += carry;
    carry = block[0];

    inflateBlock();
  }


  private void clearBlock() {
    for (int i = 0; i<BLOCK_LENGTH; i++)
      block[i] = 0;
  }


  private void readBlock() throws IOException {
    for (int i = 0; i<BLOCK_LENGTH; i++) {
      DecodeNode decoder = (i == 0) ? firstDecoder : secondDecoder;
      int token = readToken(decoder);

      if ((token == 0) && (i!= 0)) {
        System.out.println("Zero token at " + (pixel+i) + "[" + i + "]");
        break;
      }

      if (token != 0xff) {
        int numSkipped = (token >> 4) & 0x0F;
        int sampleLength = token & 0x0F;

        i += numSkipped;

        if (sampleLength != 0) {
          int sample = readSample(sampleLength);
          if (i < BLOCK_LENGTH)
            block[i] = sample;
        }
      }
    }
  }


  private void inflateBlock() {
    for (int i=0; i < BLOCK_LENGTH; i++) {
      if (pixel % width == 0) {
        base[0] = 512;
        base[1] = 512;
      }

      pixel++;

      int baseIndex = i & 1;
      base[baseIndex] += block[i];
      block[i] = base[baseIndex];
    }
  }


  private void writeBlock(ImageOutputStream out) throws IOException {
    for (int i=0; i<BLOCK_LENGTH; i++)
      out.writeShort(block[i]);
  }


  private int readToken(DecodeNode decoder) throws IOException {
    DecodeNode node = decoder;
    int code = 0;
    int numBits = 0;
    while (node instanceof Branch) {
      Branch branch = (Branch) node;
      int bit = readBit();
      code = (code << 1) | bit;
      numBits++;
      node = (bit == 0) ? branch.zero : branch.one;
    }
    int result = ((Leaf) node).value;
//    System.out.println("Code= " + Integer.toHexString(code) + "[" + numBits + "]" + " value= " + result);
    return result;
  }


  private int readSample(int sampleLength) throws IOException {
    int sign = readBit();	/* 1 is positive, 0 is negative */

    int result;
    int bits = readBits(sampleLength-1);

    if (sign == 1)
      result = (1 << (sampleLength-1)) + bits;
    else
      result = ((-1 << sampleLength) + 1) + bits;

    return result;
  }


  private long freshBits = 0;
  private int numFreshBits = 0;


  private int readBits(int numBits) throws IOException {
    int result = 0;
    if (numBits != 0) {
      if (numBits > numFreshBits)
        readMoreFreshBits();
      assert (numBits <= numFreshBits) : "Oops!"; /** @todo */
      long significantFreshBits = (freshBits << (32 - numFreshBits)) & 0x00000000FFFFFFFFL;
      long outputBits = significantFreshBits >> (32 - numBits);
      result = (int) outputBits;
//      System.out.println(
//        "Fresh: " + Long.toHexString(freshBits) + "[" + numFreshBits + "] " +
//        "Significant: " + Long.toHexString(significantFreshBits) + " " +
//        "Output: " + Long.toHexString(outputBits)
//      );
      numFreshBits -= numBits;
    }
    return result;
  }


  private int readBit() throws IOException {
    return readBits(1);
  }


  private void readMoreFreshBits() throws IOException {
    while (numFreshBits < 25) {
      int b = in.readByte();
      freshBits = (freshBits << 8) | (b & 0xFF);
      numFreshBits += 8;
      if (b==0xFF)
        in.readByte(); // Canon adds 0 after each FF.
    }
  }


  void canon_compressed_load_raw() {
    /* Set the width of the black borders */
//    switch (raw_width) {
//      case 2144:  top = 8;  left =  4;  break;	/* G1 */
//      case 2224:  top = 6;  left = 48;  break;	/* EOS D30 */
//      case 2376:  top = 6;  left = 12;  break;	/* G2 or G3 */
//      case 2672:  top = 6;  left = 12;  break;	/* S50 */
//      case 3152:  top =12;  left = 64;  break;	/* EOS D60 */
//    }

//    int[] outbuf = new int[raw_width*8];
    //in.seek(540 + lowbits*height*width/4); // And not where the image starts!
    //init bit reading


//    lowbits = canon_has_lowbits();
//    shift = 4 - lowbits*2;
//    for (row = 0; row < raw_height; row += 8) {
//      for (int numBlocks = raw_width/8; numBlocks > 0; numBlocks--) {
//        decompress();		/* Get eight rows */
//      handleLowBits();
//      bordersAndStuff();
//    }
//    free(outbuf);
//    black = ((INT64) black << shift) / ((raw_width - width) * height);
  }


  private void bordersAndStuff() {
//    for (int r=0; r < 8; r++)
//      for (int col = 0; col < raw_width; col++) {
//        irow = row+r-top;
//        icol = col-left;
//        if (irow >= height) continue;
//        if (icol < width)
//          image[irow*width+icol][FC(irow,icol)] =
//            outbuf[r*raw_width+col] << shift;
//        else
//          black += outbuf[r*raw_width+col];
//      }
  }


  private void handleLowBits() {
//    if (haveLowBits) {
//      in.mark();
//      in.seek(26+row*raw_width /* seq. number of the pixel at the beginning of outbuf. */ /4);
//      for (int i = 0; i < raw_width*2; i++) {
//        int b = in.readByte();
//        for (int r = 0; r < 8; r += 2) {
//          outbuf[i] = (outbuf[i/*+1?*/] << 2) + ((b >> r) & 3);
//        }
//      }
//      in.reset();
//    }
  }


  public BufferedImage decompress() {
    ColorSpace cs = ColorSpace.getInstance(ColorSpace.TYPE_CMYK); /** @todo ??? */

    boolean hasAlpha = false;
    boolean isAlphaPremultiplied = true;
    int transparency = Transparency.OPAQUE;
    int dataType = DataBuffer.TYPE_USHORT;
    ColorModel cm = new ComponentColorModel(
      cs,
      hasAlpha,
      isAlphaPremultiplied,
      transparency,
      dataType
    );

    int pixelStride = 0; ///???
    int scanlineStride = 0; ///???
    int[] bankIndices = null; ///???
    int[] bandOffsets = null; ///???
    SampleModel sm = new ComponentSampleModel(
      dataType,
      width,
      height,
      pixelStride,
      scanlineStride,
      bankIndices,
      bandOffsets
    );


    Point location = null;
    WritableRaster raster = Raster.createWritableRaster(sm, location);
    java.util.Hashtable properties = null;
    return new BufferedImage(
      cm,
      raster,
      isAlphaPremultiplied,
      properties
    );
  }


  public void decompress(ImageOutputStream out) throws IOException {
    out.setByteOrder(ByteOrder.LITTLE_ENDIAN);

//    int numPixels = width*height;
//    do {
//      decompressBlock();
//      writeBlock(out);
//    } while (pixel <= numPixels);
    decompressBlock(); writeBlock(out); //1
    decompressBlock(); writeBlock(out);
    decompressBlock(); writeBlock(out);
    decompressBlock(); writeBlock(out);
    decompressBlock(); writeBlock(out); //5
    System.out.println("Problematic block:");
    decompressBlock(); writeBlock(out);
  }


  public static void main(String[] args) throws IOException {
    ImageInputStream in = new FileImageInputStream(new File("/tmp/2297.crw"));
    ImageOutputStream out = new FileImageOutputStream(new File("/tmp/2297.dmp"));
    CrwDecompressor decompressor = new CrwDecompressor(in, 0, 2376, 1728);
    decompressor.decompress(out);
  }
}
