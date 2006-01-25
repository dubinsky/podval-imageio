/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class EnumerationBuilder extends Builder {

  public EnumerationBuilder(Builder previous, Attributes attributes) {
    super(previous);
    this.enumeration = createEnumeration(attributes);
  }


  private Enumeration createEnumeration(Attributes attributes) {
    return new Enumeration(null /** @todo class!!! */);
  }



  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
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
    return "<enumeration/>";
  }


  public final Enumeration enumeration;
}
