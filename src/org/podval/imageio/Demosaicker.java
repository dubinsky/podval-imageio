package org.podval.imageio;

import java.awt.image.WritableRaster;

import java.awt.Rectangle;


/**
 * R G R G R G
 * G B G B G B
 * R G R G R G
 * G B G B G B
 */

public class Demosaicker {

  private static final int RED_BAND = 0;
  private static final int GREEN_BAND = 1;
  private static final int BLUE_BAND = 2;

  public static void bilinear(WritableRaster raster) {
    Rectangle bounds = raster.getBounds();
    int height = bounds.height;
    int width = bounds.width;

    /** @todo for now (?) I am not interpolating the borders.
     * Hence false ... 1 ... -1
     */
    boolean yEven = false;
    for (int y=1; y<height-1; y++) {
      boolean xEven = false;
      for (int x=1; x<width-1; x++) {
        if (yEven && xEven) {
          /* B G B
             G[R]G
             B G B */
          averageSquare(y, x, BLUE_BAND , raster);
          averageRomb  (y, x, GREEN_BAND, raster);
        } else if (yEven && !xEven) {
          /* G B G
             R[G]R
             G B G */
          averageHorizontal(y, x, RED_BAND , raster);
          averageVertical  (y, x, BLUE_BAND, raster);
        } else if (!yEven && xEven) {
          /* G R G
             B[G]B
             G R G */
          averageHorizontal(y, x, BLUE_BAND, raster);
          averageVertical  (y, x, RED_BAND , raster);
        } else if (!yEven && !xEven) {
          /* R G R
             G[B]G
             R G R */
          averageSquare(y, x, RED_BAND  , raster);
          averageRomb  (y, x, GREEN_BAND, raster);
        }
        xEven = !xEven;
      }
      yEven = !yEven;
    }
  }


  private static void averageSquare(int y, int x, int band, WritableRaster raster) {
    int sum =
      raster.getSample(x-1, y-1, band) +
      raster.getSample(x+1, y-1, band) +
      raster.getSample(x-1, y+1, band) +
      raster.getSample(x+1, y+1, band);
    raster.setSample(x, y, band, sum/4);
  }


  private static void averageRomb(int y, int x, int band, WritableRaster raster) {
    int sum =
      raster.getSample(x  , y-1, band) +
      raster.getSample(x-1, y  , band) +
      raster.getSample(x+1, y  , band) +
      raster.getSample(x  , y+1, band);
    raster.setSample(x, y, band, sum/4);
  }


  private static void averageHorizontal(int y, int x, int band, WritableRaster raster) {
    int sum =
      raster.getSample(x-1, y, band) +
      raster.getSample(x+1, y, band);
    raster.setSample(x, y, band, sum/2);
  }


  private static void averageVertical(int y, int x, int band, WritableRaster raster) {
    int sum =
      raster.getSample(x, y-1, band) +
      raster.getSample(x, y+1, band);
    raster.setSample(x, y, band, sum/2);
  }



