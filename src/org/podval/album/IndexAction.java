package org.podval.album;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class IndexAction extends Action {

  public ActionForward doExecute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response,
    String path,
    PictureDirectory directory)
  {
    return mapping.findForward("jsp");
  }
}
