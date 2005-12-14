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
    if ("field".equals(name)) {
      /** @todo  */
      return new NullBuilder(this);
    } else

    if ("enumeration".equals(name)) {
      /** @todo  */
      return new NullBuilder(this);
    } else {

      throw new SAXException();
    }
  }


  private final RecordNG record;
}
