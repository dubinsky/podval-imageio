/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ExifReader;

import java.io.IOException;


public class MakerNoteMarker extends Entry {

  public MakerNoteMarker() throws MetaMetaDataException {
    /** @todo we do not really need a name here... */
    /** @todo we do not really need a type here either!!! */
    super("makerNoteMarker");
  }


  protected final void checkType() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    if (!(reader instanceof ExifReader)) {
      throw new IOException("Maker note only allowed in EXIF!");
    }

    String make = ((ExifReader) reader).getMake();

    if (make != null) {
      MakerNote.read(make, reader, offset, length, tag);
    }
  }


  protected Kind getKind() {
    return Kind.MAKER_NOTE_MARKER;
  }
}
