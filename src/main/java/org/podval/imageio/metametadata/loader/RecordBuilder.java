/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class RecordBuilder extends EntryBuilder {

  public RecordBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);

    this.record = getMetaMetaData().getRecord(getName(attributes));
    record.setType(getType(attributes));
    /** @todo count */
    record.setIsVector(getBooleanAttribute("vector", attributes));
    record.setSkip(getBooleanAttribute("skip", attributes));

    String conversion = attributes.getValue("conversion");
    if (conversion != null) {
      record.getDefaultField().setConversion(conversion);
    }
  }


  public Entry getEntry() {
    return record;
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("field".equals(name)) {
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes);
      Field field = fieldBuilder.field;
      if (field.getType() == null) {
        field.setType(record.getType());
      }
      if (attributes.getValue("index") != null) {
        index = getIntegerAttribute("index", attributes);
      }
      record.addField(index, field);
      index++;
      result = fieldBuilder;
    } else

    if ("enumeration".equals(name)) {
      EnumerationBuilder enumerationBuilder = new EnumerationBuilder(this, attributes);
      record.getDefaultField().setEnumeration(enumerationBuilder.enumeration);
      result = enumerationBuilder;
    }

    return result;
  }


  private final Record record;


  private int index;
}
