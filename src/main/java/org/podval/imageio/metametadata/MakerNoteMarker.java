/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ExifReader;

import java.io.IOException;


public final class MakerNoteMarker extends Entry {

  public MakerNoteMarker(final MetaMetaData metaMetaData) {
    super(null);
    this.metaMetaData = metaMetaData;
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
      String readerClassName = metaMetaData.getMakerNoteReaderClassName(make);
      if (readerClassName != null) {
        instantiate(readerClassName).read(make, metaMetaData, reader, offset, length, tag);
      }
    }
  }


  private MakerNoteReader instantiate(String readerClassName)
    throws IOException
  {
    Class clazz;
    try {
      clazz = Class.forName(readerClassName);
    } catch (ClassNotFoundException e) {
      throw new IOException("Reader class not found: " + readerClassName);
    }

    Object result;
    try {
      result = clazz.newInstance();
    } catch (InstantiationException e) {
      throw new IOException(e.getMessage());
    } catch (IllegalAccessException e) {
      throw new IOException(e.getMessage());
    }

    return (MakerNoteReader) result;
  }


  protected Kind getKind() {
    return Kind.MAKER_NOTE_MARKER;
  }


  private final MetaMetaData metaMetaData;
}
