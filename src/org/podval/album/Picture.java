package org.podval.album;

import java.util.Map;
import java.util.TreeMap;
import java.util.Iterator;

import java.io.File;
import java.io.IOException;

import java.awt.image.RenderedImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 */

public class Picture {

  public Picture(String name, File originalsDirectory, File generatedDirectory) {
    this.name = name;
    this.originalsDirectory = originalsDirectory;
    this.generatedDirectory = generatedDirectory;
  }



  public void addFile(File file, String name, String modifier,
    String extension) throws IOException {
    if (!modifier.equals(""))
      throw new IllegalArgumentException(
        "Modifiers in the originals directory - are they a good idea?");
  }



  private void addFile(File file, String name,
    String extension) throws IOException {
    if (!this.name.equals(name))
      throw new IOException("Duplicate case-sensitive file name "+name);

    if (files.get(extension)!=null)
      throw new IOException("Duplicate case-sensitive extension "+extension);

    files.put(extension, file);
  }



  public RenderedImage getCameraThumbnail() throws IOException {
    RenderedImage result = null;

    File file = getFile("thm");
    if (file!=null)
      file = getFile("crw");

    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in);

      if (reader.getNumThumbnails(0) == 0)
        result = reader.read(0);
      else
        result = reader.readThumbnail(0, 0);

      reader.dispose();
    }
    in.close();

    return result;
  }



  public RenderedImage getMainImage() {
    return null; /** @todo XXXX */
  }



  private File getFile(String extension) {
    return (File) files.get(extension);
  }



  private final String name;


  private final File originalsDirectory;


  private final File generatedDirectory;


  /**
   * map<String extension, File>
   */
  private final Map files = new TreeMap(String.CASE_INSENSITIVE_ORDER);
}
