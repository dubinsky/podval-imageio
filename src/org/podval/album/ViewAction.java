package org.podval.album;

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


public class ViewAction extends Action {

  public ActionForward doExecute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response,
    String path,
    PictureDirectory directory)
    throws IOException
  {
    String name = request.getParameter("name");
    if (name == null)
      throw new NullPointerException("No name parameter.");

    String view = request.getParameter("view");
    if (view == null)
      throw new NullPointerException("No view parameter.");

    Picture picture = directory.getPicture(name);

    File file = null;

//    if (view.equals("thumbnail"))
      file = picture.getScaledFile(160, 120);


    response.setContentType("image/jpeg");

    if (file != null) {
      InputStream is = new FileInputStream(file);
      OutputStream os = response.getOutputStream();
      stream(is, os);
    }

    return null;
  }


  private static final int BUF_SIZE = 65536;


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
}
