/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.EnumerationItem;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class EnumerationItemBuilder extends Builder<EnumerationItem> {

  public EnumerationItemBuilder(Builder<?> previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, new EnumerationItem(
      getAttribute("name", attributes),
      getAttribute("value", attributes))
    );
  }


  public Builder<?> startElement(String name, Attributes attributes) {
    return null;
  }
}
