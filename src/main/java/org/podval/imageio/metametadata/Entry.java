/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public abstract class Entry { //implements Parent {

  public enum Kind { HEAP, RECORD, UNKNOWN }


  protected Entry(String name) {
    this.name = name;
  }


  public final String getName() {
    return name;
  }


  public final void setType(Type value) throws MetaMetaDataException {
    if (value != null) {
      if ((type != null) && (type != value)) {
        throw new MetaMetaDataException("Attempt to change the type of " + this + " to " + value);
      }

      type = value;

      checkType();
    }
  }


  public final Type getType() {
    return type;
  }


  protected abstract void checkType() throws MetaMetaDataException;


  public final void setSkip(boolean value) {
    isSkip |= value;
  }


  public final boolean isSkip() {
    return isSkip;
  }


  public final String toString() {
    return getKind() + " " + getName() + "::" + getType();
  }


  protected final String unknown(int tag) {
    return "unknown-" + tag;
  }


  protected abstract String getKind();


  public abstract void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException;


  private final String name;


  private Type type;


  private boolean isSkip;
}
