/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageInputStream;

import java.awt.Rectangle;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import java.awt.color.ColorSpace;


/**
 */
public class CrwDecompressor {

  public static BufferedImage decompress(String path) throws IOException {
    ImageInputStream is = new FileImageInputStream(new File(path));

    CrwInformationExtractor crwInformation = new CrwInformationExtractor(is);

    return new CrwDecompressor(
      is,
      crwInformation.decodeTableNumber,
      crwInformation.width,
      crwInformation.height
    ).decompress(crwInformation.crwOffset+crwInformation.bytesToSkip);
  }


  private CrwDecompressor(
    ImageInputStream in,
    int decodeTableNumber,
    int rawWidth,
    int rawHeight
  ) throws IOException {
    this.in = in;
    this.firstDecoder = CrwDecoder.getInstance(decodeTableNumber, true);
    this.secondDecoder = CrwDecoder.getInstance(decodeTableNumber, false);
    this.rawWidth = rawWidth;
    this.rawHeight = rawHeight;
    this.numLowBits = 0;
    this.shift = 4 - 2*numLowBits; /** @todo ? */
  }


  /** @todo G2 sensor size is 2376x1728; image size is 2310x1718.
   *  There are black pixels: top - 7; bottom - 2; left - 12; right - 52.
   *  */


  private BufferedImage decompress(long offset) throws IOException {
    BufferedImage result = createImage(width, height);
    WritableRaster raster = result.getRaster();

    in.seek(offset + numLowBits*rawHeight*rawWidth/4);

    int numPixels = rawWidth*rawHeight;
    do {
      decompressBlock();
      writeBlock(raster);
    } while (numPixelsDone < numPixels);

    /** @todo low bits */

    black = (int) ((long) black << shift) / numBlack;

    scaleColors(raster);

    return result;
  }


  /*
     A rough description of Canon's compression algorithm:

  +  Each pixel outputs a 10-bit sample, from 0 to 1023.
  +  Split the data into blocks of 64 samples each.
  +  Subtract from each sample the value of the sample two positions
     to the left, which has the same color filter.  From the two
     leftmost samples in each row, subtract 512.
  +  For each nonzero sample, make a token consisting of two four-bit
     numbers.  The low nibble is the number of bits required to
     represent the sample, and the high nibble is the number of
     zero samples preceding this sample.
  +  Output this token as a variable-length bitstring using
     one of three tablesets.  Follow it with a fixed-length
     bitstring containing the sample.

     The "first_decode" table is used for the first sample in each
     block, and the "second_decode" table is used for the others.
   */


  private void decompressBlock() throws IOException {
    for (int numSample = 0; numSample < BLOCK_LENGTH; numSample++) {
      block[numSample] = 0;
    }

    for (int numSample = 0; numSample < BLOCK_LENGTH; numSample++) {
      boolean firstSample = (numSample == 0);
      CrwDecoder decoder = firstSample ? firstDecoder : secondDecoder;
      int token = readToken(decoder);

      if ((token == 0) && firstSample) {
        break;
      }

      if (token != 0xff) {
        int numSkipped = (token >> 4) & 0x0F;
        int sampleLength = token & 0x0F;

        numSample += numSkipped;

        if (sampleLength != 0) {
          int sample = readSample(sampleLength);
//          if ((sample & (1 << (sampleLength-1))) == 0)
//            sample -= (1 << sampleLength) - 1;
          if (numSample < BLOCK_LENGTH) {
            block[numSample] = sample;
          }
        }
      }
    }

    block[0] += carry;
    carry = block[0];

    for (int numSample = 0; numSample < BLOCK_LENGTH; numSample++) {
      if (numPixelsDone % rawWidth == 0) {
        base[0] = 512;
        base[1] = 512;
      }

      int baseIndex = numSample & 1;
      base[baseIndex] += block[numSample];
      block[numSample] = base[baseIndex];

      numPixelsDone++;
    }
  }


  /** @todo hardcoding be gone! */
  private static final int top = 6;
  private static final int left = 12;
  private static final int height = 1720;
  private static final int width = 2312;


