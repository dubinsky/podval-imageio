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
import java.util.Date;


public class Main {

  public static void main(String[] args) throws
    IOException,
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    if (args.length<2) {
      usage();
      return;
    }

    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new
      CiffImageReaderSpi());

    /** @todo where is the right place for this? */
    MetaMetadata.load();

    String command = args[0];

    for (int i=1; i<args.length; i++)
      doCommand(command, new File(args[i]));
  }


  private static void usage() {
    System.err.println("Usage: <command> <file>+");
    System.err.println();
    System.err.println("Recognized commands:");
    System.err.println("  dump - print native metadata");
    System.err.println("  number - print identification");
    System.err.println("  decompress - extract raw image out of a .crw");
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
      printNumber(file, name);
    else if ("decompress".equals(command))
      decompress(file, name);
    else if ("dump".equals(command)) {
      System.err.println();
      Metadata.read(file).print();
    } else
      usage();
  }


  private static void printNumber(File file, String name) throws IOException {
    Metadata metadata = Metadata.read(file);
    if (metadata != null) {
      int number = metadata.getIntValue("serialNumber");
      Date imageDateTime = (Date) metadata.find("dateTime");
      long fileMillis = file.lastModified();

      int fileNumber = 0;
      try {
        fileNumber = Integer.parseInt(name);
      } catch (NumberFormatException e) {
      }
      boolean wrongNumber = (fileNumber != number);
      long imageMillis = imageDateTime.getTime();
      boolean wrongDate = Math.abs(imageMillis-fileMillis)>2*1000;

      System.out.print("# " + number + " @ " + imageDateTime);

      if (wrongNumber)
        System.out.print(" wrong number");
      if (wrongDate) {
        System.out.print(" wrong date: " + new Date(fileMillis));
        file.setLastModified(imageMillis);
        System.out.print(" -corrected");
      }

      System.out.println();
    }
  }


  private static void decompress(File file, String name) throws IOException {
    Metadata metadata = Metadata.read(file);
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
