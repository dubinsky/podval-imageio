/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Enumeration;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class EnumerationBuilder extends ThingBuilder<Enumeration> {

  public EnumerationBuilder(Builder previous, Attributes attributes, Field field)
    throws MetaMetaDataException
  {
    super(previous, new Enumeration(null /** @todo class!!! */));
    field.setEnumeration(thing);
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("item".equals(name)) {
      EnumerationItemBuilder next = new EnumerationItemBuilder(this, attributes);

      thing.addValue(
        getIntegerAttribute("tag", attributes),
        next.thing
      );

      result = next;
    }

    return result;
  }
}
