/*
 * @(#)$Id$
 * Copyright 2003 Leonid Dubinsky
 */

//-Xms32m -Xmx512m

package org.podval.album;

import java.util.Date;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.awt.image.RenderedImage;

import java.lang.ref.SoftReference;

import javax.xml.bind.JAXBException;

import org.podval.imageio.Metadata;
import org.podval.imageio.Orientation;


/**
 * Picture whose files are in its album's directory.
 *
 */
public class PictureLocal extends Picture {


  private static final Logger LOG = Logger.getLogger("org.podval.album.Picture");


  private void log(String when, Exception e) {
    LOG.log(Level.WARNING, "Exception when " + when, e);
  }


  private void log(String what) {
    LOG.log(Level.WARNING, what);
  }


  private void trace(String what) {
    LOG.log(Level.FINE, what);
  }


  public PictureLocal(String name) {
    super(name);
  }


  /**
   * Absorbs a file into this picture - if it is of recognized type.
   * When file name is duplicate if considered ignoring case, the file is ignored.
   *
   * @param file
   *   file to be absorbed
   *
   * @param extension
   *   string containing the file's extension
   */
  public synchronized void addFile(File file, String extension) {
    if (extension.equalsIgnoreCase("jpg")) jpgFile = addFile(jpgFile, file); else
    if (extension.equalsIgnoreCase("crw")) crwFile = addFile(crwFile, file); else
    if (extension.equalsIgnoreCase("thm")) thmFile = addFile(thmFile, file); else
    ;
  }


