/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Enumeration;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class EnumerationBuilder extends Builder {

  public EnumerationBuilder(Builder previous, Attributes attributes) {
    super(previous);
    this.enumeration = new Enumeration(null /** @todo class!!! */);
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("item".equals(name)) {
      EnumerationItemBuilder next = new EnumerationItemBuilder(this, attributes);

      enumeration.addValue(
        getIntegerAttribute("tag", attributes),
        next.value
      );

      result = next;
    }

    return result;
  }


  public String toString() {
    return enumeration.toString();
  }


  public final Enumeration enumeration;
}
