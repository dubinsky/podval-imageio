/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class FieldBuilder extends Builder {

  public FieldBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);

    this.field = new Field(getName(attributes));
    field.setType(getType(attributes));
    field.setSkip(getBooleanAttribute("skip", attributes));
    field.setConversion(attributes.getValue("conversion"));
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("field".equals(name)) {
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes);
      Field subField = fieldBuilder.field;

      if (subField.getType() == null) {
        subField.setType(field.getType());
      }

      field.addSubField(subField);
      result = fieldBuilder;
    } else

    if ("enumeration".equals(name)) {
      EnumerationBuilder enumerationBuilder = new EnumerationBuilder(this, attributes);
      field.setEnumeration(enumerationBuilder.enumeration);
      result = enumerationBuilder;
    }

    return result;
  }


  protected void check() throws MetaMetaDataException {
    field.checkSubFields();
  }


  public String toString() {
    return field.toString();
  }


  public final Field field;
}
