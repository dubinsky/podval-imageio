package org.podval.album;

import java.io.File;
import java.io.IOException;

import java.util.Date;

import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.podval.imageio.Orientation;


public abstract class Picture implements Comparable {

  public static Picture getByPath(String path) {
    if (path == null)
      throw new NullPointerException("Path is null!");

    int colon = path.lastIndexOf(':');

    if (colon == -1)
      throw new IllegalArgumentException("No ':' in the picture path!");

    String albumPath = path.substring(0, colon);
    String pictureName = path.substring(colon+1, path.length());

    return Album.getByPath(albumPath).getPicture(pictureName);
  }


  protected Picture(String name) {
    if (name == null)
      throw new NullPointerException("Name is null!");

    if (name.equals(""))
      throw new IllegalArgumentException("Name is empty!");

    this.name = name;
  }


  public final String getName() {
    return name;
  }


  public final void setAlbum(AlbumLocal value) {
    if (value == null)
      throw new NullPointerException("Album is null!");

    album = value;
  }


  public final AlbumLocal getAlbum() {
    if (album == null)
      throw new NullPointerException("Album is not set!");

    return album;
  }


  public final String getPath() {
    return getAlbum().getPath() + ":" + getName();
  }


  public final void setTitle(String value) {
    load();
    if (((title == null) && (value != null)) || !title.equals(value)) {
      title = value;
      changed();
    }
  }


  public final String getTitle() {
    load();
    return (title != null) ? title : getDefaultTitle();
  }


  public final int compareTo(Object o) {
    Picture other = (Picture) o;
    return getDateTime().compareTo(other.getDateTime());
  }


  private static final SimpleDateFormat dateFormat =
    new SimpleDateFormat("M/d/y HH:mm:ss");


  public final String getDateTimeString() throws IOException {
    return dateFormat.format(getDateTime());
  }


  protected final void changed() {
    changed = true;
    getAlbum().changed(this);
  }


  protected abstract String getDefaultTitle();


  public abstract File getThumbnailFile() throws IOException;


  public abstract File getScreensizedFile() throws IOException;


  public abstract File getFullsizedFile() throws IOException;


  public abstract Date getDateTime();


  public abstract Orientation getOrientation();


  public abstract void rotateLeft();


  public abstract void rotateRight();


  protected abstract void load();


  public abstract void save();


  private final String name;


  private AlbumLocal album;


  protected String title;


  protected boolean changed;
}
