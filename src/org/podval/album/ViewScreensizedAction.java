package org.podval.album;

import java.io.File;
import java.io.IOException;


public class ViewScreensizedAction extends ViewAction {

  protected File getViewFile(Picture picture) throws IOException {
    return picture.getScreensizedFile();
  }
}
