package org.podval.imageio;

/**
 */

public class CrwDecompressor {

  // Starting with 2-bit, not 1-bit...

  private static final int[][] FIRST_0 = {
    {0x04},
    {0x03, 0x05, 0x06, 0x02},
    {0x07, 0x01},
    {0x08, 0x09, 0x00},
    {0x0a},
    {0x0b, 0xff}
  };


  private static final int[][] SECOND_0 = {
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
    null,
    null,
    null,
    {0x31, 0x32, 0x18, 0x19, 0x33, 0x25, 0x41, 0x34, 0x42,
     0x35, 0x51, 0x36, 0x37, 0x38, 0x29, 0x79, 0x26, 0x1a, 0x39, 0x56, 0x57,
     0x28, 0x27, 0x52, 0x55, 0x58, 0x43, 0x76, 0x59, 0x77, 0x54, 0x61, 0xf9,
     0x71, 0x78, 0x75, 0x96, 0x97, 0x49, 0xb7, 0x53, 0xd7, 0x74, 0xb6, 0x98,
     0x47, 0x48, 0x95, 0x69, 0x99, 0x91, 0xfa, 0xb8, 0x68, 0xb5, 0xb9, 0xd6,
     0xf7, 0xd8, 0x67, 0x46, 0x45, 0x94, 0x89, 0xf8, 0x81, 0xd5, 0xf6, 0xb4,
     0x88, 0xb1, 0x2a, 0x44, 0x72, 0xd9, 0x87, 0x66, 0xd4, 0xf5, 0x3a, 0xa7,
     0x73, 0xa9, 0xa8, 0x86, 0x62, 0xc7, 0x65, 0xc8, 0xc9, 0xa1, 0xf4, 0xd1,
     0xe9, 0x5a, 0x92, 0x85, 0xa6, 0xe7, 0x93, 0xe8, 0xc1, 0xc6, 0x7a, 0x64,
     0xe1, 0x4a, 0x6a, 0xe6, 0xb3, 0xf1, 0xd3, 0xa5, 0x8a, 0xb2, 0x9a, 0xba,
     0x84, 0xa4, 0x63, 0xe5, 0xc5, 0xf3, 0xd2, 0xc4, 0x82, 0xaa, 0xda, 0xe4,
     0xf2, 0xca, 0x83, 0xa3, 0xa2, 0xc3, 0xea, 0xc2, 0xe2, 0xe3, 0xff, 0xff}
  };


  /*
      { 0,2,2,1,4,1,4,1,3,3,1,0,0,0,0,140,
        0x02,0x03,0x01,0x04,0x05,0x12,0x11,0x06,
        0x13,0x07,0x08,0x14,0x22,0x09,0x21,0x00,0x23,0x15,0x31,0x32,
        0x0a,0x16,0xf0,0x24,0x33,0x41,0x42,0x19,0x17,0x25,0x18,0x51,
        0x34,0x43,0x52,0x29,0x35,0x61,0x39,0x71,0x62,0x36,0x53,0x26,
        0x38,0x1a,0x37,0x81,0x27,0x91,0x79,0x55,0x45,0x28,0x72,0x59,
        0xa1,0xb1,0x44,0x69,0x54,0x58,0xd1,0xfa,0x57,0xe1,0xf1,0xb9,
        0x49,0x47,0x63,0x6a,0xf9,0x56,0x46,0xa8,0x2a,0x4a,0x78,0x99,
        0x3a,0x75,0x74,0x86,0x65,0xc1,0x76,0xb6,0x96,0xd6,0x89,0x85,
        0xc9,0xf5,0x95,0xb4,0xc7,0xf7,0x8a,0x97,0xb8,0x73,0xb7,0xd8,
        0xd9,0x87,0xa7,0x7a,0x48,0x82,0x84,0xea,0xf4,0xa6,0xc5,0x5a,
        0x94,0xa4,0xc6,0x92,0xc3,0x68,0xb5,0xc8,0xe4,0xe5,0xe6,0xe9,
        0xa2,0xa3,0xe3,0xc2,0x66,0x67,0x93,0xaa,0xd4,0xd5,0xe7,0xf8,
        0x88,0x9a,0xd7,0x77,0xc4,0x64,0xe2,0x98,0xa5,0xca,0xda,0xe8,
        0xf3,0xf6,0xa9,0xb2,0xb3,0xf2,0xd2,0x83,0xba,0xd3,0xff,0xff  },
      { 0,0,6,2,1,3,3,2,5,1,2,2,8,10,0,117,
        0x04,0x05,0x03,0x06,0x02,0x07,0x01,0x08,
        0x09,0x12,0x13,0x14,0x11,0x15,0x0a,0x16,0x17,0xf0,0x00,0x22,
        0x21,0x18,0x23,0x19,0x24,0x32,0x31,0x25,0x33,0x38,0x37,0x34,
        0x35,0x36,0x39,0x79,0x57,0x58,0x59,0x28,0x56,0x78,0x27,0x41,
        0x29,0x77,0x26,0x42,0x76,0x99,0x1a,0x55,0x98,0x97,0xf9,0x48,
        0x54,0x96,0x89,0x47,0xb7,0x49,0xfa,0x75,0x68,0xb6,0x67,0x69,
        0xb9,0xb8,0xd8,0x52,0xd7,0x88,0xb5,0x74,0x51,0x46,0xd9,0xf8,
        0x3a,0xd6,0x87,0x45,0x7a,0x95,0xd5,0xf6,0x86,0xb4,0xa9,0x94,
        0x53,0x2a,0xa8,0x43,0xf5,0xf7,0xd4,0x66,0xa7,0x5a,0x44,0x8a,
        0xc9,0xe8,0xc8,0xe7,0x9a,0x6a,0x73,0x4a,0x61,0xc7,0xf4,0xc6,
        0x65,0xe9,0x72,0xe6,0x71,0x91,0x93,0xa6,0xda,0x92,0x85,0x62,
        0xf3,0xc5,0xb2,0xa4,0x84,0xba,0x64,0xa5,0xb3,0xd2,0x81,0xe5,
        0xd3,0xaa,0xc4,0xca,0xf2,0xb1,0xe4,0xd1,0x83,0x63,0xea,0xc3,
        0xe2,0x82,0xf1,0xa3,0xc2,0xa1,0xc1,0xe3,0xa2,0xe1,0xff,0xff  }
   */


