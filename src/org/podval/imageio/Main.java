package org.podval.imageio;

import javax.imageio.spi.IIORegistry;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.metadata.IIOMetadata;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.awt.image.RenderedImage;
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
    /** @todo should register using jar manifest (also). */
    IIORegistry.getDefaultInstance().registerServiceProvider(new
      CiffImageReaderSpi());

    /** @todo where is the right place for this? */
    MetaMetadata.load();


    String command = "number";

    for (int i=0; i<args.length; i++) {
      String arg = args[i];
      if (arg.startsWith("-")) {
        arg = arg.substring(1, arg.length());
        command = arg;
      } else
        doCommand(command, new File(arg));
    }
  }


  private static void usage() {
    System.err.println("Usage: <command> <file>+");
    System.err.println();
    System.err.println("Recognized commands:");
    System.err.println("  number     - print identification");
    System.err.println("  decompress - extract raw image out of a .crw");
    System.err.println("  metadata   - print native metadata");
    System.err.println("  thumbnail  - extract thumbnail out of the file");
  }


  private static void doCommand(String command, File file) throws
    IOException,
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    System.err.print("File " + file + ": ");

    if (!file.exists() || !file.isFile()) {
      System.err.print(" does not exist or is not a file.");
    } else {

      String fullName = file.getName();
      int dot = fullName.lastIndexOf('.');
      String name;
      String extension;

      if (dot!=-1) {
        name = fullName.substring(0, dot);
        extension = fullName.substring(dot+1, fullName.length());
      } else {
        name = fullName;
        extension = null;
      }

      ImageInputStream in = ImageIO.createImageInputStream(file);
      Iterator readers = ImageIO.getImageReaders(in);
      ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

      if (reader == null) {
        System.err.print(" no reader found.");
      } else {
        if (readers.hasNext())
          System.err.print(" more readers!");

        reader.setInput(in);

        if ("number"    .equals(command)) number    (reader, file, name); else
        if ("decompress".equals(command)) decompress(reader, file, name); else
        if ("metadata"  .equals(command)) metadata  (reader); else
        if ("thumbnail" .equals(command)) thumbnail (reader, file, name); else
          usage();

        reader.dispose();
      }

      in.close();
    }

    System.err.println();
  }



  public static Metadata readMetadata(ImageReader reader) throws IOException {
    IIOMetadata result = reader.getImageMetadata(0);

    /** @todo this should be done through a transcoder? */
    if (result instanceof JPEGMetadata)
      result = ExifReader.transcodeJpegMetadata(result);

    return (Metadata) result;
  }




  private static void number(ImageReader reader, File file, String name)
    throws IOException
  {
    Metadata metadata = readMetadata(reader);
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
    }
  }


  private static void decompress(ImageReader reader, File file, String name)
    throws IOException
  {
    Metadata metadata = readMetadata(reader);
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

    System.err.print("bilinearily interpolating...");
    Demosaicker.bilinear(result.getRaster());

    System.err.print("writing...");

    write(result, file, name, "tiff");

    System.err.print("done!");
  }



  private static void metadata(ImageReader reader) throws
    IOException,
    javax.xml.transform.TransformerException
  {
    System.err.println();
    readMetadata(reader).print();
  };


  private static void thumbnail(ImageReader reader, File file, String name)
    throws IOException
  {
    RenderedImage result = null;

    int numImages = reader.getNumImages(true);
    int numThumbnails = reader.getNumThumbnails(0);
    System.err.print(" has " + numImages + " images, 0th with " + numThumbnails + " thumbnails");
    if (reader.getNumThumbnails(0) == 0)
      result = reader.read(0);
    else
      result = reader.readThumbnail(0, 0);

    write(result, file, name, "jpeg");
  }


  private static void write(RenderedImage result, File file, String name, String suffix)
    throws IOException
  {
    Iterator writers = ImageIO.getImageWritersBySuffix(suffix);
    ImageWriter writer = (javax.imageio.ImageWriter) writers.next();

    File newFile = new File(file.getParentFile(), name + "." + suffix);
    ImageOutputStream out = new FileImageOutputStream(newFile);

    writer.setOutput(out);
    writer.write(result);
    out.close();
    writer.dispose();
  }
}
