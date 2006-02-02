/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.xml.sax.Attributes;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.MetaMetaDataException;


public final class HeapBuilder extends EntryBuilder {

  public HeapBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);

    heap = getMetaMetaData().getHeap(getName(attributes));
    heap.setType(getType(attributes));
  }


  public Entry getEntry() {
    return heap;
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    EntryBuilder result = null;

    if ("directory".equals(name)) {
      result = new HeapBuilder(this, attributes);
    } else

    if ("record".equals(name)) {
      result = new RecordBuilder(this, attributes);
    } else

    if ("makerNoteMarker".equals(name)) {
      result = new MakerNoteMarkerBuilder(this, attributes);
    }

    if (result != null) {
      heap.addEntry(getIntegerAttribute("tag", attributes), result.getEntry());
    }

    return result;
  }


  private final Heap heap;
}
