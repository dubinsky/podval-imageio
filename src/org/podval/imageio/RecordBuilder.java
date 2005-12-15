/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class RecordBuilder extends Builder {

  public RecordBuilder(HeapBuilder previous, RecordNG record) {
    super(previous);
    this.record = record;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    Builder result = null;


    if ("field".equals(name)) {
      FieldNG field = new FieldNG(getName(attributes), getType(attributes));
      result = new FieldBuilder(this, field);
    } else

    if ("enumeration".equals(name)) {
      /** @todo  */
      result = new NullBuilder(this);
    }

    return result;
  }


  private final RecordNG record;
}
