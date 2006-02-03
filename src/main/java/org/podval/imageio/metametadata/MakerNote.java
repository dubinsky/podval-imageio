/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public final class MakerNote {

  public static void put(String make, String readerClassName) {
    make2note.put(make, readerClassName);
  }


  private static String getReaderClassName(String make) {
    return make2note.get(make);
  }


  public static void read(String make, Reader reader, long offset, int length, int tag)
    throws IOException
  {
    String readerClassName = getReaderClassName(make);
    if (readerClassName != null) {
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
  }


  private static final Map<String, String> make2note = new HashMap<String, String>();
}
