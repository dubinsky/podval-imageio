package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;

/** @todo I want to separate javax.imageio glue from the business code.
 * That is why I have
 * JpegToExifTranscoder, JpegStreamExifDecoder, ExifReader / ExifDecoder and
 * CiffImageReader[Spi] / CiffMetadataReader.
 * The names are not exactly logical, though...
 * Also, eventually there will be CRW image-reading code...
 */
public class ExifReader {

  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_exif_1.0";


  public static Metadata read(ImageInputStream in) throws IOException {
    SimpleMetadataHandler handler = new SimpleMetadataHandler(NATIVE_FORMAT_NAME);
    ExifDecoder.read(in, handler);
    return handler.getResult();
  }
}