  public void variableNumberGradients(WritableRaster raster) {
  }


//scale_colors();
//vng_interpolate();
//convert_to_rgb();


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
//void vng_interpolate() {
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
//        for (x=-1; x <= 1; x++) {
//          shift = (y==0) + (x==0);
//          if (shift == 2) continue;
//          color = FC(row+y, col+x);
//          *ip++ = (width*y+x)*4+color;
//          *ip++ = shift;
//          *ip++ = color;
//          sum[color] += 1<<shift;
//        }
//      for (c=0; c < colors; c++)
//        if (c != FC(row,col)) {
//          *ip++ = c;
//          *ip++ = sum[c];
//        }
//    }
//  }
//  for (row=1; row < height-1; row++) {	/* Do bilinear interpolation */
//    pix = image[row*width+1];
//    for (col=1; col < width-1; col++) {
//      if (col & 1)
//        ip = code[row & 7];
//      memset (sum, 0, sizeof sum);
//      for (g=8; g--; ) {
//        diff = pix[*ip++];
//        diff <<= *ip++;
//        sum[*ip++] += diff;
//      }
//      for (g=colors; --g; ) {
//        c = *ip++;
//        pix[c] = sum[c]/(*ip++);
//      }
//      pix += 4;
//    }
//  }
//  if (quick_interpolate)
//    return;
//
//  for (row=0; row < 8; row++) {		/* Precalculate for VNG */
//    ip = code[row];
//    for (col=0; col < 2; col++) {
//      for (cp=terms, t=0; t < 64; t++) {
//        y1 = *cp++;
//        x1 = *cp++;
//        y2 = *cp++;
//        x2 = *cp++;
//        weight = *cp++;
//        grads = *cp++;
//        color = FC(row+y1, col+x1);
//        if (FC(row+y2, col+x2)!=color)
//          continue;
//        diag = (FC(row, col+1)==color && FC(row+1, col)==color) ? 2 : 1;
//        if (abs(y1-y2) == diag && abs(x1-x2) == diag)
//          continue;
//        *ip++ = (y1*width+x1)*4+color;
//        *ip++ = (y2*width+x2)*4+color;
//        *ip++ = weight;
//        for (g = 0; g<8; g++)
//          if (grads&1<<g)
//            *ip++ = g;
//        *ip++ = -1;
//      }
//      *ip++ = INT_MAX;
//      for (cp = chood, g = 0; g<8; g++) {
//        y = *cp++;
//        x = *cp++;
//        *ip++ = (y*width+x)*4;
//        color = FC(row, col);
//        if ((g&1)==0&&
//          FC(row+y, col+x)!=color&&FC(row+y*2, col+x*2)==color)
//          *ip++ = (y*width+x)*8+color;
//        else
//          *ip++ = 0;
//      }
//    }
//  }
//  brow[4] = calloc (width*3, sizeof **brow);
//  merror (brow[4], "vng_interpolate()");
//  for (row = 0; row<3; row++)
//    brow[row] = brow[4]+row*width;
//
//  for (row = 2; row<height-2; row++) { /* Do VNG interpolation */
//    pix = image[row*width+2];
//    for (col = 2; col<width-2; col++) {
//      if ((col&1)==0)
//        ip = code[row&7];
//      memset(gval, 0, sizeof gval);
//      while ((g = *ip++) != INT_MAX) { /* Calculate gradients */
//        diff = abs(pix[g]-pix[*ip++]);
//        diff <<= *ip++;
//        while ((g = *ip++)!=-1)
//          gval[g] += diff;
//      }
//      gmin = INT_MAX; /* Choose a threshold */
//      gmax = 0;
//      for (g = 0; g<8; g++) {
//        if (gmin>gval[g])
//          gmin = gval[g];
//        if (gmax<gval[g])
//          gmax = gval[g];
//      }
//      thold = gmin + (gmax>>1);
//      memset(sum, 0, sizeof sum);
//      color = FC(row, col);
//      for (num = g = 0; g<8; g++, ip += 2) { /* Average the neighbors */
//        if (gval[g]<=thold) {
//          for (c = 0; c<colors; c++)
//            if (c==color&&ip[1])
//              sum[c] += (pix[c]+pix[ip[1]])>>1;
//            else
//              sum[c] += pix[ip[0]+c];
//          num++;
//        }
//      }
//      for (c = 0; c<colors; c++) { /* Save to buffer */
//        t = pix[color]+(sum[c]-sum[color])/num;
//        brow[2][col][c] = t>0 ? t : 0;
//      }
//      pix += 4;
//    }
//    if (row>3) /* Write buffer to image */
//      memcpy(image[(row-2)*width+2], brow[0]+2, (width-4)*sizeof*image);
//    for (g = 0; g<4; g++)
//      brow[(g-1)&3] = brow[g];
//  }
//  memcpy(image[(row-2)*width+2], brow[0]+2, (width-4)*sizeof*image);
//  memcpy(image[(row-1)*width+2], brow[1]+2, (width-4)*sizeof*image);
//  free(brow[4]);
//}


//  /*
//     Convert the entire image to RGB colorspace and build a histogram.
//   */
//  void convert_to_rgb()
//  {
//    int row, col, r, g, c=0;
//    ushort *img;
//    float rgb[4];
//
//    if (document_mode)
//      colors = 1;
//    memset (histogram, 0, sizeof histogram);
//    for (row = trim; row < height-trim; row++)
//      for (col = trim; col < width-trim; col++) {
//        img = image[row*width+col];
//        if (document_mode)
//    c = FC(row,col);
//        if (colors == 4 && !use_coeff)	/* Recombine the greens */
//    img[1] = (img[1] + img[3]) >> 1;
//        if (colors == 1)			/* RGB from grayscale */
//    for (r=0; r < 3; r++)
//      rgb[r] = img[c];
//        else if (use_coeff) {		/* RGB from GMCY or Foveon */
//    for (r=0; r < 3; r++)
//      for (rgb[r]=g=0; g < colors; g++)
//        rgb[r] += img[g] * coeff[r][g];
//        } else if (is_cmy) {		/* RGB from CMY */
//    rgb[0] = img[0] + img[1] - img[2];
//    rgb[1] = img[1] + img[2] - img[0];
//    rgb[2] = img[2] + img[0] - img[1];
//        } else				/* RGB from RGB (easy) */
//    for (r=0; r < 3; r++)
//      rgb[r] = img[r];
//        for (rgb[3]=r=0; r < 3; r++) {	/* Compute the magnitude */
//    if (rgb[r] < 0) rgb[r] = 0;
//    if (rgb[r] > rgb_max) rgb[r] = rgb_max;
//    rgb[3] += rgb[r]*rgb[r];
//        }
//        rgb[3] = sqrt(rgb[3]);
//        if (rgb[3] > 0xffff) rgb[3] = 0xffff;
//        for (r=0; r < 4; r++)
//    img[r] = rgb[r];
//        histogram[img[3] >> 4]++;		/* bin width is 16 */
//      }
//  }
}