  private static final int[][] FIRST_1 = {
    {0x03, 0x02},
    {0x04, 0x01},
    {0x05, 0x00, 0x06},
    {0x07},
    {0x09},
    {0x08},
    {0x0a},
    {0x0b, 0xff}
  };


  private static final int[][] FIRST_2 = {
    null,
    {0x06, 0x05, 0x07, 0x04, 0x08, 0x03},
    {0x09, 0x02, 0x00},
    {0x0a},
    {0x01},
    {0x0b, 0xff}
  };


  private static class DecodeNode {
    public DecodeNode(DecodeNode zero, DecodeNode one, int leaf) {
      this.zero = zero;
      this.one = one;
      this.leaf = leaf;
    }



    public DecodeNode(int leaf) {
      this(null, null, leaf);
    }



    private final DecodeNode zero;


    private final DecodeNode one;


    private final int leaf;
  }
}

/*
   Construct a decode tree according the specification in *source.
   The first 16 bytes specify how many codes should be 1-bit, 2-bit
   3-bit, etc.  Bytes after that are the leaf values.
   For example, if the source is
    { 0,1,4,2,3,1,2,0,0,0,0,0,0,0,0,0,
      0x04,0x03,0x05,0x06,0x02,0x07,0x01,0x08,0x09,0x00,0x0a,0x0b,0xff  },
   then the code is
  00		0x04
  010		0x03
  011		0x05
  100		0x06
  101		0x02
  1100		0x07
  1101		0x01
  11100		0x08
  11101		0x09
  11110		0x00
  111110		0x0a
  1111110		0x0b
  1111111		0xff
  if (table > 2) table = 2;
  memset( first_decode, 0, sizeof first_decode);
  memset(second_decode, 0, sizeof second_decode);
  make_decoder( first_decode,  first_tree[table], 0);
  make_decoder(second_decode, second_tree[table], 0);
 }
 */
/*


 void make_decoder(struct decode *dest, const uchar *source, int level) {
   static struct decode *free;	// Next unused node
   static int leaf;		// no. of leaves already added
   int i, next;
   if (level==0) {
     free = dest;
     leaf = 0;
   }
   free++;
  // At what level should the next leaf appear?
   for (i=next=0; i <= leaf && next < 16; )
     i += source[next++];
   if (level < next) {		// Are we there yet?
     dest->branch[0] = free;
     make_decoder(free,source,level+1);
     dest->branch[1] = free;
     make_decoder(free,source,level+1);
   } else
     dest->leaf = source[16 + leaf++];
   }
 }
 */

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
/*
  The following information from the metadata is used:
  cameraObject/modelName/model (name)
  imageProperties/canonRawProperties/sensor/width  (!= imageWidth)
  imageProperties/canonRawProperties/sensor/height (!= imageHeight)
  imageProperties/canonRawProperties/decodeTable/decodeTable-1 (table number: 0..2)
 */
/*
   Return 0 if the image starts with compressed data,
   1 if it starts with uncompressed low-order bits.
   In Canon compressed data, 0xff is always followed by 0x00.
 int canon_has_lowbits() {
  uchar test[8192];
  int ret=1, i;
  fseek (ifp, 0, SEEK_SET);
  fread (test, 1, 8192, ifp);
  for (i=540; i < 8191; i++)
    if (test[i] == 0xff) {
      if (test[i+1]) return 1;
      ret=0;
    }
  return ret;
 }
  fprintf(stderr,"name = %s, width = %d, height = %d, table = %d, bpp = %d\n",
  name, width, height, table, 10+lowbits*2);
 */
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
/*
   getbits(-1) initializes the buffer
   getbits(n) where 0 <= n <= 25 returns an n-bit integer
 unsigned long getbits(int nbits) {
  static unsigned long bitbuf=0, ret=0;
  static int vbits=0;
  unsigned char c;
  if (nbits == 0) return 0;
  if (nbits == -1)
    ret = bitbuf = vbits = 0;
  else {
    ret = bitbuf << (32 - vbits) >> (32 - nbits);
    vbits -= nbits;
  }
  while (vbits < 25) {
    c=fgetc(ifp);
    bitbuf = (bitbuf << 8) + c;
    if (c == 0xff) fgetc(ifp);	// always extra 00 after ff
    vbits += 8;
  }
  return ret;
 }
 */
