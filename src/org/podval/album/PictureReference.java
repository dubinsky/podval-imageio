package org.podval.album;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import org.podval.imageio.Orientation;

import javax.xml.bind.JAXBException;


public class PictureReference extends Picture {

  public PictureReference(String path) {
    super(path);
  }


  protected String getDefaultTitle() {
    return getReferent().getTitle();
  }


  public Date getDateTime() {
    return getReferent().getDateTime();
  }


  public Orientation getOrientation() {
    return getReferent().getOrientation();
  }


  public void rotateLeft() {
    getReferent().rotateLeft();
  }


  public void rotateRight() {
    getReferent().rotateRight();
  }


  public File getThumbnailFile() {
    return getReferent().getThumbnailFile();
  }


  public File getScreensizedFile() {
    return getReferent().getScreensizedFile();
  }


  public File getFullsizedFile() {
    return getReferent().getFullsizedFile();
  }


  private Picture getReferent() {
    if (referent == null) {
      synchronized (this) {
      referent = Picture.getByPath(getName());

      /** @todo ? */
//      if (referent == null)
//        throw new NullPointerException("Referent not found: " + getPath());
      }
    }

    return referent;
  }


  protected void load() {
  }


  public void save() {
    // Whatever needs saving about the reference will be saved by the album.
    // Whatever was changed through the reference will be saved by the referent.
  }


  public static PictureReference load(
    org.podval.album.jaxb.PictureReferences.Picture xml
  ) {
    PictureReference result = new PictureReference(xml.getPath());
    result.title = xml.getTitle();
    return result;
  }


  public org.podval.album.jaxb.PictureReferences.Picture toXml()
    throws JAXBException
  {
    org.podval.album.jaxb.PictureReferences.Picture result =
      JAXB.getObjectFactory().createPictureReferencesTypePicture();

    result.setPath(getName());
    result.setTitle(title);

    return result;
  }


  private Picture referent;
}
