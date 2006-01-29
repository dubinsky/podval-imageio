/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class RecordBuilder extends Builder {

  public RecordBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);
    this.record = createRecord(attributes);
  }


  private Record createRecord(Attributes attributes)
    throws MetaMetaDataException
  {
    Record result = getMetaMetaData().getRecord(
      getName(attributes),
      getType(attributes)
    );

    /** @todo count */

    result.setIsVector(getBooleanAttribute("vector", attributes));

    result.setSkip(getBooleanAttribute("skip", attributes));

    String conversion = attributes.getValue("conversion");
    if (conversion != null) {
      result.getDefaultField().setConversion(conversion);
    }

    return result;
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("field".equals(name)) {
      /** @todo if default field was created, explicit fields are not allowed. */
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes, record.getType());
      /** @todo there can not be one subfield in a field */
      if (attributes.getValue("index") != null) {
        index = getIntegerAttribute("index", attributes);
      }
      record.addField(index, fieldBuilder.field);
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


  public String toString() {
    return record.toString();
  }


  public final Record record;


  private int index;
}
