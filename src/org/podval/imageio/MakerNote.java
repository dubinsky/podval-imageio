/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public class MakerNote extends Heap {

  public MakerNote(String name, String maker, String signature) {
    super(name, null);
    this.maker = maker;
    this.signature = signature;
  }


  public void read(Reader reader, long offset, int length, int tag, TypeNG type)
    throws IOException
  {
    /** @todo read signature */
    read(reader, offset, length, tag, false);
  }


  private final String maker;


  private final String signature;
}
