package org.podval.album;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class Action extends org.apache.struts.action.Action {

  protected Album setupDirectory(HttpServletRequest request) {
    String path = getParameterOrEmpty(request, "path");
    Album result = Album.getByPath(path);
    request.setAttribute("path", path);
    request.setAttribute("directory", result);
    return result;
  }


  protected Picture setupPicture(HttpServletRequest request) {
    Album directory = setupDirectory(request);

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
}
