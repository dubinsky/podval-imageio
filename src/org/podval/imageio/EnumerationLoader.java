package org.podval.imageio;

import java.util.Iterator;


/**
 */

public class EnumerationLoader {

  public static Enumeration load(org.podval.imageio.jaxb.Enumeration xml) {
    Enumeration result = null;

    if (xml != null) {
      result = new Enumeration();

      for (Iterator i = xml.getItems().iterator(); i.hasNext(); ) {
        org.podval.imageio.jaxb.EnumItem item =
          (org.podval.imageio.jaxb.EnumItem) i.next();

        result.addDescription(item.getValue(), item.getDescription());
      }
    }

    return result;
  }
}
