/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class RecordBuilder extends Builder {

  public RecordBuilder(Builder previous, RecordNG record) {
    super(previous);
    this.record = record;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    Builder result = null;

    if ("field".equals(name)) {
      RecordNG field = getField(attributes, record.getType());
      /** @todo count and indexes are allowed only for the top-most fields */
      /** @todo there can not be one subfield in a field */
      if (attributes.getValue("index") != null) {
        int index = getIntegerAttribute("index", attributes);
      }
      record.addField(index, field);
      index++;
      result = new RecordBuilder(this, field);
    } else

    if ("enumeration".equals(name)) {
      Enumeration enumeration = getEnumeration(attributes);
      record.setEnumeration(enumeration);
      result = new EnumerationBuilder(this, enumeration);
    }

    return result;
  }


  public String toString() {
    return "<record name=\"" + record.getName() + "\"/>";
  }


  private final RecordNG record;


  private int index;
}
