package org.podval.imageio;

import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;

import java.io.File;
import java.io.IOException;


public class Main {

  public static void main(String[] args) throws
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new CiffImageReaderSpi());

    /** @todo where is the right place for this? */
    MetaMetadata.load();

    String command;
    File path;

    if (args.length>1) {
      command = args[0];
      path = new File(args[1]);
    } else {
      command = null;
      path = new File(args[0]);
    }

    if ("number".equals(command)) {
      printNumber(path);
    } else

    if ("numbers".equals(command)) {
      File[] files = path.listFiles();
      for (int i=0; i<files.length; i++) {
        File file = files[i];
        if (!file.isDirectory()) {
          printNumber(file);
        }
      }

    } else {
      Metadata metadata = readMetadata(path);
      metadata.print();
    }
  }

  private static void printNumber(File path) {
    Metadata metadata = readMetadata(path);
    if (metadata != null) {
      int number = metadata.getIntegerValue("serialNumber");
      int fff = number / 10000;
      int xxxx = number % 10000;
      System.out.println("File " + path + "; image # " + fff + "-" + xxxx);
    }
  }


  private static Metadata readMetadata(File path) {
    Metadata result = null;

    try {
      ImageInputStream in =
        ImageIO.createImageInputStream(path);

      javax.imageio.ImageReader reader =
        (javax.imageio.ImageReader) ImageIO.getImageReaders(in).next();

      reader.setInput(in);
      IIOMetadata metadata = reader.getImageMetadata(0);
      reader.dispose();
      in.close();

      /** @todo this should be done through a transcoder? */
      if (metadata instanceof JPEGMetadata) {
        metadata = ExifReader.transcodeJpegMetadata(metadata);
      }

      if (metadata instanceof Metadata) {
        result = (Metadata) metadata;
      } else {
        System.out.println("File " + path + "; unknown metadata " + metadata);
      }
    } catch (IOException e) {
      System.out.println("Exception: " + e);
      e.printStackTrace(System.out);
    }

    return result;
  }
}
