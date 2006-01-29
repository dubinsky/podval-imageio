/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.Type;

import java.io.IOException;


public class MakerNote extends Heap {

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
