/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MakerNoteMarker;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class MakerNoteMarkerBuilder extends EntryBuilder<MakerNoteMarker> {

  public MakerNoteMarkerBuilder(Builder<?> previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, new MakerNoteMarker(), attributes);
  }


  public Builder<?> startElement(String name, Attributes attributes) {
    return null;
  }
}
