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

    this.makerNote = MakerNote.get(
      getAttribute("make", attributes),
      attributes.getValue("signature"),
      attributes.getValue("directory")
    );
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }


  public String toString() {
    return makerNote.toString();
  }


  private final MakerNote makerNote;
}
