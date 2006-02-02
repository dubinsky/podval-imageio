/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class EnumerationItemBuilder extends Builder {

  public EnumerationItemBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);

    value = attributes.getValue("value");
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }


  public String toString() {
    return value;
  }


  public final String value;
}
