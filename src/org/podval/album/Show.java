package org.podval.album;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Show extends Action {

  public ActionForward execute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response)
  {
    return mapping.findForward("jsp");
  }
}
