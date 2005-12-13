package org.podval.imageio;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
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

    Metadata metadata = (Metadata) getImageMetadata(imageIndex);
    Field.PointerValue pointer = null;

    if (thumbnailIndex == 0)
      pointer = (Field.PointerValue) metadata.find("jpegThumbnail");
    else
      pointer = (Field.PointerValue) metadata.find("bigJpegThumbnail");

    BufferedImage result = null;

    if (pointer != null) {
      ImageInputStream in = getInputStream();
      in.seek(pointer.getOffset());

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
    readMetadata();
    return null;
  }


  public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
    checkImageIndex(imageIndex);
    readMetadata();
    return metadata;
  }


  public Iterator getImageTypes(int imageIndex) {
    checkImageIndex(imageIndex);
    /** @todo XXXX */
    return null;
  }


  public int getHeight(int imageIndex) throws IOException {
    return ((Metadata) getImageMetadata(imageIndex)).getIntValue("imageHeight");
  }


  public int getWidth(int imageIndex) throws IOException {
    return ((Metadata) getImageMetadata(imageIndex)).getIntValue("imageWidth");
  }


  private void readMetadata() throws IOException {
    if (metadata == null) {
/////      DefaultMetadataHandler handler =
/////        new DefaultMetadataHandler(NATIVE_FORMAT_NAME);
      /** @todo !!! */
//      CiffDecoder.read(getInputStream(), NATIVE_FORMAT_NAME, handler);
/////      metadata = handler.getResult();
    }
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
    result.seek(0); /** @todo use marl/reset instead!!! */
    return result;
  }


  private Metadata metadata;
}
