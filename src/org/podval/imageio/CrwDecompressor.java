package org.podval.imageio;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;

import java.awt.color.ColorSpace;


/**
 */
public class CrwDecompressor {

  public static void decompress(
    ImageInputStream in,
    int decodeTableNumber,
    int width,
    int height,
    ImageOutputStream out
  ) throws IOException
  {
    CrwDecompressor decompressor =
      new CrwDecompressor(in, decodeTableNumber, width, height);
    decompressor.decompress(out);
  }


  public static BufferedImage decompress(
    ImageInputStream in,
    int decodeTableNumber,
    int width,
    int height
  ) throws IOException
  {
    CrwDecompressor decompressor =
      new CrwDecompressor(in, decodeTableNumber, width, height);
    return decompressor.decompress();
  }


  private CrwDecompressor(
    ImageInputStream in,
    int decodeTableNumber,
    int width,
    int height
  ) throws IOException {
    this.in = in;
    this.firstDecoder = CrwDecoder.getInstance(decodeTableNumber, true);
    this.secondDecoder = CrwDecoder.getInstance(decodeTableNumber, false);
    this.width = width;
    this.height = height;
    this.numLowBits = 0;
    this.shift = 4 - 2*numLowBits; /** @todo ? */
    in.seek(540 + numLowBits*height*width/4); /** @todo and not where the image starts? */
  }


  private void decompress(ImageOutputStream out) throws IOException {
    out.setByteOrder(java.nio.ByteOrder.LITTLE_ENDIAN);

    int numPixels = width*height;
    do {
      decompressBlock();
      writeBlock(out);
    } while (numPixelsDone < numPixels);
  }


  private BufferedImage decompress() throws IOException {
    BufferedImage result = createImage(width, height);

    int numPixels = width*height;
    do {
      decompressBlock();
      writeBlock(result);
    } while (numPixelsDone < numPixels);

    return result;
  }


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
      CrwDecoder decoder = (i == 0) ? firstDecoder : secondDecoder;
      int token = readToken(decoder);

      if ((token == 0) && (i != 0)) {
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
      if (numPixelsDone % width == 0) {
        base[0] = 512;
        base[1] = 512;
      }

      int baseIndex = i & 1;
      base[baseIndex] += block[i];
      block[i] = base[baseIndex];

      numPixelsDone++;
    }
  }


  private void writeBlock(ImageOutputStream out) throws IOException {
    for (int i=0; i<BLOCK_LENGTH; i++)
      out.writeShort(block[i]);
  }


  private void writeBlock(BufferedImage result) {
    for (int i=0; i<BLOCK_LENGTH; i++) {
      int pixelNumber = numPixelsDone-BLOCK_LENGTH+i;
      int y = pixelNumber / width;
      int x = pixelNumber % width;
      int band;
      if (y % 2 == 0) {
        band = (x % 2 == 0) ? 0 /* R */ : 1 /* G */;
      } else {
        band = (x % 2 == 0) ? 1 /* G */ : 2 /* B */;
      }
      int sample = block[i] << shift;
      result.getRaster().setSample(x, y, band, sample);
    }
  }


  private int readToken(CrwDecoder decoder) throws IOException {
    CrwDecoder node = decoder;
    while (node.hasNext()) {
      node = node.getNext(readBit() == 0);
    }
    return node.getValue();
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
      /** @todo revisit */
      // First shift rolls old bits off the edge;
      // since int in Java is signed, I have to use long and a mask -
      // or redo this code, which I'd like to - one day...
      long significantFreshBits = (freshBits << (32 - numFreshBits)) & 0x00000000FFFFFFFFL;
      long outputBits = significantFreshBits >> (32 - numBits);
      result = (int) outputBits;
      numFreshBits -= numBits;
    }
    return result;
  }


  private int readBit() throws IOException {
    return readBits(1);
  }


  private void readMoreFreshBits() throws IOException {
    while (numFreshBits < 25) {
      int b = in.readByte() & 0xFF;
      freshBits = (freshBits << 8) | b;
      numFreshBits += 8;
      if (b == 0xFF)
        in.readByte(); // Canon adds 0 after each FF.
    }
  }


  private static BufferedImage createImage(int width, int height) {
    int colorSpace = ColorSpace.CS_sRGB;
    ColorSpace cs = ColorSpace.getInstance(colorSpace); /** @todo ??? */

    boolean hasAlpha = false;
    boolean isAlphaPremultiplied = true;
    int transparency = java.awt.Transparency.OPAQUE;
    int dataType = java.awt.image.DataBuffer.TYPE_USHORT;
    java.awt.image.ColorModel cm = new java.awt.image.ComponentColorModel(
      cs,
      hasAlpha,
      isAlphaPremultiplied,
      transparency,
      dataType
    );

    int numBands = 3;
    java.awt.image.SampleModel sm = new java.awt.image.BandedSampleModel(
      dataType,
      width,
      height,
      numBands
    );

    java.awt.Point location = null;
    java.awt.image.WritableRaster raster =
      java.awt.image.Raster.createWritableRaster(sm, location);

    java.util.Hashtable properties = null;
    BufferedImage result = new BufferedImage(
      cm,
      raster,
      isAlphaPremultiplied,
      properties
    );

    return result;
  }


  private final ImageInputStream in;


  private final CrwDecoder firstDecoder;


  private final CrwDecoder secondDecoder;


  private final int width;


  private final int height;


  private final int numLowBits;


  private final int shift;


  private static final int BLOCK_LENGTH = 64;


  private final int[] block = new int[BLOCK_LENGTH];


  private int carry = 0;


  private final int[] base = new int[2];


  private int numPixelsDone = 0;


/*
      26 3024436 Image
 3024433           Decode table?
 3024462   64598 Thumbnail
 3089060    5252 Small thumbnail
 3094312         ?
 3095358    2048 ?
 3097406    2048 ?
 3099454         ?
*/

//  void canon_compressed_load_raw() {
//    /* Set the width of the black borders */
//    switch (raw_width) {
//      case 2144:  top = 8;  left =  4;  break;	/* G1 */
//      case 2224:  top = 6;  left = 48;  break;	/* EOS D30 */
//      case 2376:  top = 6;  left = 12;  break;	/* G2 or G3 */
//      case 2672:  top = 6;  left = 12;  break;	/* S50 */
//      case 3152:  top =12;  left = 64;  break;	/* EOS D60 */
//    }

//    int[] outbuf = new int[raw_width*8];

//    shift = 4 - numLowBits*2;
//    for (row = 0; row < raw_height; row += 8) {
//      for (int numBlocks = raw_width/8; numBlocks > 0; numBlocks--) {
//        decompress();		/* Get eight rows */
//      handleLowBits();
//      bordersAndStuff();
//    }
//    free(outbuf);
//    black = ((INT64) black << shift) / ((raw_width - width) * height);
//  }


//  private void bordersAndStuff() {
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
//  }


//  private void handleLowBits() throws IOException {
//    if (numLowBits > 0) {
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
//  }
}
