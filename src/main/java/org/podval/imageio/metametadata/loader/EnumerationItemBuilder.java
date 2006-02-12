/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class EnumerationItemBuilder extends ThingBuilder<String> {

  public EnumerationItemBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, attributes.getValue("value"));
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }
}
