/*
 * @(#)$Id$
 * Copyright 2003 Leonid Dubinsky
 */

package org.podval.album;

import java.util.Date;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.image.RenderedImage;

import java.lang.ref.SoftReference;

import javax.xml.bind.JAXBException;

import org.podval.imageio.Metadata;
import org.podval.imageio.Orientation;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;


/**
 * Picture whose files are in its album's directory.
 *
 * @author $Author$
 * @version $Revision$, $Date$
 */
public class PictureLocal extends Picture {


  private static final Log LOG = LogFactory.getLog(Picture.class);


  public PictureLocal(String name) {
    super(name);
  }


  /**
   * Absorbs a file into this picture - if it is of recognized type.
   *
   * @param file
   *   file to be absorbed
   *
   * @param extension
   *   string containing the file's extension
   *
   * @throws IOException
   *   when file name is duplicate if considered ignoring case
   */
  public void addFile(File file, String extension) throws IOException {
    if (extension.equalsIgnoreCase("jpg")) {
      checkIsNull(jpgFile);
      jpgFile = file;
    } else

    if (extension.equalsIgnoreCase("crw")) {
      checkIsNull(crwFile);
      crwFile = file;
    } else

    if (extension.equalsIgnoreCase("thm")) {
      checkIsNull(thmFile);
      thmFile = file;
    } else

    ;
  }


  private void checkIsNull(File file) throws IOException {
    if (file != null)
      throw new IOException("Duplicate case-sensitive file name " + file);
  }


  /**
   * Indicates that this picture has camera original files, and is a real picture.
   *
   * @return
   *   boolean indicating that this picture has camera original files,
   *   and is a real picture
   */
  public boolean isPicture() {
    return (getOriginalFile() != null);
  }


  protected String getDefaultTitle() {
    return getName();
  }


  public File getThumbnailFile() throws IOException {
    File result = getThumbnailGnereatedFile();
    if (!result.exists())
      scale(readCameraThumbnail(), 120, 160, result);
    return result;
  }


  public File getScreensizedFile() throws IOException {
    File result = getScreensizedGeneratedFile();
    if (!result.exists())
      scale(readCameraScreensized(), 480, 640, result);
    return result;
  }


  public File getFullsizedFile() throws IOException {
    /** @todo logging */
    File result = null;

    if ((jpgFile != null) && (getOrientation() == Orientation.TOP_LEFT)) {
      result = jpgFile;

    } else {
      result = getFullsizedGeneratedFile();

      if (!result.exists()) {
        RenderedImage image = readCameraFullsized();
        /* Theoretically it is possible to losslesly rotate JPEG,
           but if this will become a problem, I'll just switch to using
           some other format for generated files - format that supports
           lossless compression.
         */
        Util.writeImage(Util.rotate(image, getOrientation()), result);
      }
    }

    return result;
  }


  private File getOriginalFile() {
    File result = null;

    if (jpgFile != null) result = jpgFile; else
    if (crwFile != null) result = crwFile;

    return result;
  }


  private RenderedImage readCameraThumbnail() throws IOException {
    RenderedImage result = null;

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraThumbnail()");

    if (thmFile != null) result = Util.readImage    (thmFile   ); else
    if (crwFile != null) result = Util.readThumbnail(crwFile, 0);

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraThumbnail() returning " + result);

    return result;
  }


  private RenderedImage readCameraScreensized() throws IOException {
    RenderedImage result = null;

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraScreensized()");

    if (crwFile != null) result = Util.readThumbnail(crwFile, 1);

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraScreensized() returning " + result);

    return result;
  }


  private RenderedImage readCameraFullsized() throws IOException {
    RenderedImage result = null;

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraFullsized()");

    if (jpgFile != null) result = Util.readImage(jpgFile); else
    if (crwFile != null) result = Util.convert  (crwFile);

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".readCameraFullsized() returning " + result);

