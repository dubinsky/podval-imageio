/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public final class MakerNote implements Readable {

  public static MakerNote get(String make, String signature, String heapName) {
    MakerNote result = new MakerNote(make, signature, heapName);
    make2note.put(make, result);
    return result;
  }


  public static MakerNote get(String make) {
    return make2note.get(make);
  }


  private static final Map<String, MakerNote> make2note = new HashMap<String, MakerNote>();


  public MakerNote(String maker, String signature, String heapName) {
    this.maker = maker;
    this.signature = signature;
    this.heapName = heapName;
  }


  protected void checktype() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    reader.seek(offset);

    /** @todo read signature */

    reader.getMetaMetaData().getHeap(heapName).readInPlace(reader, offset, length, tag, false);
  }


  private final String maker;


  private final String signature;


  private final String heapName;
}
