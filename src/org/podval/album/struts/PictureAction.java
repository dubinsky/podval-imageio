package org.podval.album.struts;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.podval.album.*;


public class PictureAction extends Action {

  public ActionForward execute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response)
  {
    setupPicture(request);
    return mapping.findForward("jsp");
  }
}
