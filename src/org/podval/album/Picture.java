package org.podval.album;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Iterator;

import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;

import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.BorderExtender;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import java.lang.ref.SoftReference;



/**
 */

public class Picture {

  public Picture(String name, File originalsDirectory, File generatedDirectory) {
    this.name = name;
    this.originalsDirectory = originalsDirectory;
    this.generatedDirectory = generatedDirectory;
  }



  public String getName() {
    return name;
  }


  public void addFile(File file, String name, String modifier,
    String extension) throws IOException {
    if (!modifier.equals(""))
      throw new IllegalArgumentException(
        "Modifiers in the originals directory - are they a good idea?");

    addFile(file, name, extension);
  }



  private void addFile(File file, String name,
    String extension) throws IOException {
    if (!this.name.equals(name))
      throw new IOException("Duplicate case-sensitive file name " + name);

    if (files.get(extension) != null)
      throw new IOException("Duplicate case-sensitive extension " + extension);

    files.put(extension, file);
  }


  private BufferedImage getFullsized() throws IOException {
    BufferedImage result = null;

    if (fullsizedReference != null)
      result = (BufferedImage) fullsizedReference.get();

    if (result == null) {
      File file;
      if ((file = getFile("jpg")) != null) result = readImage(file); else
      if ((file = getFile("crw")) != null) {
        File convertedFile = new File(generatedDirectory, name+".jpg");
        if (convertedFile.exists())
          result = readImage(convertedFile);
        else {
          result = convert(file);
          writeImage(result, convertedFile);
        }
      }
      fullsizedReference = new SoftReference(result);
    }

    /** @todo  */
    return result;
  }


  public RenderedImage getScaled(int width, int height) throws IOException {
    RenderedImage result = null;
    Scaling scaling = new Scaling(width, height);
    SoftReference reference = (SoftReference) scalings.get(scaling);

    if (reference != null) {
      result = (BufferedImage) reference.get();
    }

    if (result == null) {
      if ((width == 160) && (height == 120)) result = getCameraThumbnail(); else
      if ((width == 640) && (height == 480)) result = getCameraScreensized();

      if (result == null) {
        String modifier = height+"x"+width;
        File file = new File(generatedDirectory, name+"-"+modifier+".jpg");
        if (file.exists())
          result = readImage(file);
        else {
          result = scale(getFullsized(), width, height);
          writeImage(result, file);
        }
      }

      scalings.put(scaling, new SoftReference(result));
    }

    return result;
  }


  private BufferedImage getCameraThumbnail() throws IOException {
    BufferedImage result = null;

    File file;
    if ((file = getFile("thm")) != null) result = readImage(file); else
    if ((file = getFile("crw")) != null) result = readThumbnail(file, 0);

    return result;
  }



  private BufferedImage getCameraScreensized() throws IOException {
    BufferedImage result = null;

    File file;
    if ((file = getFile("crw")) != null) result = readThumbnail(file, 1);

    return result;
  }


  private RenderedImage scale(BufferedImage image, int width, int height) {
    float xscale = (float) width / image.getWidth ();
    float yscale = (float) height / image.getHeight();
    float scale = Math.min(xscale, yscale);
    return scaleImage(image, scale);
  }


  private RenderedImage scaleImage(BufferedImage image, float scale) {
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


  private BufferedImage readImage(File file) throws IOException {
    BufferedImage result = null;
    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in);
      result = reader.read(0);
      reader.dispose();
    }
    in.close();
    return result;
  }


  private void writeImage(RenderedImage result, File file) throws IOException {
    Iterator writers = ImageIO.getImageWritersBySuffix("jpg");
    ImageWriter writer = (ImageWriter) writers.next();
    ImageOutputStream out = ImageIO.createImageOutputStream(file);
    writer.setOutput(out);
    writer.write(result);
    out.close();
    writer.dispose();
  }


  private BufferedImage readThumbnail(File file, int number) throws IOException {
    BufferedImage result = null;
    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in);
      result = reader.readThumbnail(0, number);
      reader.dispose();
    }
    in.close();
    return result;
  }


  private BufferedImage convert(File file) {
    BufferedImage result = null;
    /** @todo XXXXX */
    return result;
  }


  private File getFile(String extension) {
    return (File) files.get(extension);
  }



  public String toString() {
    boolean first = true;
    String result = name + " [";
    for (Iterator extensions = files.keySet().iterator(); extensions.hasNext();) {
      String extension = (String) extensions.next();
      if (!first)
        result += ", ";
      result += extension;
      first = false;
    }
    result += "]";
    return result;
  }


  private final String name;


  private final File originalsDirectory;


  private final File generatedDirectory;


  /**
   * map<String extension, File>
   */
  private final Map files = new TreeMap(String.CASE_INSENSITIVE_ORDER);


  private SoftReference fullsizedReference;


  /**
   *  map<Scaling, BufferedImage>
   */
  private final Map scalings = new HashMap();



  /**
   *
   */
  private static class Scaling {

    public Scaling(int width, int height) {
      this.width = width;
      this.height = height;
    }


    public int hashCode() {
      return (width*57314 + height);
    }


    public boolean equals(Object o) {
      Scaling other = (Scaling) o;
      return (this.width == other.width) && (this.height == other.height);
    }


    private final int width;


    private final int height;
  }
}