    return result;
  }


  private void scale(RenderedImage image, int height, int width, File result)
    throws IOException
  {
    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".scale()");

    if (image != null)
      image = Util.rotate(image, getOrientation());
    else
      image = Util.scale(getFullsizedFile(), height, width);

    Util.writeImage(image, result);

    if (LOG.isDebugEnabled())
      LOG.debug(getName() + ".scale() wrote file of length " + result.length());
  }


  private File getThumbnailGnereatedFile() {
    if (thumbnailFile == null)
      thumbnailFile = getGeneratedFile("120x160");
    return thumbnailFile;
  }


  private File getScreensizedGeneratedFile() {
    if (screensizedFile == null)
      screensizedFile = getGeneratedFile("480x640");
    return screensizedFile;
  }


  private File getFullsizedGeneratedFile() {
    if (originalFile == null)
      originalFile = getGeneratedFile("original");
    return originalFile;
  }


  private File getGeneratedFile(String modifier) {
    return new File(getAlbum().getGeneratedDirectory(), getName()+"-"+modifier+".jpg");
  }


  public Date getDateTime() {
    load();
    if (dateTime == null) {
      dateTime = (Date) getCameraMetadata("dateTime");
      if (dateTime == null)
        dateTime = new Date(getOriginalFile().lastModified());
    }
    return dateTime;
  }


  public Orientation getOrientation() {
    load();
    if (orientation == null) {
      Orientation value = (Orientation) getCameraMetadata("orientation");
      if (value == null)
        value = Orientation.TOP_LEFT;
      setOrientation(value);
    }
    return orientation;
  }


  private void setOrientation(Orientation value) {
    if (orientation != value) {
      orientation = value;
      removeGeneratedFiles();
      changed();
    }
  }


  public void rotateLeft() {
    setOrientation(getOrientation().rotateLeft());
  }


  public void rotateRight() {
    setOrientation(getOrientation().rotateRight());
  }


  private void removeGeneratedFiles() {
    deleteFile(getThumbnailGnereatedFile());
    deleteFile(getScreensizedGeneratedFile());
    deleteFile(getFullsizedGeneratedFile());
  }


  private void deleteFile(File file) {
    if(file.exists() && !file.delete())
      LOG.warn("Can not delete file " + file);
  }


  protected void load() {
    if (!changed) {
      /* If I do not cache it, a File object is created every time.
         If I cache it, and metadata file gets created on the side, it
         will not be detected... */
      if (metadataReadFile == null)
        metadataReadFile = getAlbum().getMetadataReadFile(getMetadataFileName());

      if (metadataReadFile != null) {
        long lastModified = metadataReadFile.lastModified();
        if (loaded < lastModified) {
          loaded = lastModified;
          load(metadataReadFile);
          changed = false;
        }
      }
    }
  }


  public void save() {
    if (changed) {
      File file = getAlbum().getMetadataWriteFile(getMetadataFileName());
      if (file != null) {
        save(file);
        metadataReadFile = file;
        loaded = file.lastModified();
        changed = false;
      }
    }
  }


  private void load(File file) {
    try {
      load(JAXB.unmarshallPicture(file));
    } catch (JAXBException e) {
      LOG.warn("Exception when unmarshalling picture metadata from file " + file, e);
    }
  }


  private void save(File file) {
    try {
      org.podval.album.jaxb.Picture result = JAXB.getObjectFactory().createPicture();
      save(result);
      JAXB.marshall(result, file);
    } catch (Exception e) {
      LOG.warn("Exception when marshalling picture metadata to file " + file, e);
    }
  }


  private void load(org.podval.album.jaxb.Picture xml) {
    title = xml.getTitle();
    orientation = Orientation.get(xml.getOrientation());
  }


  private void save(org.podval.album.jaxb.Picture xml) {
    if (title != null) xml.setTitle(title);
    xml.setOrientation(getOrientation().toString());
  }


  private String getMetadataFileName() {
    return getName() + ".xml";
  }


  private Object getCameraMetadata(String name) {
    if ((cameraMetadata == null) || (cameraMetadata.get() == null)) {
      Metadata result = null;
      File file = getOriginalFile();
      try {
        result = Metadata.read(file);
      } catch (IOException e) {
        LOG.warn("Exception when reading camera metadata from file " + file, e);
      }
      cameraMetadata = new SoftReference(result);
    }
    return ((Metadata) cameraMetadata.get()).find(name);
  }


  private Date dateTime;


  private Orientation orientation;


  private File jpgFile;


  private File thmFile;


  private File crwFile;


  private File thumbnailFile;


  private File screensizedFile;


  private File originalFile;


  private File metadataReadFile;


  private long loaded = 0;


  private SoftReference cameraMetadata;
}
