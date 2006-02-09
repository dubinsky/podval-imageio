/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class FieldBuilder extends Builder {

  public FieldBuilder(Builder previous, Attributes attributes, Type defaultType)
    throws MetaMetaDataException
  {
    super(previous);
    this.field = createField(attributes, defaultType);
  }


  private Field createField(Attributes attributes, Type defaultType)
    throws MetaMetaDataException
  {
    String name = getName(attributes);
    Type type = getType(attributes);

    if (type == null) {
      type = defaultType;
    }

    Field result = new Field(name);
    result.setType(type);

    result.setSkip(getBooleanAttribute("skip", attributes));
    result.setConversion(attributes.getValue("conversion"));

    return result;
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("field".equals(name)) {
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes, field.getType());
      Field subField = fieldBuilder.field;
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
