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

import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.Interpolation;
import javax.media.jai.BorderExtender;

import javax.media.jai.operator.ScaleDescriptor;
import javax.media.jai.operator.TransposeDescriptor;
import javax.media.jai.operator.TransposeType;

import org.podval.imageio.Orientation;


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
    writer.setOutput(out);
    writer.write(result); /** @todo parameters can be used to specify 'quality'... */
    out.close();
    writer.dispose();
  }


  public static synchronized RenderedImage scale(File file, int height, int width) throws IOException {
    RenderedImage image = readImage(file);
    float longScale  = ((float) Math.max(width, height)) / Math.max(image.getWidth(), image.getHeight());
    float shortScale = ((float) Math.min(width, height)) / Math.min(image.getWidth(), image.getHeight());
    float scale = Math.min(longScale, shortScale);
    RenderedImage result = scaleImage(image, scale);
    return result;
  }


  private static RenderedImage scaleImage(RenderedImage image, float scale) {
    return ScaleDescriptor.create(
      image,
      new Float(scale),
      new Float(scale),
      new Float(0),
      new Float(0),
      Interpolation.getInstance(Interpolation.INTERP_BICUBIC_2),
      RENDERING_HINTS
    );
  }


  private static final RenderingHints RENDERING_HINTS =
    new RenderingHints(JAI.KEY_BORDER_EXTENDER, BorderExtender.createInstance(BorderExtender.BORDER_COPY));


  public static RenderedImage rotate(RenderedImage image, Orientation orientation) {
    RenderedImage result = image;

    if (orientation.isFlipAroundHorizontal())
      result = TransposeDescriptor.create(result, TransposeDescriptor.FLIP_VERTICAL, null);

    TransposeType rotation = null;
    if (orientation.getRotation() == Orientation.Rotation.LEFT)
      rotation = TransposeDescriptor.ROTATE_270;
    else
    if (orientation.getRotation() == Orientation.Rotation.OVER)
      rotation = TransposeDescriptor.ROTATE_180;
    else
    if (orientation.getRotation() == Orientation.Rotation.RIGHT)
      rotation = TransposeDescriptor.ROTATE_90;

    if (rotation != null)
      result = TransposeDescriptor.create(result, rotation, null);

    return result;
  }


  public static RenderedImage convert(File file) {
    RenderedImage result = null;
    /** @todo XXXXX */
    return result;
  }
}
