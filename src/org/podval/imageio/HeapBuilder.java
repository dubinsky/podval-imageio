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
    throws MetaMetaDataException
  {
    Builder result = null;

    Entry entry = null;

    if ("directory".equals(name)) {
      Heap heap = getHeap(attributes);
      entry = heap;
      result = new HeapBuilder(this, heap);
    } else

    if ("record".equals(name)) {
      RecordBuilder recordBuilder = new RecordBuilder(this, attributes);
      entry = recordBuilder.record;
      result = recordBuilder;
    } else

    if ("makerNoteMarker".equals(name)) {
      MakerNoteMarker makerNoteMarker = new MakerNoteMarker(getType(attributes));
      entry = makerNoteMarker;
      result = new MakerNoteMarkerBuilder(this, makerNoteMarker);
    }

    if (entry != null) {
      heap.addEntry(getIntegerAttribute("tag", attributes), entry);
    }

    return result;
  }


  public String toString() {
    return heap.toString();
  }


  private final Heap heap;
}
