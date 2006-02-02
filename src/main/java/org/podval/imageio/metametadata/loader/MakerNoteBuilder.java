/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.xml.sax.Attributes;

import org.podval.imageio.metametadata.MakerNote;
import org.podval.imageio.metametadata.MetaMetaDataException;


public final class MakerNoteBuilder extends Builder {

  public MakerNoteBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);

    MakerNote.put(
      getAttribute("make", attributes),
      getAttribute("class", attributes)
    );
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }
}