  /* Bayer array:
     R G R G
     G B G B
     R G R G
   */
  private void writeBlock(WritableRaster raster) {
    for (int i=0; i<BLOCK_LENGTH; i++) {
      int pixelNumber = numPixelsDone-BLOCK_LENGTH+i;
      int y = pixelNumber / rawWidth;
      int x = pixelNumber % rawWidth;
      int value = block[i];
      x -= left;
      y -= top;
      if ((y >= 0) && (y < height) && (x >= 0) && (x < width))
          raster.setSample(x, y, getBayerBand(y, x), value << shift);
        else {
          black += value;
          numBlack++;
        }
    }
  }



//  void CLASS canon_compressed_load_raw()
//  {
//    ushort *pixel, *prow;
//    int lowbits, i, row, r, col, save, val;
//    unsigned irow, icol;
//    struct decode *decode, *dindex;
//    int block, diffbuf[64], leaf, len, diff, carry=0, pnum=0, base[2];
//    uchar c;
//
//    pixel = calloc (raw_width*8, sizeof *pixel);
//    merror (pixel, "canon_compressed_load_raw()");
//    lowbits = canon_has_lowbits();
//    if (!lowbits) maximum = 0x3ff;
//    fseek (ifp, 540 + lowbits*raw_height*raw_width/4, SEEK_SET);
//    zero_after_ff = 1;
//    getbits(-1);
//    for (row = 0; row < raw_height; row += 8) {
//      for (block=0; block < raw_width >> 3; block++) {
//        memset (diffbuf, 0, sizeof diffbuf);
//        decode = first_decode;
//        for (i=0; i < 64; i++ ) {
//          for (dindex=decode; dindex->branch[0]; )
//            dindex = dindex->branch[getbits(1)];
//          leaf = dindex->leaf;
//          decode = second_decode;
//          if (leaf == 0 && i) break;
//          if (leaf == 0xff) continue;
//          i  += leaf >> 4;
//          len = leaf & 15;
//          if (len == 0) continue;
//          diff = getbits(len);
//          if ((diff & (1 << (len-1))) == 0)
//            diff -= (1 << len) - 1;
//          if (i < 64) diffbuf[i] = diff;
//        }
//        diffbuf[0] += carry;
//        carry = diffbuf[0];
//        for (i=0; i < 64; i++ ) {
//          if (pnum++ % raw_width == 0)
//            base[0] = base[1] = 512;
//          pixel[(block << 6) + i] = ( base[i & 1] += diffbuf[i] );
//        }
//      }
//      if (lowbits) {
//        save = ftell(ifp);
//        fseek (ifp, 26 + row*raw_width/4, SEEK_SET);
//        for (prow=pixel, i=0; i < raw_width*2; i++) {
//          c = fgetc(ifp);
//          for (r=0; r < 8; r+=2, prow++) {
//            val = (*prow << 2) + ((c >> r) & 3);
//            if (raw_width == 2672 && val < 512) val += 2;
//            *prow = val;
//          }
//        }
//        fseek (ifp, save, SEEK_SET);
//      }
//      for (r=0; r < 8; r++) {
//        irow = row - top_margin + r;
//        if (irow >= height) continue;
//        for (col = 0; col < raw_width; col++) {
//          icol = col - left_margin;
//          if (icol < width)
//            BAYER(irow,icol) = pixel[r*raw_width+col];
//          else
//            black += pixel[r*raw_width+col];
//        }
//      }
//    }
//    free (pixel);
//    if (raw_width > width)
//      black /= (raw_width - width) * height;
//  }






  private void writeBlock(ImageOutputStream out) throws IOException {
    for (int i=0; i<BLOCK_LENGTH; i++)
      out.writeShort(block[i]);
  }


  private int getBayerBand(int y, int x) {
    int result;
    if (y % 2 == 0) {
      result = (x % 2 == 0) ? 0 /* R */ : 1 /* G */;
    } else {
      result = (x % 2 == 0) ? 1 /* G */ : 2 /* B */;
    }
    return result;
  }


  private int readToken(CrwDecoder decoder) throws IOException {
    CrwDecoder node = decoder;
    while (node.hasNext()) {
      node = node.getNext(readBit() == 1);
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
      if (numBits <= numFreshBits) {
          throw new AssertionError("Oops!");
      }
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


  private static final float[] preMul = new float[]{1.965f, 1, 1.208f};
  private static final int rgbMax = 0x4000;


  public void scaleColors(WritableRaster raster) {
    Rectangle bounds = raster.getBounds();
    int height = bounds.height;
    int width = bounds.width;

    int max = rgbMax - black;
    for (int y=0; y < height; y++) {
      for (int x = 0; x<width; x++) {
        for (int band = 0; band<3; band++) {
          int sample = raster.getSample(x, y, band);
          if (sample != 0) {
            sample -= black;
            sample *= preMul[band];
            if (sample < 0)
              sample = 0;
            if (sample > max)
              sample = max;
            raster.setSample(x, y, band, sample);
          }
        }
      }
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


  private final int rawWidth;


  private final int rawHeight;


  private final int numLowBits;


  private final int shift;


  private static final int BLOCK_LENGTH = 64;


  private final int[] block = new int[BLOCK_LENGTH];


  private int carry = 0;


  private final int[] base = new int[2];


  private int numPixelsDone = 0;


  private int black = 0;


  private int numBlack = 0;
}
