package org.podval.album;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;

import java.text.SimpleDateFormat;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.Interpolation;
import javax.media.jai.BorderExtender;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import javax.imageio.metadata.IIOMetadata;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.lang.ref.SoftReference;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;

import org.podval.imageio.Metadata;
import org.podval.imageio.ExifReader;


/**
 */

public class Picture {

  public Picture(String name, File generatedDirectory) {
    this.name = name;
    this.thumbnailFile = getGeneratedFile(generatedDirectory, "120x160");
    this.screensizedFile = getGeneratedFile(generatedDirectory, "480x640");
    this.convertedFile = getGeneratedFile(generatedDirectory, "converted");
  }



  private File getGeneratedFile(File generatedDirectory, String modifier) {
    return new File(generatedDirectory, name+"-"+modifier+".jpg");
  }


  public String getName() {
    return name;
  }


  public String getTimeString() {
    return "Timestamp reading not yet implemented.";
  }


  public void addFile(File file, String name, String extension) throws IOException {
    if (!this.name.equals(name))
      throw new IOException("Duplicate case-sensitive file name " + name);

    if (extension.equalsIgnoreCase("jpg")) {
      if (jpgFile != null)
        throw new IOException("Duplicate case-sensitive extension " + extension);
      jpgFile = file;
    } else

    if (extension.equalsIgnoreCase("crw")) {
      if (crwFile != null)
        throw new IOException("Duplicate case-sensitive extension " + extension);
      crwFile = file;
    } else

    if (extension.equalsIgnoreCase("thm")) {
      if (thmFile != null)
        throw new IOException("Duplicate case-sensitive extension " + extension);
      thmFile = file;
    } else

    ;
  }


  public boolean isPicture() {
    return (crwFile != null) || (jpgFile != null);
  }


  public File getThumbnailFile() throws IOException {
    File result = thumbnailFile;
    if (!result.exists()) {
      RenderedImage image = null;

      if (thmFile != null) image = readImage(thmFile); else
      if (crwFile != null) image = readThumbnail(crwFile, 0);

      if (image == null) image = scale(120, 160);

      writeImage(image, result);
    }

    return result;
  }


  public File getScreensizedFile() throws IOException {
    File result = screensizedFile;
    if (!result.exists()) {
      RenderedImage image = null;

      if (crwFile != null) image = readThumbnail(crwFile, 1);

      if (image == null) image = scale(480, 640);

      writeImage(image, result);
    }

    return result;
  }


  public File getFullsizedFile() throws IOException {
    File result = jpgFile;
    if (result == null) {
      result = convertedFile;
      if (!result.exists()) {
        if (crwFile == null)
          throw new NullPointerException("No JPEG or RAW file!");
        writeImage(convert(crwFile), result);
      }
    }

    return result;
  }


  private static final SimpleDateFormat dateFormat =
    new SimpleDateFormat("M/d/y HH:mm:ss");


  public String getDateTimeString() throws IOException {
    return dateFormat.format(getDateTime());
  }


  private Date getDateTime() throws IOException {
    return (Date) getMetadata().find("dateTime");
  }


  private Metadata getMetadata() throws IOException {
    if ((metadata == null) || (metadata.get() == null)) {
      metadata = new SoftReference(readMetadata(getMetadataFile()));
    }
    return (Metadata) metadata.get();
  }


  private File getMetadataFile() {
    File result = jpgFile;
    if (!result.exists()) result = crwFile;
    if (!result.exists()) result = thmFile;
    return result;
  }


  private static IIOMetadata readMetadata(File file) throws IOException {
    IIOMetadata result = null;

    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in);
      result = reader.getImageMetadata(0);
      reader.dispose();

      /** @todo this should be done through a transcoder? */
      if (result instanceof JPEGMetadata)
        result = ExifReader.transcodeJpegMetadata(result);
    }

    in.close();
    return (Metadata) result;
  }


  private RenderedImage scale(int height, int width) throws IOException {
    return scale(getFullsizedFile(), height, width);
  }


  private static synchronized RenderedImage scale(File file, int height, int width) throws IOException {
    RenderedImage image = readImage(file);
    float xscale = (float) width / image.getWidth ();
    float yscale = (float) height / image.getHeight();
    float scale = Math.min(xscale, yscale);
    Context.log("*** Scaling " + file);
    RenderedImage result = scaleImage(image, scale);
    return result;
  }


  private static RenderedImage scaleImage(RenderedImage image, float scale) {
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(image);
    pb.add(scale);
    pb.add(scale);
    pb.add((float) -image.getMinX());
    pb.add((float) -image.getMinY());
    pb.add(Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2));

    return JAI.create("scale", pb, renderingHints);
  }


  private static final RenderingHints renderingHints =
    new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));


  private static synchronized BufferedImage readImage(File file) throws IOException {
    BufferedImage result = null;
    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in, false, true);
      result = reader.read(0);
      reader.dispose();
    }
    in.close();

    return result;
  }


  private static void writeImage(RenderedImage result, File file) throws IOException {
    Iterator writers = ImageIO.getImageWritersBySuffix("jpg");
    ImageWriter writer = (ImageWriter) writers.next();
    ImageOutputStream out = ImageIO.createImageOutputStream(file);
    writer.setOutput(out);
    writer.write(result); /** @todo parameters can be used to specify 'quality'... */
    out.close();
    writer.dispose();
  }


  private static synchronized BufferedImage readThumbnail(File file, int number) throws IOException {
    BufferedImage result = null;
    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in, false, true);
      result = reader.readThumbnail(0, number);
      reader.dispose();
    }
    in.close();

    return result;
  }


  private static BufferedImage convert(File file) {
    BufferedImage result = null;
    /** @todo XXXXX */
    return result;
  }


  private final String name;


  private File jpgFile;


  private File thmFile;


  private File crwFile;


  private final File thumbnailFile;


  private final File screensizedFile;


  private final File convertedFile;


  private SoftReference metadata;
}
