package org.podval.imageio;

import java.io.IOException;

import java.nio.ByteOrder;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.BandedSampleModel;
import java.awt.image.Raster;
import java.awt.image.DataBuffer;

import java.awt.Transparency;
import java.awt.Point;

import java.awt.color.ColorSpace;

import java.util.Hashtable;


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
    this.shift = 4 - 2*numLowBits; //???
    in.seek(540 + numLowBits*height*width/4); // And not where the image starts!
  }


  private void decompress(ImageOutputStream out) throws IOException {
    out.setByteOrder(ByteOrder.LITTLE_ENDIAN);

    int numPixels = width*height;
    do {
      decompressBlock();
      writeBlock(out);
    } while (pixel < numPixels);
  }


  private BufferedImage decompress() throws IOException {
    BufferedImage result = createImage(width, height);

    int numPixels = width*height;
    do {
      decompressBlock();
      writeBlock(result);
    } while (pixel < numPixels);

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
      if (pixel % width == 0) {
        base[0] = 512;
        base[1] = 512;
      }

      int baseIndex = i & 1;
      base[baseIndex] += block[i];
      block[i] = base[baseIndex];

      pixel++;
    }
  }


  private void writeBlock(ImageOutputStream out) throws IOException {
    for (int i=0; i<BLOCK_LENGTH; i++)
      out.writeShort(block[i]);
  }


  private void writeBlock(BufferedImage result) {
    for (int i=0; i<BLOCK_LENGTH; i++) {
      int pixelNumber = pixel-BLOCK_LENGTH+i;
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
      /** @todo  */
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
    int transparency = Transparency.OPAQUE;
    int dataType = DataBuffer.TYPE_USHORT;
    ColorModel cm = new ComponentColorModel(
      cs,
      hasAlpha,
      isAlphaPremultiplied,
      transparency,
      dataType
    );

    int numBands = 3;
    SampleModel sm = new BandedSampleModel(
      dataType,
      width,
      height,
      numBands
    );

    Point location = null;
    WritableRaster raster = Raster.createWritableRaster(sm, location);

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


  private int pixel = 0;


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


///*
//   This algorithm is officially called:
//
//   "Interpolation using a Threshold-based variable number of gradients"
//
//   described in http://www-ise.stanford.edu/~tingchen/algodep/vargra.html
//
//   I've extended the basic idea to work with non-Bayer filter arrays.
//   Gradients are numbered clockwise from NW=0 to W=7.
// */
//void vng_interpolate()
//{
//  static const signed char *cp, terms[] = {
//    -2,-2,+0,-1,0,0x01, -2,-2,+0,+0,1,0x01, -2,-1,-1,+0,0,0x01,
//    -2,-1,+0,-1,0,0x02, -2,-1,+0,+0,0,0x03, -2,-1,+0,+1,0,0x01,
//    -2,+0,+0,-1,0,0x06, -2,+0,+0,+0,1,0x02, -2,+0,+0,+1,0,0x03,
//    -2,+1,-1,+0,0,0x04, -2,+1,+0,-1,0,0x04, -2,+1,+0,+0,0,0x06,
//    -2,+1,+0,+1,0,0x02, -2,+2,+0,+0,1,0x04, -2,+2,+0,+1,0,0x04,
//    -1,-2,-1,+0,0,0x80, -1,-2,+0,-1,0,0x01, -1,-2,+1,-1,0,0x01,
//    -1,-2,+1,+0,0,0x01, -1,-1,-1,+1,0,0x88, -1,-1,+1,-2,0,0x40,
//    -1,-1,+1,-1,0,0x22, -1,-1,+1,+0,0,0x33, -1,-1,+1,+1,1,0x11,
//    -1,+0,-1,+2,0,0x08, -1,+0,+0,-1,0,0x44, -1,+0,+0,+1,0,0x11,
//    -1,+0,+1,-2,0,0x40, -1,+0,+1,-1,0,0x66, -1,+0,+1,+0,1,0x22,
//    -1,+0,+1,+1,0,0x33, -1,+0,+1,+2,0,0x10, -1,+1,+1,-1,1,0x44,
//    -1,+1,+1,+0,0,0x66, -1,+1,+1,+1,0,0x22, -1,+1,+1,+2,0,0x10,
//    -1,+2,+0,+1,0,0x04, -1,+2,+1,+0,0,0x04, -1,+2,+1,+1,0,0x04,
//    +0,-2,+0,+0,1,0x80, +0,-1,+0,+1,1,0x88, +0,-1,+1,-2,0,0x40,
//    +0,-1,+1,+0,0,0x11, +0,-1,+2,-2,0,0x40, +0,-1,+2,-1,0,0x20,
//    +0,-1,+2,+0,0,0x30, +0,-1,+2,+1,0,0x10, +0,+0,+0,+2,1,0x08,
//    +0,+0,+2,-2,1,0x40, +0,+0,+2,-1,0,0x60, +0,+0,+2,+0,1,0x20,
//    +0,+0,+2,+1,0,0x30, +0,+0,+2,+2,1,0x10, +0,+1,+1,+0,0,0x44,
//    +0,+1,+1,+2,0,0x10, +0,+1,+2,-1,0,0x40, +0,+1,+2,+0,0,0x60,
//    +0,+1,+2,+1,0,0x20, +0,+1,+2,+2,0,0x10, +1,-2,+1,+0,0,0x80,
//    +1,-1,+1,+1,0,0x88, +1,+0,+1,+2,0,0x08, +1,+0,+2,-1,0,0x40,
//    +1,+0,+2,+1,0,0x10
//  }, chood[] = { -1,-1, -1,0, -1,+1, 0,+1, +1,+1, +1,0, +1,-1, 0,-1 };
//  ushort (*brow[5])[4], *pix;
//  int code[8][640], *ip, gval[8], gmin, gmax, sum[4];
//  int row, col, shift, x, y, x1, x2, y1, y2, t, weight, grads, color, diag;
//  int g, diff, thold, num, c;
//
//  for (row=0; row < 8; row++) {		/* Precalculate for bilinear */
//    ip = code[row];
//    for (col=1; col < 3; col++) {
//      memset (sum, 0, sizeof sum);
//      for (y=-1; y <= 1; y++)
//  for (x=-1; x <= 1; x++) {
//    shift = (y==0) + (x==0);
//    if (shift == 2) continue;
//    color = FC(row+y,col+x);
//    *ip++ = (width*y + x)*4 + color;
//    *ip++ = shift;
//    *ip++ = color;
//    sum[color] += 1 << shift;
//  }
//      for (c=0; c < colors; c++)
//  if (c != FC(row,col)) {
//    *ip++ = c;
//    *ip++ = sum[c];
//  }
//    }
//  }
//  for (row=1; row < height-1; row++) {	/* Do bilinear interpolation */
//    pix = image[row*width+1];
//    for (col=1; col < width-1; col++) {
//      if (col & 1)
//  ip = code[row & 7];
//      memset (sum, 0, sizeof sum);
//      for (g=8; g--; ) {
//  diff = pix[*ip++];
//  diff <<= *ip++;
//  sum[*ip++] += diff;
//      }
//      for (g=colors; --g; ) {
//  c = *ip++;
//  pix[c] = sum[c] / *ip++;
//      }
//      pix += 4;
//    }
//  }
//  if (quick_interpolate)
//    return;
//  for (row=0; row < 8; row++) {		/* Precalculate for VNG */
//    ip = code[row];
//    for (col=0; col < 2; col++) {
//      for (cp=terms, t=0; t < 64; t++) {
//  y1 = *cp++;  x1 = *cp++;
//  y2 = *cp++;  x2 = *cp++;
//  weight = *cp++;
//  grads = *cp++;
//  color = FC(row+y1,col+x1);
//  if (FC(row+y2,col+x2) != color) continue;
//  diag = (FC(row,col+1) == color && FC(row+1,col) == color) ? 2:1;
//  if (abs(y1-y2) == diag && abs(x1-x2) == diag) continue;
//  *ip++ = (y1*width + x1)*4 + color;
//  *ip++ = (y2*width + x2)*4 + color;
//  *ip++ = weight;
//  for (g=0; g < 8; g++)
//    if (grads & 1<<g) *ip++ = g;
//  *ip++ = -1;
//      }
//      *ip++ = INT_MAX;
//      for (cp=chood, g=0; g < 8; g++) {
//  y = *cp++;  x = *cp++;
//  *ip++ = (y*width + x) * 4;
//  color = FC(row,col);
//  if ((g & 1) == 0 &&
//      FC(row+y,col+x) != color && FC(row+y*2,col+x*2) == color)
//    *ip++ = (y*width + x) * 8 + color;
//  else
//    *ip++ = 0;
//      }
//    }
//  }
//  brow[4] = calloc (width*3, sizeof **brow);
//  merror (brow[4], "vng_interpolate()");
//  for (row=0; row < 3; row++)
//    brow[row] = brow[4] + row*width;
//  for (row=2; row < height-2; row++) {		/* Do VNG interpolation */
//    pix = image[row*width+2];
//    for (col=2; col < width-2; col++) {
//      if ((col & 1) == 0)
//  ip = code[row & 7];
//      memset (gval, 0, sizeof gval);
//      while ((g = *ip++) != INT_MAX) {		/* Calculate gradients */
//  diff = abs(pix[g] - pix[*ip++]);
//  diff <<= *ip++;
//  while ((g = *ip++) != -1)
//    gval[g] += diff;
//      }
//      gmin = INT_MAX;				/* Choose a threshold */
//      gmax = 0;
//      for (g=0; g < 8; g++) {
//  if (gmin > gval[g]) gmin = gval[g];
//  if (gmax < gval[g]) gmax = gval[g];
//      }
//      thold = gmin + (gmax >> 1);
//      memset (sum, 0, sizeof sum);
//      color = FC(row,col);
//      for (num=g=0; g < 8; g++,ip+=2) {		/* Average the neighbors */
//  if (gval[g] <= thold) {
//    for (c=0; c < colors; c++)
//      if (c == color && ip[1])
//        sum[c] += (pix[c] + pix[ip[1]]) >> 1;
//      else
//        sum[c] += pix[ip[0] + c];
//    num++;
//  }
//      }
//      for (c=0; c < colors; c++) {		/* Save to buffer */
//  t = pix[color] + (sum[c] - sum[color])/num;
//  brow[2][col][c] = t > 0 ? t:0;
//      }
//      pix += 4;
//    }
//    if (row > 3)				/* Write buffer to image */
//      memcpy (image[(row-2)*width+2], brow[0]+2, (width-4)*sizeof *image);
//    for (g=0; g < 4; g++)
//      brow[(g-1) & 3] = brow[g];
//  }
//  memcpy (image[(row-2)*width+2], brow[0]+2, (width-4)*sizeof *image);
//  memcpy (image[(row-1)*width+2], brow[1]+2, (width-4)*sizeof *image);
//  free(brow[4]);
//}
}
