package org.podval.album;

import java.io.File;
import java.io.IOException;


public class PictureRef implements Picture {

  public PictureRef(Album album, Picture referent) {
    /** @todo null checking for album and referent... */
    this.album = album;
    this.referent = referent;
  }


  public Album getAlbum() {
    return album;
  }


  public String getName() {
    return referent.getName();
  }


  public String getDateTimeString() throws IOException {
    return referent.getDateTimeString();
  }


  public File getThumbnailFile() throws IOException {
    return referent.getThumbnailFile();
  }


  public File getScreensizedFile() throws IOException {
    return referent.getScreensizedFile();
  }


  public File getFullsizedFile() throws IOException {
    return referent.getFullsizedFile();
  }


  private final Album album;


  private final Picture referent;
}
