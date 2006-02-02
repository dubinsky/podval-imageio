/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public final class MakerNote implements Readable {

  public static void put(String make, String readerClassName) {
    MakerNote result = new MakerNote(readerClassName);
    make2note.put(make, result);
  }


  public static MakerNote get(String make) {
    return make2note.get(make);
  }


  private static final Map<String, MakerNote> make2note = new HashMap<String, MakerNote>();


  public MakerNote(String readerClassName) {
    this.readerClassName = readerClassName;
  }


  protected void checktype() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
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

    ((Readable) readerInstance).read(reader, offset, length, tag, type);
  }


  private final String readerClassName;
}
