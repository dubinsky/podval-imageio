package org.podval.album;

import java.util.Date;

import java.io.File;
import java.io.IOException;

import java.awt.image.RenderedImage;

import java.lang.ref.SoftReference;

import org.podval.imageio.Metadata;


/**
 */

public class PictureLocal extends Picture {

  public PictureLocal(Album album, String name) {
    super(album, name);
  }


  public void addFile(File file, String name, String extension) throws IOException {
    if (!getName().equals(name))
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
    return (getOriginalFile() != null);
  }


  public File getThumbnailFile() throws IOException {
    if (thumbnailFile == null) thumbnailFile = getGeneratedFile("120x160");

    File result = thumbnailFile;
    if (!result.exists()) {
      RenderedImage image = null;

      if (thmFile != null) image = Util.readImage(thmFile); else
      if (crwFile != null) image = Util.readThumbnail(crwFile, 0);

      if (image == null) image = scale(120, 160);

      Util.writeImage(image, result);
    }

    return result;
  }


  public File getScreensizedFile() throws IOException {
    if (screensizedFile == null) screensizedFile = getGeneratedFile("480x640");

    File result = screensizedFile;
    if (!result.exists()) {
      RenderedImage image = null;

      if (crwFile != null) image = Util.readThumbnail(crwFile, 1);

      if (image == null) image = scale(480, 640);

      Util.writeImage(image, result);
    }

    return result;
  }


  public File getFullsizedFile() throws IOException {
    File result = jpgFile;
    if (result == null) {
      if (convertedFile == null) convertedFile = getGeneratedFile("converted");
      result = convertedFile;
      if (!result.exists()) {
        Util.writeImage(Util.convert(crwFile), result);
      }
    }

    return result;
  }


  private File getGeneratedFile(String modifier) {
    return new File(getAlbum().getGeneratedDirectory(), getName()+"-"+modifier+".jpg");
  }


  private RenderedImage scale(int height, int width) throws IOException {
    return Util.scale(getFullsizedFile(), height, width);
  }


  private File getOriginalFile() {
    File result = null;

    if (jpgFile != null) result = jpgFile; else
    if (crwFile != null) result = crwFile;

    return result;
  }


  public Date getDateTime() {
    try {
      return new Date(getOriginalFile().lastModified());
    } catch (Exception e) {
    }
/////    return (Date) getCameraMetadata().find("dateTime");
    return null;
  }


  private Metadata getCameraMetadata() throws IOException {
    if ((cameraMetadata == null) || (cameraMetadata.get() == null)) {
      cameraMetadata = new SoftReference(Metadata.read(getOriginalFile()));
    }
    return (Metadata) cameraMetadata.get();
  }


  private File jpgFile;


  private File thmFile;


  private File crwFile;


  private File thumbnailFile;


  private File screensizedFile;


  private File convertedFile;


  private SoftReference cameraMetadata;
}
