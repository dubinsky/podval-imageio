package org.podval.album;

import java.util.Collection;


public class AlbumReference extends Album {

  public String getPath() {
    return getParent().getPath() + ":" + getName();
  }


  protected String getDefaultTitle() {
    return getReferent().getTitle();
  }


  public int getNumSubalbums() {
    return getReferent().getNumSubalbums();
  }


  public Album getSubalbum(String name) {
    return getReferent().getSubalbum(name);
  }


  public Collection getSubalbums() {
    return getReferent().getSubalbums();
  }


  public int getNumPictures() {
    return getReferent().getNumPictures();
  }


  public Picture getPicture(String name) {
    return getReferent().getPicture(name);
  }


  public Collection getPictures() {
    return getReferent().getPictures();
  }


  protected void loadMetadata() {
  }


  protected void metadataChanged() {
  }


  private Album getReferent() {
    /** @todo XXXXX */
    return null;
  }
}
