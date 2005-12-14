/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class HeapBuilder extends Builder {

  public HeapBuilder(Builder previous, Heap heap) {
    super(previous);
    this.heap = heap;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    Builder result;

    int tag;
    try {
      tag = Integer.valueOf(attributes.getValue("tag"));
    } catch (NumberFormatException e) {
      throw new SAXException(e);
    }

    Entry entry;
    if ("directory".equals(name)) {
      Heap heap = getHeap(attributes);
      entry = heap;
      result = new HeapBuilder(this, heap);
    } else

    if ("record".equals(name)) {
      RecordNG record = new RecordNG(getName(attributes), getType(attributes));
      entry = record;
      result = new RecordBuilder(this, record);
    } else {

      throw new SAXException();
    }

    heap.addEntry(tag, entry);

    return result;
  }


  private final Heap heap;
}
