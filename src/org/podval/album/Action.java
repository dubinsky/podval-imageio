package org.podval.album;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.StringTokenizer;


public abstract class Action extends org.apache.struts.action.Action {

  public ActionForward execute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response)
    throws Exception
  {
    String path = request.getParameter("path");
    if (path == null)
      path = "";

    PictureDirectory directory = getByPath(path);

    request.setAttribute("path", path);
    request.setAttribute("directory", directory);

    return doExecute(mapping, actionForm, request, response, path, directory);
  }


  protected abstract ActionForward doExecute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response,
    String path,
    PictureDirectory directory
  ) throws Exception;


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
