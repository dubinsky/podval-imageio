package org.podval.album;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.StringTokenizer;


public abstract class Action extends org.apache.struts.action.Action {

  protected PictureDirectory setupDirectory(HttpServletRequest request) {
    String path = getParameterOrEmpty(request, "path");
    PictureDirectory result = getByPath(path);
    request.setAttribute("path", path);
    request.setAttribute("directory", result);
    return result;
  }


  protected Picture setupPicture(HttpServletRequest request) {
    PictureDirectory directory = setupDirectory(request);

    String name = getParameterOrEmpty(request, "name");
    Picture result = directory.getPicture(name);
    if (result == null)
      throw new NullPointerException("No picture with this name.");

    request.setAttribute("name", name);
    request.setAttribute("picture", result);

    return result;
  }


  private String getParameterOrEmpty(HttpServletRequest request, String name) {
    String result = request.getParameter(name);
    return (result != null) ? result : "";
  }


  private PictureDirectory getByPath(String path) {
    PictureDirectory result = Context.getRoot();

    if (result == null)
      throw new NullPointerException("Root is not set");

//    if (!path.startsWith("/"))
//      throw new IllegalArgumentException("Path does not start with '/'.");

    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    while (tokenizer.hasMoreTokens() && (result != null)) {
      result = ((PictureDirectory) result).getSubdirectory(tokenizer.nextToken());
    }

    if (result == null)
      throw new NullPointerException("No PictureDirectory at path: " + path);

    return result;
  }
}
