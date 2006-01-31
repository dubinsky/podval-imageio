/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public final class MakerNote extends Heap {

  public static MakerNote get(String name, String make, String signature) {
    MakerNote result = new MakerNote(name, make, signature);
    make2note.put(make, result);
    return result;
  }


  public static MakerNote get(String make) {
    return make2note.get(make);
  }


  private static final Map<String, MakerNote> make2note = new HashMap<String, MakerNote>();


  public MakerNote(String name, String maker, String signature) {
    super(name);
    this.maker = maker;
    this.signature = signature;
  }


  protected void checktype() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    reader.seek(offset);
    /** @todo read signature */
    readInPlace(reader, offset, length, tag, false);
  }


  private final String maker;


  private final String signature;
}
