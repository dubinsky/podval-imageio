package org.podval.album.struts;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.podval.album.*;


public class IndexAction extends Action {

  public ActionForward execute(
    ActionMapping mapping,
    ActionForm actionForm,
    HttpServletRequest request,
    HttpServletResponse response)
  {
    /** @todo THIS IS NOT SUPPOSED TO BE HERE AT ALL!!!!! */
    AlbumLocal.saveChanged();



    setupAlbum(request);
    return mapping.findForward("jsp");
  }
}
