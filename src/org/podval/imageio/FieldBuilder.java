/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class FieldBuilder extends Builder {

  public FieldBuilder(Builder previous, Attributes attributes, Type defaultType)
    throws SAXException
  {
    super(previous);
    this.field = createField(attributes, defaultType);
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    Builder result = null;

    if ("field".equals(name)) {
      /** @todo !!!!! */
      /** @todo there can not be one subfield in a field */
/////      field.addField(subField);
      FieldBuilder fieldBuilder = new FieldBuilder(this, attributes, field.getType());
      Field subField = fieldBuilder.field;
      result = fieldBuilder;
    } else

    if ("enumeration".equals(name)) {
      EnumerationBuilder enumerationBuilder = new EnumerationBuilder(this, attributes);
      field.setEnumeration(enumerationBuilder.enumeration);
      result = enumerationBuilder;
    }

    return result;
  }


  private Field createField(Attributes attributes, Type defaultType)
    throws SAXException
  {
    String name = getName(attributes);
    Type type = getType(attributes);

    if (type == null) {
      type = defaultType;
    }

    Field result = new Field(name, type);

    result.setSkip(getBooleanAttribute("skip", attributes));
    result.setConversion(attributes.getValue("conversion"));

    return result;
  }


  public String toString() {
    return "<record name=\"" + field.getName() + "\"/>";
  }


  public final Field field;
}
