/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.xml.sax.Attributes;

import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.MetaMetaDataException;


public final class HeapBuilder extends EntryBuilder<Heap> {

  public HeapBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, previous.getMetaMetaData().getHeap(getName(attributes)), attributes);
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    EntryBuilder<?> result = null;

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
      thing.addEntry(getIntegerAttribute("tag", attributes), result.thing);
    }

    return result;
  }
}
