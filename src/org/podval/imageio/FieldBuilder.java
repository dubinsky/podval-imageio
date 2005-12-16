/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class FieldBuilder extends Builder {

  public FieldBuilder(RecordBuilder previous, FieldNG field) {
    super(previous);
    this.field = field;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    Builder result = null;

    /** @todo  */
    result = new NullBuilder(this);

    return result;
  }


  public String toString() {
    return "<field name=\"" + field.getName() + "\"/>";
  }


  private final FieldNG field;
}
