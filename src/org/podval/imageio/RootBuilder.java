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
    Builder result = null;

    if ("directory".equals(name)) {
      result = new HeapBuilder(this, getHeap(attributes));
    }

    /** @todo record!!! */
    /** @todo makerNote!!! */

    return result;
  }
}
