/* $Id$ */

package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import java.util.Iterator;


public final class Cli {

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      throw new Exception("Too few arguments!");
    }

    String command = args[0];

    if (command.equals("date")) {
//      "serialNumber"
//      "dateTime" (within 2 seconds)
    } else

    if (command.equals("thumbnails")) {
      for (int i = 1; i<args.length; i++) {
        extractThumbnails(args[i]);
      }
    } else

    if (command.equals("decompress")) {
      for (int i = 1; i<args.length; i++) {
        String path = args[i];
        if (path.endsWith(".crw")) {
          decompress(path);
        }
      }
    } else

    if (command.equals("dump")) {
      for (int i = 1; i<args.length; i++) {
        dump(args[i]);
      }

    } else {

      throw new Exception("Unknown command " + command + " not one of date, thumbnails, decompress, dump");
    }
  }


  private static void extractThumbnails(String path) throws IOException {
    String name = getPathName(path);

    CrwThumbnailExtractor.extract(
      new File(name + getPathExtension(path)),
      new File(name + ".thumb.jpg"),
      new File(name + ".proof.jpg")
    );
  }


  private static void dump(String path) throws Exception {
    boolean crw = path.endsWith(".crw");

    ImageInputStream is = new FileImageInputStream(new File(path));

    SaxDumpingHandler.dump(
      crw ? new CiffReader() : new ExifReader(),
      crw ? is : ExifStream.fromJpegStream(is),
      MetaMetaData.get(crw ? "ciff" : "exif"),
      System.out
    );
  }


  private static void decompress(String path) throws IOException {
    System.err.print("decompressing...");
    BufferedImage result = CrwDecompressor.decompress(path);
//
//      System.err.print("bilinearily interpolating...");
//      Demosaicker.bilinear(result.getRaster());
//
//      System.err.print("writing...");
//      write(result, file, name, "tiff");

    System.err.print("done!");
  }


//  private static void write(BufferedImage result, File file, String name, String suffix)
//    throws IOException
//  {
//    Iterator writers = ImageIO.getImageWritersBySuffix(suffix);
//    ImageWriter writer = (ImageWriter) writers.next();
//
//    File newFile = new File(file.getParentFile(), name + "." + suffix);
//    ImageOutputStream out = ImageIO.createImageOutputStream(newFile);
//
//    writer.setOutput(out);
//    writer.write(result);
//    out.close();
//    writer.dispose();
//  }


  private static String getPathName(String path) {
    int dot = path.lastIndexOf(".");
    return (dot == -1) ? path : path.substring(0, dot);
  }


  private static String getPathExtension(String path) {
    int dot = path.lastIndexOf(".");
    return (dot == -1) ? "" : path.substring(dot);
  }


  private Cli() {
  }
}
