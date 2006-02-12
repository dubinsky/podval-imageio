/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class RootBuilder extends Builder<MetaMetaData> {

  public RootBuilder(DocumentBuilder previous, MetaMetaData metaMetaData) {
    super(previous, metaMetaData);
  }


  public Builder<?> startElement(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    Builder<?> result = null;

    if ("directory".equals(name)) {
      result = new HeapBuilder(this, attributes);
    } else

    if ("record".equals(name)) {
      result = new RecordBuilder(this, attributes);
    } else

    if ("makerNote".equals(name)) {
      result = new MakerNoteBuilder(this, attributes);
    }

    return result;
  }
}
