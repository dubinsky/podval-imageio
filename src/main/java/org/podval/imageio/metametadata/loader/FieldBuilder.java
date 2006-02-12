/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class FieldBuilder extends EntryBuilder<Field> {

  public FieldBuilder(Builder<?> previous, Attributes attributes, Entry parent)
    throws MetaMetaDataException
  {
    super(previous, new Field(getName(attributes)), attributes);

    if (thing.getType() == null) {
      thing.setType(parent.getType());
    }

    thing.setSkip(getBooleanAttribute("skip", attributes));
    thing.setConversion(getAttribute("conversion", attributes));
  }


  public Builder<?> startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder<?> result = null;

    if ("field".equals(name)) {
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes, thing);
      thing.addSubField(fieldBuilder.thing);
      result = fieldBuilder;
    } else

    if ("enumeration".equals(name)) {
      result = new EnumerationBuilder(this, attributes, thing);
    }

    return result;
  }


  protected void check() throws MetaMetaDataException {
    thing.checkSubFields();
  }
}