  private File addFile(File file, File value) {
    File result;

    if (file != null) {
      log("Duplicate case-sensitive file name " + file);
      result = file;
    } else
      result = value;

    return result;
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


  public File getThumbnailFile() {
    File result = getThumbnailGeneratedFile();
    if (!result.exists()) {
      synchronized (PictureLocal.class) {
        scale(readCameraThumbnail(), 120, 160, result);
      }
    }
    return ifExists(result);
  }


  public File getScreensizedFile() {
    File result = getScreensizedGeneratedFile();
    if (!result.exists()) {
      synchronized (PictureLocal.class) {
        scale(readCameraScreensized(), 480, 640, result);
      }
    }
    return ifExists(result);
  }


  private static File ifExists(File file) {
    return (file.exists()) ? file : null;
  }


  /** @todo review the naming: fullsized... original... camera... generated... */
  public File getFullsizedFile() {
    File result = null;

    if (getOrientation() == Orientation.TOP_LEFT)
      result = getStandardOriginalFile();

    if (result == null) {
      result = getFullsizedGeneratedFile();

      if (!result.exists()) {
        synchronized (PictureLocal.class) {
          RenderedImage image = readCameraFullsized();
          /** @todo this can not really be null, and resulting file must exist -
           * once I do crw conversion... */
          /* Theoretically it is possible to losslesly rotate JPEG,
             but if this will become a problem, I'll just switch to using
             some other format for generated files - format that supports
             lossless compression.
           */
          if (image != null) {
            image = Util.rotate(image, getOrientation());
            writeImage(image, result);
          }
        }
        result = ifExists(result);
      }
    }

    if (result == null)
      trace("*** Fullsized file is null for " + getName());

    return result;
  }


  private File getOriginalFile() {
    File result = null;

    if (jpgFile != null) result = jpgFile; else
    if (crwFile != null) result = crwFile;

    return result;
  }


  private File getStandardOriginalFile() {
    File result = null;

    if (jpgFile != null) result = jpgFile;

    return result;
  }


  private RenderedImage readCameraThumbnail() {
    RenderedImage result = null;

    try {
      if (thmFile != null) result = Util.readImage    (thmFile   ); else
      if (crwFile != null) result = Util.readThumbnail(crwFile, 0);
    } catch (IOException e) {
      log("reading camera thumbnail", e);
    }

    return result;
  }


  private RenderedImage readCameraScreensized() {
    RenderedImage result = null;

    try {
      if (crwFile != null) result = Util.readThumbnail(crwFile, 1);
    } catch (IOException e) {
      log("reading camera screensized", e);
    }

    return result;
  }


  private RenderedImage readCameraFullsized() {
    RenderedImage result = null;

    try {
      if (jpgFile != null) result = Util.readImage(jpgFile); else
      if (crwFile != null) result = Util.convert  (crwFile);
    } catch (IOException e) {
      log("reading camera fullsized", e);
    }

    return result;
  }


  private void scale(RenderedImage image, int height, int width, File result) {
    if (image != null)
      image = Util.rotate(image, getOrientation());
    else
      image = scale(height, width);

    writeImage(image, result);
  }


  private RenderedImage scale(int height, int width) {
    RenderedImage result = null;

    try {
      File file = getFullsizedFile();
      if (file != null)
        result = Util.scale(file, height, width);
    } catch (IOException e) {
      log("scaling image", e);
    }

    return result;
  }


  private void writeImage(RenderedImage image, File file) {
    if (image != null) {
      try {
        Util.writeImage(image, file);
//      } catch (IOException e) {
//        log("writing image", e);
//      }
      } catch (Throwable t) {
        /** @todo OutOfMemory is thrown here!
         * Introduce global exception handler?
         */

        trace("***** Writing " + file + " " + t);
      }
    }
  }


  private File getThumbnailGeneratedFile() {
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


  private synchronized void setOrientation(Orientation value) {
    if (orientation != value) {
      orientation = value;
      deleteGeneratedFiles();
      changed();
    }
  }


  public void rotateLeft() {
    setOrientation(getOrientation().rotateLeft());
  }


  public void rotateRight() {
    setOrientation(getOrientation().rotateRight());
  }


  private void deleteGeneratedFiles() {
    deleteFile(getThumbnailGeneratedFile());
    deleteFile(getScreensizedGeneratedFile());
    deleteFile(getFullsizedGeneratedFile());
  }


  private void deleteFile(File file) {
    if(file.exists() && !file.delete())
      log("Can not delete file " + file);
  }


  protected synchronized void load() {
    if (!changed) {
      File metadataWriteFile = null;

      /* If I do not cache it, a File object is created every time.
         If I cache it, and metadata file gets created on the side, it
         will not be detected... */
      if (metadataFile == null) {
        File separate =
          new File(getAlbum().getMetadataDirectory(), getMetadataFileName());
        File withOriginals =
          new File(getAlbum().getDirectory()        , getMetadataFileName());

        if (separate.exists()) {
          metadataFile = separate;
          metadataWriteFile = separate;
        } else
        if (withOriginals.exists()) {
          metadataFile = withOriginals;
          metadataWriteFile = (withOriginals.canWrite()) ? withOriginals : separate;
        } else {
          try {
            withOriginals.createNewFile();
          } catch (IOException e) {
          }
          metadataWriteFile = (withOriginals.canWrite()) ? withOriginals : separate;
        }
      }

      if (metadataFile != null) {
        long lastModified = metadataFile.lastModified();
        if (loaded < lastModified) {
          loaded = lastModified;
          load(metadataFile);
          changed = false;
        }
      }

      if ((metadataWriteFile != null) && (metadataWriteFile != metadataFile)) {
        metadataFile = metadataWriteFile;
        /** @todo can creation of a copy of a metadataReadFile be avoided? */
        changed = true;
        /** @todo some strange permission-related exceptions are thrown here! */
        save();
      }
    }
  }


  public synchronized void save() {
    if (changed) {
      if (metadataFile != null) {
        save(metadataFile);
        loaded = metadataFile.lastModified();
        changed = false;
      }
    }
  }


  private void load(File file) {
    try {
      load(JAXB.unmarshallPicture(file));
    } catch (JAXBException e) {
      log("unmarshalling picture metadata from file " + file, e);
    }
  }


  private void save(File file) {
    try {
      org.podval.album.jaxb.Picture result = JAXB.getObjectFactory().createPicture();
      save(result);
      JAXB.marshall(result, file);
    } catch (Exception e) {
      log("marshalling picture metadata to file " + file, e);
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
        log("reading camera metadata from file " + file, e);
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


  private File metadataFile;


  private long loaded = 0;


  private SoftReference cameraMetadata;
}
