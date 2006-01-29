/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Enumeration;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class EnumerationBuilder extends Builder {

  public EnumerationBuilder(Builder previous, Attributes attributes) {
    super(previous);
    this.enumeration = createEnumeration(attributes);
  }


  private Enumeration createEnumeration(Attributes attributes) {
    return new Enumeration(null /** @todo class!!! */);
  }


  public Builder startElement(String name,
    Attributes attributes) throws MetaMetaDataException {
    Builder result = null;

    if ("item".equals(name)) {
      int tag = getIntegerAttribute("tag", attributes);
      String value = attributes.getValue("value");
      enumeration.addValue(tag, value);
      result = new EnumerationItemBuilder(this);
    }

    return result;
  }


  public String toString() {
    return enumeration.toString();
  }


  public final Enumeration enumeration;
}
