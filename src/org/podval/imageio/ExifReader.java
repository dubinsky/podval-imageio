package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class ExifReader {

  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_exif_1.0";


  public static Metadata read(ImageInputStream in) throws IOException {
    SimpleMetadataHandler handler = new SimpleMetadataHandler(NATIVE_FORMAT_NAME);
    ExifDecoder.read(in, handler);
    return handler.getResult();
  }
}
