package org.podval.album;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Date;

import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBException;

import org.podval.imageio.Orientation;


public abstract class Picture implements Comparable {

  public static Picture getByPath(String path) {
    if (path == null)
      throw new NullPointerException("Path is null!");

    int colon = path.indexOf(':');

    if (colon == -1)
      throw new IllegalArgumentException("No ':' in the picture path!");

    String albumPath = path.substring(0, colon);
    String pictureName = path.substring(colon+1, path.length());

    return Album.getByPath(albumPath).getPicture(pictureName);
  }


  protected Picture(Album album, String name) {
    if (album == null)
      throw new NullPointerException("Album is null!");

    if (name == null)
      throw new NullPointerException("Name is null!");

    if (name.equals(""))
      throw new IllegalArgumentException("Name is empty!");

    this.album = album;
    this.name = name;
  }


  public Album getAlbum() {
    return album;
  }


  public String getName() {
    return name;
  }


  public String getPath() {
    return album.getPath() + ":" + getName();
  }


  public int compareTo(Object o) {
    Picture other = (Picture) o;
    return getDateTime().compareTo(other.getDateTime());
  }


  private static final SimpleDateFormat dateFormat =
    new SimpleDateFormat("M/d/y HH:mm:ss");


  public String getDateTimeString() throws IOException {
    return dateFormat.format(getDateTime());
  }


  public abstract void setTitle(String value);


  public abstract String getTitle();


  public abstract File getThumbnailFile() throws IOException;


  public abstract File getScreensizedFile() throws IOException;


  public abstract File getFullsizedFile() throws IOException;


  public abstract Date getDateTime();


  public abstract Orientation getOrientation();


  public abstract void rotateLeft();


  public abstract void rotateRight();


  public abstract void save();


  private final Album album;


  private final String name;
}
