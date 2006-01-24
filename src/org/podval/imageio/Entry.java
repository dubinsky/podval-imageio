/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public abstract class Entry {

  public enum Kind { HEAP, RECORD, UNKNOWN }


  protected Entry(String name) {
    this(name, null);
  }


  protected Entry(String name, TypeNG type) {
    this.name = name;
    setType(type);
  }


  public final String getName() {
    return name;
  }


  public final void setType(TypeNG value) {
    if (value != null) {
      if ((type != null) && (type != value)) {
        throw new IllegalStateException("Attempt to change the type");
      }

      if (!checkType()) {
        throw new IllegalArgumentException("Wrong type: " + this);
      }

      type = value;
    }
  }


  public final TypeNG getType() {
    return type;
  }


  protected abstract boolean checkType();


  public abstract void read(Reader reader, long offset, int length, int tag, TypeNG type)
    throws IOException;


  private final String name;


  private TypeNG type;
}
