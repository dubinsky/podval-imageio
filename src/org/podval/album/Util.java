package org.podval.album;

import java.util.Iterator;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import javax.imageio.ImageWriteParam;

import java.awt.image.RenderedImage;

import java.awt.RenderingHints;

import javax.media.jai.JAI;
import javax.media.jai.Interpolation;
import javax.media.jai.BorderExtender;

import javax.media.jai.operator.ScaleDescriptor;


public class Util {

  public static RenderedImage readImage(File file)
    throws IOException
  {
    RenderedImage result = null;

    ImageInputStream in = ImageIO.createImageInputStream(file);
    try {
      ImageReader reader = getImageReader(in);
      if (reader != null) {
        try {
          result = reader.read(0);
        } finally {
          reader.dispose();
        }
      } else
        throw new IOException("No reader for " + file);
    } finally {
      in.close();
    }

    return result;
  }


  public static RenderedImage readThumbnail(File file, int number)
    throws IOException
  {
    RenderedImage result = null;

    ImageInputStream in = ImageIO.createImageInputStream(file);
    try {
      ImageReader reader = getImageReader(in);
      if (reader != null) {
        try {
          result = reader.readThumbnail(0, number);
        } finally {
          reader.dispose();
        }
      } else
        throw new IOException("No reader for " + file);
    } finally {
      in.close();
    }

    return result;
  }


  private static ImageReader getImageReader(ImageInputStream in)
    throws IOException
  {
    ImageReader result = null;

    Iterator readers = ImageIO.getImageReaders(in);
    if (readers.hasNext()) {
      result = (ImageReader) readers.next();
      result.setInput(in, false, true);
    };

    return result;
  }


  public static void writeImage(RenderedImage result, File file)
    throws IOException
  {
    Iterator writers = ImageIO.getImageWritersBySuffix("jpg");
    ImageWriter writer = (ImageWriter) writers.next();

    // If there are no readers for JPEGs, we crash!

    ImageWriteParam param = writer.getDefaultWriteParam();
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    param.setCompressionQuality(0.9824f);

    ImageOutputStream out = ImageIO.createImageOutputStream(file);
    writer.setOutput(out);
    writer.write(null, new IIOImage(result, null, null), param);
    out.close();
    writer.dispose();
  }


  public static RenderedImage scale(File file, int height, int width)
    throws IOException
  {
    RenderedImage image = readImage(file);

    float longScale  = ((float) Math.max(width, height)) / Math.max(image.getWidth(), image.getHeight());
    float shortScale = ((float) Math.min(width, height)) / Math.min(image.getWidth(), image.getHeight());
    float scale = Math.min(longScale, shortScale);

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


  public static RenderedImage convert(File file) {
    RenderedImage result = null;
    /** @todo implement CRW conversion. */
    return result;
  }
}
