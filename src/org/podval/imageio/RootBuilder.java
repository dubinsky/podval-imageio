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
    } else

    if ("record".equals(name)) {
      result = new RecordBuilder(this, getRecord(attributes));
    } else

    if ("makerNote".equals(name)) {
      Heap makerNote = getMetaMetaData().getMakerNote(
        getName(attributes),
        getAttribute("maker", attributes),
        attributes.getValue("signature")
      );
      result = new HeapBuilder(this, makerNote);
    }

    return result;
  }


  public String toString() {
    return "<meta-metadata/>";
  }
}