/*
 int main(int argc, char **argv) {
  struct decode *decode, *dindex;
  int i, j, leaf, len, sign, diff, diffbuf[64], r, save;
  int carry=0, column=0, base[2];
  unsigned short outbuf[64];
  uchar c;
  init_tables(table);
  fseek (ifp, 540 + lowbits*height*width/4, SEEK_SET);
  getbits(-1);			// Prime the bit buffer
    if (lowbits) {
      save = ftell(ifp);
      fseek (ifp, (column-64)/4 + 26, SEEK_SET);
      for (i=j=0; j < 64/4; j++ ) {
  c = fgetc(ifp);
  for (r = 0; r < 8; r += 2)
    outbuf[i++] = (outbuf[i] << 2) + ((c >> r) & 3);
      }
      fseek (ifp, save, SEEK_SET);
    }
    fwrite(outbuf,2,64,stdout);
  }
  return 0;
 }
    /*
         Decompress "count" blocks of 64 samples each.
         Note that the width passed to this function is slightly
         larger than the global width, because it includes some
         blank pixels that (*load_raw) will strip off.
      void decompress(ushort *outbuf, int count) {
        struct decode *decode, *dindex;
        int i, leaf, len, sign, diff, diffbuf[64];
        static int carry, pixel, base[2];
        if (!outbuf) {			// Initialize
          carry = pixel = 0;
          fseek (ifp, count, SEEK_SET);
          getbits(-1);
          return;
        }
        while (count--) {
          memset(diffbuf,0,sizeof diffbuf);
          decode = first_decode;
        for (i=0; i < 64; i++ ) {
          for (dindex=decode; dindex->branch[0]; )
      dindex = dindex->branch[getbits(1)];
          leaf = dindex->leaf;
          decode = second_decode;
          if (leaf == 0 && i) break;
          if (leaf == 0xff) continue;
          i  += leaf >> 4;
          len = leaf & 15;
          if (len == 0) continue;
          sign=(getbits(1));	// 1 is positive, 0 is negative
          diff=getbits(len-1);
          if (sign)
      diff += 1 << (len-1);
          else
      diff += (-1 << len) + 1;
          if (i < 64) diffbuf[i] = diff;
        }
          diffbuf[0] += carry;
          carry = diffbuf[0];
          for (i=0; i < 64; i++ ) {
            if (pixel++ % raw_width == 0)
        base[0] = base[1] = 512;
            outbuf[i] = ( base[i & 1] += diffbuf[i] );
          }
          outbuf += 64;
        }
      }
  */
 /*
     void canon_compressed_load_raw() {
       ushort *pixel, *prow;
       int lowbits, shift, i, row, r, col, save;
       unsigned top=0, left=0, irow, icol;
       uchar c;
     // Set the width of the black borders
       switch (raw_width) {
         case 2144:  top = 8;  left =  4;  break;	// G1
         case 2224:  top = 6;  left = 48;  break;	// EOS D30
         case 2376:  top = 6;  left = 12;  break;	// G2 or G3
         case 2672:  top = 6;  left = 12;  break;	// S50
         case 3152:  top =12;  left = 64;  break;	// EOS D60
       }
       pixel = calloc (raw_width*8, sizeof *pixel);
       merror (pixel, "canon_compressed_load_raw()");
       lowbits = canon_has_lowbits();
       shift = 4 - lowbits*2;
       decompress(0, 540 + lowbits*raw_height*raw_width/4);
       for (row = 0; row < raw_height; row += 8) {
         decompress(pixel, raw_width/8);		// Get eight rows
         if (lowbits) {
           save = ftell(ifp);			// Don't lose our place
           fseek (ifp, 26 + row*raw_width/4, SEEK_SET);
           for (prow=pixel, i=0; i < raw_width*2; i++) {
       c = fgetc(ifp);
       for (r = 0; r < 8; r += 2)
  *prow++ = (*prow << 2) + ((c >> r) & 3);
           }
           fseek (ifp, save, SEEK_SET);
         }
         for (r=0; r < 8; r++)
           for (col = 0; col < raw_width; col++) {
       irow = row+r-top;
       icol = col-left;
       if (irow >= height) continue;
       if (icol < width)
         image[irow*width+icol][FC(irow,icol)] =
         pixel[r*raw_width+col] << shift;
         else
           black += pixel[r*raw_width+col];
           }
       }
       free(pixel);
       black = ((INT64) black << shift) / ((raw_width - width) * height);
     }
  */
