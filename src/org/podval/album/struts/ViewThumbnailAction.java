package org.podval.album.struts;

import java.io.File;
import java.io.IOException;
import org.podval.album.*;


public class ViewThumbnailAction extends ViewAction {

  protected File getViewFile(Picture picture) throws IOException {
    return picture.getThumbnailFile();
  }
}
