/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ExifReader;

import java.io.IOException;


public final class MakerNoteMarker extends Entry {

  public MakerNoteMarker() throws MetaMetaDataException {
    /** @todo we do not really need a name here... */
    /** @todo we do not really need a type here either!!! */
    super("makerNoteMarker");
  }


  protected void checkType() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    if (!(reader instanceof ExifReader)) {
      throw new IOException("Maker note only allowed in EXIF!");
    }

    String make = ((ExifReader) reader).getMake();

    if (make != null) {
      String readerClassName = reader.getMetaMetaData().getMakerNoteReaderClassName(make);
      if (readerClassName != null) {
        read(make, reader, readerClassName, offset, length, tag);
      }
    }
  }


  private void read(String make, Reader reader, String readerClassName, long offset, int length, int tag)
    throws IOException
  {
    Class clazz;
    try {
      clazz = Class.forName(readerClassName);
    } catch (ClassNotFoundException e) {
      throw new IOException("Reader class not found: " + readerClassName);
    }

    Object readerInstance;
    try {
      readerInstance = clazz.newInstance();
    } catch (InstantiationException e) {
      throw new IOException(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new IOException(e.getMessage());
    }

    ((MakerNoteReader) readerInstance).read(make, reader, offset, length, tag);
  }


  protected Kind getKind() {
    return Kind.MAKER_NOTE_MARKER;
  }
}
