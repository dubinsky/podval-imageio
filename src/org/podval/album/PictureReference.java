package org.podval.album;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import org.podval.imageio.Orientation;


public class PictureReference extends Picture {

  public PictureReference(Album album, String name) {
    super(album, name);
  }


  public void setTitle(String value) {
    if (title != value) {
      title = value;
      getAlbum().pictureReferencesChanged();
    }
  }


  public String getRawTitle() {
    return title;
  }


  public String getTitle() {
    return (title != null) ? title : getReferent().getTitle();
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


  public File getThumbnailFile() throws IOException {
    return getReferent().getThumbnailFile();
  }


  public File getScreensizedFile() throws IOException {
    return getReferent().getScreensizedFile();
  }


  public File getFullsizedFile() throws IOException {
    return getReferent().getFullsizedFile();
  }


  public void save() {
    // Whatever needs saving about the reference will be saved by the album.
    // Whatever was changed through the reference will be saved by the referent.
  }


  private Picture getReferent() {
    if (referent == null) {
      referent = Picture.getByPath(getName());

      /** @todo ? */
//      if (referent == null)
//        throw new NullPointerException("Referent not found: " + getPath());
    }

    return referent;
  }


  private String title;


  private Picture referent;
}
