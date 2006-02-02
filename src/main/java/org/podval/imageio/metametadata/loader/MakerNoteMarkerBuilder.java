/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.MakerNoteMarker;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class MakerNoteMarkerBuilder extends EntryBuilder {

  public MakerNoteMarkerBuilder(Builder previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous);
    this.makerNoteMarker = new MakerNoteMarker(getType(attributes));
  }


  public Entry getEntry() {
    return makerNoteMarker;
  }


  public Builder startElement(String name, Attributes attributes) {
    return null;
  }


  private final MakerNoteMarker makerNoteMarker;
}
