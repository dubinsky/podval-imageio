/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public abstract class Entry {

  public enum Kind { HEAP, RECORD, UNKNOWN }


  protected Entry(String name) {
    this(name, null);
  }


  protected Entry(String name, Type type) {
    this.name = name;
    setType(type);
  }


  public final String getName() {
    return name;
  }


  public final void setType(Type value) {
    if (value != null) {
      if ((type != null) && (type != value)) {
        throw new IllegalStateException("Attempt to change the type");
      }

      type = value;

      if (!checkType()) {
        throw new IllegalArgumentException("Wrong type: " + this);
      }
    }
  }


  public final Type getType() {
    return type;
  }


  protected abstract boolean checkType();


  public final void addSkip(boolean value) {
    isSkip |= value;
  }


  public final boolean isSkip() {
    return isSkip;
  }


  public abstract void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException;


  public final String toString() {
    return getKind() + " " + getName() + "::" + getType();
  }


  protected abstract String getKind();


  private final String name;


  private Type type;


  private boolean isSkip;
}
