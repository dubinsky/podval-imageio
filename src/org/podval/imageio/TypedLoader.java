package org.podval.imageio;

/**
 */

public class TypedLoader {

  public static void load(Typed result, org.podval.imageio.jaxb.Typed xml) {
    if (xml.getType() != null)
      result.setType(Type.parse(xml.getType()));
  }
}
