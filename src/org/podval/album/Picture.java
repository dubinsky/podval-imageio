package org.podval.album;

import java.io.File;
import java.io.IOException;


public interface Picture {

  public Album getAlbum();


  public String getName();


  public String getDateTimeString() throws IOException;


  public File getThumbnailFile() throws IOException;


  public File getScreensizedFile() throws IOException;


  public File getFullsizedFile() throws IOException;
}
