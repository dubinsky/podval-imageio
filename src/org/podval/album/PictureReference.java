package org.podval.album;

import java.io.File;
import java.io.IOException;

import java.util.Date;


public class PictureReference extends Picture {

  public PictureReference(Album album, String name) {
    super(album, name);
    this.referent = Picture.getByPath(name);
  }


  public void setTitle(String value) {
    getReferent().setTitle(value);
  }


  public String getTitle() {
    return getReferent().getTitle();
  }


  public Date getDateTime() {
    return getReferent().getDateTime();
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
    getReferent().save(); /** @todo or nothing? */
  }


  private Picture getReferent() {
    if (referent == null)
      throw new NullPointerException("Referent is null in " + getPath());

    return referent;
  }


  private final Picture referent;
}
