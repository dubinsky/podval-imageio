package org.podval.album;

import java.util.Iterator;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import javax.imageio.metadata.IIOMetadata;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;

import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.Interpolation;
import javax.media.jai.BorderExtender;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;

import org.podval.imageio.Metadata;
import org.podval.imageio.ExifReader;


public class Util {

  public static synchronized RenderedImage readImage(File file) throws IOException {
    RenderedImage result = null;
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


  public static synchronized RenderedImage readThumbnail(File file, int number) throws IOException {
    RenderedImage result = null;
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


  public static void writeImage(RenderedImage result, File file) throws IOException {
    Iterator writers = ImageIO.getImageWritersBySuffix("jpg");
    ImageWriter writer = (ImageWriter) writers.next();
    /** @todo  */

    ImageOutputStream out = ImageIO.createImageOutputStream(file);
    /** @todo crashes with "output has not been set!" */
    writer.setOutput(out);
    writer.write(result); /** @todo parameters can be used to specify 'quality'... */
    out.close();
    writer.dispose();
  }


  public static synchronized RenderedImage scale(File file, int height, int width) throws IOException {
    RenderedImage image = readImage(file);
    float xscale = (float) width / image.getWidth ();
    float yscale = (float) height / image.getHeight();
    float scale = Math.min(xscale, yscale);
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


  public static RenderedImage convert(File file) {
    RenderedImage result = null;
    /** @todo XXXXX */
    return result;
  }
}
