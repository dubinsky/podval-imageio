/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ExifReader;

import java.io.IOException;


public class MakerNoteMarker extends Entry {

  public MakerNoteMarker(Type type) throws MetaMetaDataException {
    /** @todo we do not really need a name here... */
    /** @todo we do not really need a type here either!!! */
    super("makerNoteMarker", type);
  }


  protected final void checkType() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    MakerNote makerNote = null;

    if (!(reader instanceof ExifReader)) {
      throw new IOException("Maker note only allowed in EXIF!");
    }

    String make = ((ExifReader) reader).getMake();

    if (make != null) {
      makerNote = MakerNote.get(make);
    }

    if (makerNote == null) {
      makerNote = new MakerNote("unknown-maker-note", "unknown", null);
    }

    makerNote.read(reader, offset, length, tag, type);
  }


  protected String getKind() {
    return "MakerNoteMarker";
  }
}
