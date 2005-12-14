/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class RootBuilder extends Builder {

  public RootBuilder(DocumentBuilder previous) {
    super(previous);
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    if (!"directory".equals(name)) {
      throw new SAXException();
    }

    return new HeapBuilder(this, getHeap(attributes));
  }
}
