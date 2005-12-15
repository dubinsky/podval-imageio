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

    return result;
  }


  private final FieldNG field;
}
