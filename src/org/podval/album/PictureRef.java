package org.podval.album;

import java.io.File;
import java.io.IOException;


public class PictureRef implements Picture {

  public PictureRef(Album album, String path, String referentName, String localName) {
    this.album = album;
    /** @todo null checking for album and referent... */
    this.path = path;
    this.referentName = referentName;
    this.localName = localName;
    this.referent = Album.getByPath(path).getPicture(referentName);
  }


  public Album getAlbum() {
    return album;
  }


  public String getPath() {
   return path;
  }


  public String getReferentName() {
    return referentName;
  }


  public String getName() {
    return localName;
  }


  public String getDateTimeString() throws IOException {
    return referent.getDateTimeString();
  }


  public File getThumbnailFile() throws IOException {
    if (referent == null) {
      System.out.println("Referent is null: " + path + " " + referentName + " " + localName);
    }
    return referent.getThumbnailFile();
  }


  public File getScreensizedFile() throws IOException {
    return referent.getScreensizedFile();
  }


  public File getFullsizedFile() throws IOException {
    return referent.getFullsizedFile();
  }


  private final Album album;


  private final String path;


  private final String referentName;


  private final String localName;


  private final Picture referent;
}
