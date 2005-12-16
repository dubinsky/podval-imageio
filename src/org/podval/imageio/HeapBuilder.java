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
    Builder result = null;

    int tag;
    try {
      tag = Integer.valueOf(attributes.getValue("tag"));
    } catch (NumberFormatException e) {
      throw new SAXException(e);
    }

    Entry entry = null;

    if ("directory".equals(name)) {
      Heap heap = getHeap(attributes);
      entry = heap;
      result = new HeapBuilder(this, heap);
    } else

    if ("record".equals(name)) {
      RecordNG record = getRecord(attributes);
      entry = record;
      result = new RecordBuilder(this, record);
    } else

    if ("makerNoteMarker".equals(name)) {
      MakerNoteMarker makerNoteMarker = new MakerNoteMarker(getType(attributes));
      entry = makerNoteMarker;
      result = new MakerNoteMarkerBuilder(this, makerNoteMarker);
    }

    if (entry != null) {
      heap.addEntry(tag, entry);
    }

    return result;
  }


  public String toString() {
    return "<heap name=\"" + heap.getName() + "\"/>";
  }


  private final Heap heap;
}
