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
      checkExtension(jpgFile, extension);
      jpgFile = file;
    } else

    if (extension.equalsIgnoreCase("crw")) {
      checkExtension(crwFile, extension);
      crwFile = file;
    } else

    if (extension.equalsIgnoreCase("thm")) {
      checkExtension(thmFile, extension);
      thmFile = file;
    } else

    ;
  }


  private void checkExtension(File file, String extension) throws IOException {
    if (file != null)
        throw new IOException("Duplicatr case-sensitive extension " + extension);
  }


  public boolean isPicture() {
    return (getOriginalFile() != null);
  }


  public void setTitle(String value) {
    ensureMetadataLoaded();
    if (title != value) {
      title = value;
      metadataChanged();
    }
  }


  public String getTitle() {
    ensureMetadataLoaded();
    return (title != null) ? title : getName();
  }


  public File getThumbnailFile() throws IOException {
    File result = getThumbnailGnereatedFile();
    if (!result.exists()) {
      RenderedImage image = null;

      if (thmFile != null) image = Util.rotate(Util.readImage(thmFile), getOrientation()); else
      if (crwFile != null) image = Util.rotate(Util.readThumbnail(crwFile, 0), getOrientation());

      if (image == null) image = scale(120, 160);

      Util.writeImage(image, result);
    }

    return result;
  }


  private File getThumbnailGnereatedFile() {
    if (thumbnailFile == null)
      thumbnailFile = getGeneratedFile("120x160");

    return thumbnailFile;
  }


  public File getScreensizedFile() throws IOException {
    File result = getScreensizedGeneratedFile();
    if (!result.exists()) {
      RenderedImage image = null;

      if (crwFile != null) image = Util.rotate(Util.readThumbnail(crwFile, 1), getOrientation());

      if (image == null) image = scale(480, 640);

      Util.writeImage(image, result);
    }

    return result;
  }


  private File getScreensizedGeneratedFile() {
    if (screensizedFile == null)
      screensizedFile = getGeneratedFile("480x640");

    return screensizedFile;
  }


  public File getFullsizedFile() throws IOException {
    File result = null;

    if ((jpgFile != null) && (getOrientation() == Orientation.NORMAL)) {
      result = jpgFile;

    } else {
      result = getFullsizedGeneratedFile();

      if (!result.exists()) {
        RenderedImage image = ((jpgFile != null) ? Util.readImage(jpgFile) : Util.convert(crwFile));
        Util.writeImage(Util.rotate(image, getOrientation()), result);
      }
    }

    return result;
  }


  private File getFullsizedGeneratedFile() {
    if (originalFile == null)
      originalFile = getGeneratedFile("original");

    return originalFile;
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
/////    return (Date) getCameraMetadata("dateTime");
    /** @todo ??? */
    return null;
  }


  public void rotateLeft() {
    orientation = orientation.rotateLeft();
    removeGeneratedFiles();
    metadataChanged();
  }


  public void rotateRight() {
    orientation = orientation.rotateRight();
    removeGeneratedFiles();
    metadataChanged();
  }


  public Orientation getOrientation() {
    ensureMetadataLoaded();
    if (orientation == null) {
      orientation = (Orientation) getCameraMetadata("orientation");
      removeGeneratedFiles();
      metadataChanged();
    }
    return orientation;
  }


  private void removeGeneratedFiles() {
    getThumbnailGnereatedFile().delete();
    getScreensizedGeneratedFile().delete();
    getFullsizedGeneratedFile().delete();
    /** @todo check return? */
  }


  private void ensureMetadataLoaded() {
    if (!metadataChanged) {
      File file = getMetadataReadFile();
      if (file != null) {
        long lastModified = file.lastModified();
        if (metadataLoaded<lastModified) {
          metadataLoaded = lastModified;
          loadMetadata(file);
          metadataChanged = false;
        }
      }
    }
  }


  private void metadataChanged() {
    metadataChanged = true;
    getAlbum().pictureChanged();
  }


  private void loadMetadata(File file) {
    try {
      loadMetadata(JAXB.unmarshallPicture(file));
    } catch (JAXBException e) {
    /** @todo  */
    }
  }


  private void loadMetadata(org.podval.album.jaxb.Picture xml) {
    title = xml.getTitle();
    orientation = Orientation.get(xml.getOrientation());
  }



  public void save() {
    if (metadataChanged) {
      File file = getMetadataWriteFile();
      if (file != null) {
        try {
          org.podval.album.jaxb.Picture result = JAXB.createPicture();

          if (title != null)
            result.setTitle(title);

          result.setOrientation(getOrientation().toString());

          JAXB.marshallPicture(result, file);

        } catch (FileNotFoundException e) {
          /** @todo  */
        } catch (JAXBException e) {
          /** @todo  */
        } catch (Exception e) {
          /** @todo  */
        }
      }
    }
  }


  private File getMetadataReadFile() {
    /** @todo cache? */
    return getAlbum().getMetadataReadFile(getName()+".xml");
  }


  private File getMetadataWriteFile() {
    return getAlbum().getMetadataWriteFile(getName()+".xml");
  }


  private Object getCameraMetadata(String name) {
    if ((cameraMetadata == null) || (cameraMetadata.get() == null)) {
      Metadata result = null;
      try {
        result = Metadata.read(getOriginalFile());
      } catch (IOException e) {
        /** @todo ? */
      }
      cameraMetadata = new SoftReference(result);
    }
    return ((Metadata) cameraMetadata.get()).find(name);
  }


  private String title;


  private Orientation orientation;


  private File jpgFile;


  private File thmFile;


  private File crwFile;


  private File thumbnailFile;


  private File screensizedFile;


  /**
   * For CRW - converted and rotated;
   * For JPG - rotated if neccessary - or null.
   */
  private File originalFile;


  private long metadataLoaded = 0;


  private boolean metadataChanged;


  private SoftReference cameraMetadata;
}
