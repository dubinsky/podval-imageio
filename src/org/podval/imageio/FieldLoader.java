package org.podval.imageio;

import java.util.Iterator;


/**
 */

public class FieldLoader {

  public static Field load(org.podval.imageio.jaxb.Field xml) {
    String name = xml.getName();
    Field result = new Field(name);
    TypedLoader.load(result, xml);
    loadEnumeration(result, xml);
    loadSubfields(result, xml);
    return result;
  }


  private static void loadEnumeration(Field result, org.podval.imageio.jaxb.Field xml) {
    result.setEnumeration(EnumerationLoader.load(xml.getEnumeration()));
  }


  private static void loadSubfields(Field result, org.podval.imageio.jaxb.Field xml) {
    for (Iterator i = xml.getSubfields().iterator(); i.hasNext();)
      result.addSubfield(FieldLoader.load((org.podval.imageio.jaxb.Field) i.next()));
  }
}
