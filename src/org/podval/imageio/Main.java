package org.podval.imageio;

import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.metadata.IIOMetadata;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.awt.image.BufferedImage;

import java.util.Iterator;


public class Main {

  public static void main(String[] args) throws
    IOException,
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new
      CiffImageReaderSpi());

    /** @todo where is the right place for this? */
    MetaMetadata.load();

    String command;
    String arg;

    if (args.length>1) {
      command = args[0];
      arg = args[1];
    } else {
      command = "dump";
      arg = args[0];
    }

    File file = new File(arg);
    File path = file.getParentFile();
    String name = file.getName();

    int star = name.indexOf('*');
    String start = null;
    String end = null;
    if (star != -1) {
      start = name.substring(0, star);
      end = name.substring(star+1, name.length());
      File[] files = path.listFiles();
      for (int i=0; i<files.length; i++) {
        file = files[i];
        name = file.getName();

        if (name.startsWith(start) && name.endsWith(end)) {
          doCommand(command, file);
        }
      }
    } else {
      doCommand(command, file);
    }
  }


  private static void doCommand(String command, File file) throws
    IOException,
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    System.err.print("File " + file + ": ");

    if (!file.exists() || !file.isFile()) {
      System.err.println(" must exist and be a file.");
      return;
    }

    String fullName = file.getName();
    int dot = fullName.lastIndexOf('.');
    String name;
    String extension;

    if (dot != -1) {
      name = fullName.substring(0, dot);
      extension = fullName.substring(dot+1, fullName.length());
    } else {
      name = fullName;
      extension = null;
    }


    if ("number".equals(command))
      printNumber(file);
    else if ("decompress".equals(command))
      decompress(file, name);
    else if ("dump".equals(command)) {
      System.err.println();
      readMetadata(file).print();
    } else {
      System.err.println("Usage: XXX <command> <arg>");
      System.err.println();
      System.err.println("Recognized commands:");
      System.err.println("  dump - print native metadata");
      System.err.println("  number - print identification");
      System.err.println("  decompress - extract raw image out of a .crw");
    }
  }


  private static Metadata readMetadata(File file) {
    Metadata result = null;

    try {
      ImageInputStream in =
        ImageIO.createImageInputStream(file);

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
        System.out.println("unknown metadata " + metadata);
      }
    } catch (IOException e) {
      System.out.println("Exception: " + e);
      e.printStackTrace(System.out);
    }

    return result;
  }


  private static void printNumber(File file) {
    Metadata metadata = readMetadata(file);
    if (metadata != null) {
      int number = metadata.getIntValue("serialNumber");
      int fff = number / 10000;
      int xxxx = number % 10000;
      System.out.println("image # " + fff + "-" + xxxx);
    }
  }


  private static void decompress(File file, String name) throws IOException {
    Metadata metadata = readMetadata(file);
    String model = metadata.getStringValue("model"); // cameraObject/modelName/model
    int width = metadata.getIntValue("width"); // imageProperties/canonRawProperties/sensor/width
    int height = metadata.getIntValue("height"); // imageProperties/canonRawProperties/sensor/height
    int decodeTableNumber = metadata.getIntValue("decodeTable-1"); // imageProperties/canonRawProperties/decodeTable/decodeTable-1 (table number: 0..2)
    ImageInputStream in = new FileImageInputStream(file);

//    File newFile = new File(file.getParentFile(), name + ".dmp");
//    ImageOutputStream out = new FileImageOutputStream(newFile);
    System.err.print("decompressing...");
//    CrwDecompressor.decompress(in, decodeTableNumber, width, height, out);
    BufferedImage result = CrwDecompressor.decompress(in, decodeTableNumber, width, height);

    System.err.print("writing...");

//    String suffix = "png";
    String suffix = "tiff";
    Iterator writers = ImageIO.getImageWritersBySuffix(suffix);
    javax.imageio.ImageWriter writer = (javax.imageio.ImageWriter) writers.next();

    File newFile = new File(file.getParentFile(), name + "." + suffix);
    ImageOutputStream out = new FileImageOutputStream(newFile);

    writer.setOutput(out);
    writer.write(result);
    out.close();
    writer.dispose();

    System.err.println("done!");
  }
}
