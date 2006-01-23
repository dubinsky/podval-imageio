/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public class MakerNoteMarker extends Entry {

  public MakerNoteMarker(TypeNG type) {
    /** @todo we do not really need a name here... */
    /** @todo we do not really need a type here either!!! */
    super("makerNoteMarker", type);
  }


  public void read(Reader reader, long offset, int length, int tag, TypeNG type)
    throws IOException
  {
    MakerNote makerNote = null;

    String make = reader.getHandler().getMake();

    if (make != null) {
      makerNote = reader.getMetaMetaData().getMakerNote(make);
    }

    if (makerNote == null) {
      makerNote = new MakerNote("unknown-maker-note", "unknown", null);
    }

//    makerNote.read(reader, offset, length, tag, type);
//    new RecordNG("unknown-maker-note", type).read(reader, offset, length, tag, type);
  }
}
