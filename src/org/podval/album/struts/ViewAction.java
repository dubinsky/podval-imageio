package org.podval.album.struts;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.podval.album.Picture;


public abstract class ViewAction extends Action {

  public ActionForward execute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response)
    throws IOException
  {
    Picture picture = setupPicture(request);

    File file = getViewFile(picture);
    if (file == null)
      throw new AssertionError("getViewFile() returned null!");

    response.setContentType("image/jpeg");

    InputStream is = new FileInputStream(file);
    OutputStream os = response.getOutputStream();
    stream(is, os);

    return null;
  }


  private static final int BUF_SIZE = 512;


  private void stream(final InputStream is, final OutputStream os) {
    byte[] buf = new byte[BUF_SIZE];
    try {
      while (is.available() > 0) {
        int numBytes = is.read(buf);
        if (numBytes > 0)
          os.write(buf, 0, numBytes);
      }
    } catch (IOException e) {
    } finally {
      try {
        os.flush();
        is.close();
      } catch (IOException e) {
      }
    }
  }


  protected abstract File getViewFile(Picture picture) throws IOException;


  public static class Screensized extends ViewAction {

    protected File getViewFile(Picture picture) throws IOException {
      return picture.getScreensizedFile();
    }
  }



  public static class Thumbnail extends ViewAction {

    protected File getViewFile(Picture picture) throws IOException {
      return picture.getThumbnailFile();
    }
  }
}
