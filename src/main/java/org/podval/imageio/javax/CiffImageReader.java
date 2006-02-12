/* $Id$ */

package org.podval.imageio.javax;

import org.podval.imageio.CiffReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;

import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.Iterator;


public class CiffImageReader extends ImageReader {

  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_ciff_1.0";


  public CiffImageReader(ImageReaderSpi originatingProvider) {
    super(originatingProvider);
  }


  public static boolean canDecodeInput(ImageInputStream in) {
    return new CiffReader().canRead(in);
  }


  public int getNumImages(boolean allowSearch) {
    return 1;
  }


  public BufferedImage read(int imageIndex, ImageReadParam param)
    throws IOException
  {
    /** @todo XXXX */
    return null;
  }


  public boolean readerSupportsThumbnails() {
    return true;
  }


  public int getNumThumbnails(int imageIndex) {
    checkImageIndex(imageIndex);
    return 2;
  }


  public BufferedImage readThumbnail(int imageIndex, int thumbnailIndex)
    throws IOException
  {
    if ((thumbnailIndex < 0) || (thumbnailIndex > 1))
      throw new ArrayIndexOutOfBoundsException("Thumbnail index out of bounds: " + thumbnailIndex);

    long offset = 0;

    /** @todo  */
//    if (thumbnailIndex == 0)
//      offset = ... "jpegThumbnail"
//    else
//      offset = ... "bigJpegThumbnail"

    BufferedImage result = null;

    if (offset != 0) {
      ImageInputStream in = getInputStream();
      in.seek(offset);

      Iterator readers = ImageIO.getImageReaders(in);
      ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

      if (reader!=null) {
        reader.setInput(in);
        result = reader.read(0);
        reader.dispose();
      }
      /** @todo else: XXXX */
    }

    return result;
  }


  public IIOMetadata getStreamMetadata() throws IOException {
    return null;
  }


  public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
    checkImageIndex(imageIndex);
    /** @todo  */
    return null;
  }


  public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) {
    checkImageIndex(imageIndex);
    /** @todo XXXX */
    return null;
  }


  public int getHeight(int imageIndex) throws IOException {
    return 0; /** @todo */ // "imageHeight"
  }


  public int getWidth(int imageIndex) throws IOException {
    return 0; /** @todo */ // "imageWidth"
  }


  private void checkImageIndex(int imageIndex) {
    if (imageIndex != 0)
      throw new IndexOutOfBoundsException("Image index out of bounds: " + imageIndex);
  }


  private ImageInputStream getInputStream() throws IOException {
    ImageInputStream result = (ImageInputStream) getInput();
    /** @todo in the SPI I check that this is an ImageInputStream... */
    if (result == null)
      throw new IllegalStateException("Input not set.");
    result.seek(0); /** @todo use mark/reset instead!!! */
    return result;
  }
}
