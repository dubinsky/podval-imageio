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
      ImageInputStream input = (ImageInputStream) getInput();
      if (input == null)
        throw new IllegalStateException("Input not set.");

      metadata = CiffMetadataReader.read(input);
    }
  }


  private void checkImageIndex(int imageIndex) {
    if (imageIndex!=0)
      throw new IndexOutOfBoundsException();
  }


  private Metadata metadata;
}
