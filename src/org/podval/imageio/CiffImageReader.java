package org.podval.imageio;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;

import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.Iterator;


public class CiffImageReader extends ImageReader {

  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_ciff_1.0";


  public CiffImageReader(ImageReaderSpi originatingProvider) {
    super(originatingProvider);
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
    checkImageIndex(imageIndex);
    readMetadata();
    return metadata.getIntegerValue("imageHeight");
  }


  public int getWidth(int imageIndex) throws IOException {
    checkImageIndex(imageIndex);
    readMetadata();
    return metadata.getIntegerValue("imageWidth");
  }


  private void readMetadata() throws IOException {
    if (metadata == null) {
      DefaultMetadataHandler handler =  new DefaultMetadataHandler(NATIVE_FORMAT_NAME);
      CiffDecoder.read(getInputStream(), handler);
      metadata = handler.getResult();
    }
  }


  private void checkImageIndex(int imageIndex) {
    if (imageIndex!=0)
      throw new IndexOutOfBoundsException();
  }


  private ImageInputStream getInputStream() {
    ImageInputStream result = (ImageInputStream) getInput();
    if (result == null)
      throw new IllegalStateException("Input not set.");
    /** @todo reset? seek(0)? */
    return result;
  }


  private Metadata metadata;
}
