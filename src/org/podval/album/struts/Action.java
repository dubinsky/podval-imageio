package org.podval.album.struts;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.podval.album.Album;
import org.podval.album.Picture;


public abstract class Action extends org.apache.struts.action.Action {

  protected Album setupAlbum(HttpServletRequest request) {
    String path = getPath(request);
    Album result = Album.getByPath(path);
    request.setAttribute("album", result);
    return result;
  }


  protected Picture setupPicture(HttpServletRequest request) {
    String path = getPath(request);
    Picture result = Picture.getByPath(path);
    request.setAttribute("picture", result);
    return result;
  }


  private String getPath(HttpServletRequest request) {
    String result = request.getParameter("path");
    return (result != null) ? result : "/";
  }
}
