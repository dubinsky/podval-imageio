package org.podval.imageio;

import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;


public class Main {

  public static void main(String[] args) {
    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new CiffImageReaderSpi());

    String path = args[0];

    try {
      ImageInputStream in =
        ImageIO.createImageInputStream(new java.io.File(path));

      javax.imageio.ImageReader reader =
        (javax.imageio.ImageReader) ImageIO.getImageReaders(in).next();

      reader.setInput(in);
      IIOMetadata metadata = reader.getImageMetadata(0);
      reader.dispose();
      in.close();

      /** @todo this should be done through a transcoder... */
      if (metadata instanceof JPEGMetadata) {
        metadata = JpegToExifTranscoder.read(metadata);
      }

      if (metadata instanceof Metadata) {
        ((Metadata) metadata).print();
      } else {
        System.out.println("Unknown metadata " + metadata);
      }

    } catch (Exception e) {
      System.out.println("Exception: " + e);
      e.printStackTrace(System.out);
    }
  }
}
