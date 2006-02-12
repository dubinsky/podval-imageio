/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public abstract class EntryBuilder<T extends Entry> extends ThingBuilder<T> {

  protected EntryBuilder(Builder previous, T entry, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, entry);
    thing.setType(getType(attributes));
  }


  private static Type getType(Attributes attributes)
    throws MetaMetaDataException
  {
    Type result =  null;

    String typeName = attributes.getValue("type");

    if (typeName != null) {
      try {
        typeName = typeName.toUpperCase();
        result = Type.valueOf(typeName);
      } catch (IllegalArgumentException e) {
        throw new MetaMetaDataException("Unknown type " + typeName);
      }
    }

    return result;
  }
}
