/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public class RootBuilder extends Builder {

  public RootBuilder(DocumentBuilder previous) {
    super(previous);
  }


  public Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder result = null;

    if ("directory".equals(name)) {
      result = new HeapBuilder(this, getHeap(attributes));
    } else

    if ("record".equals(name)) {
      result = new RecordBuilder(this, attributes);
    } else

    if ("makerNote".equals(name)) {
      result = new HeapBuilder(this, getMakerNote(attributes));
    }

    return result;
  }
}