/** @todo original should be written (when rotated) losslessly! */
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

  public PictureLocal(String name) {
    super(name);
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
        throw new IOException("Duplicate case-sensitive extension " + extension);
  }


  public boolean isPicture() {
    return (getOriginalFile() != null);
  }


  public void setTitle(String value) {
    load();
    super.setTitle(value);
  }


  public String getTitle() {
    load();
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
    setOrientation(getOrientation().rotateLeft());
  }


  public void rotateRight() {
    setOrientation(getOrientation().rotateRight());
  }


  private void setOrientation(Orientation value) {
    if (orientation != value) {
      orientation = value;
      removeGeneratedFiles();
      changed();
    }
  }


  public Orientation getOrientation() {
    load();
    if (orientation == null) {
      Orientation value = (Orientation) getCameraMetadata("orientation");
      if (value == null)
        value = Orientation.NORMAL;
      setOrientation(value);
    }
    return orientation;
  }


  private void removeGeneratedFiles() {
    getThumbnailGnereatedFile().delete();
    getScreensizedGeneratedFile().delete();
    getFullsizedGeneratedFile().delete();
    /** @todo check return? */
  }


  private void load() {
    if (!changed) {
      if (metadataReadFile == null)
        metadataReadFile = getAlbum().getMetadataReadFile(getMetadataFileName());
        /* If I do not cache it, a File object is created every time.
           If I cache it, and metadata file gets created on the side, it
           will not be detected... */
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


  private void load(File file) {
    try {
      org.podval.album.jaxb.Picture xml = JAXB.unmarshallPicture(file);
      title = xml.getTitle();
      orientation = Orientation.get(xml.getOrientation());
    } catch (JAXBException e) {
    /** @todo  */
    }
  }


  public void save() {
    if (changed) {
      File file = getAlbum().getMetadataWriteFile(getMetadataFileName());
      if (file != null) {
        try {
          org.podval.album.jaxb.Picture result =
            JAXB.getObjectFactory().createPicture();

          if (title != null)
            result.setTitle(title);

          result.setOrientation(getOrientation().toString());

          JAXB.marshall(result, file);

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


  private String getMetadataFileName() {
    return getName() + ".xml";
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


  private File metadataReadFile;


  private long loaded = 0;


  private SoftReference cameraMetadata;
}
