/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public final class MakerNoteBuilder extends Builder<String> {

  public MakerNoteBuilder(Builder<?> previous, Attributes attributes)
    throws MetaMetaDataException
  {
    super(previous, getRequiredAttribute("make", attributes));

    getMetaMetaData().addMakerNoteReaderClass(
      thing,
      getRequiredAttribute("class", attributes)
    );
  }


  public Builder<?> startElement(String name, Attributes attributes) {
    return null;
  }
}
