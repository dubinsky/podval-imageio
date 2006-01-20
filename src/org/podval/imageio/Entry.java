/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public abstract class Entry {

  public enum Kind { HEAP, RECORD, UNKNOWN }


  protected Entry(String name, TypeNG type) {
//    if (type == null) {
//      throw new NullPointerException("null type for " + name);
//    }

    this.name = name;
    this.type = type;
  }


  public final String getName() {
    return name;
  }


  public final TypeNG getType() {
    return type;
  }


  public abstract void read(Reader reader, long offset, int length, int tag, TypeNG type)
    throws IOException;


  private final String name;


  private final TypeNG type;
}
