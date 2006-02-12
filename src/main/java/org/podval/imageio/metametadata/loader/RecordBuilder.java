/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class RecordBuilder extends EntryBuilder<Record> {

  public RecordBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, previous.getMetaMetaData().getRecord(getName(attributes)), attributes);

    /** @todo count */
    thing.setIsVector(getBooleanAttribute("vector", attributes));
    thing.setSkip(getBooleanAttribute("skip", attributes));

    String conversion = attributes.getValue("conversion");
    if (conversion != null) {
      thing.getDefaultField().setConversion(conversion);
    }
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("field".equals(name)) {
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes, thing);
      if (attributes.getValue("index") != null) {
        index = getIntegerAttribute("index", attributes);
      }
      thing.addField(index, fieldBuilder.thing);
      index++;
      result = fieldBuilder;
    } else

    if ("enumeration".equals(name)) {
      result = new EnumerationBuilder(this, attributes, thing.getDefaultField());
    }

    return result;
  }


  private int index;
}
