/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public abstract class Entry implements Parent {

  public enum Kind { HEAP, RECORD, FIELD, MAKER_NOTE_MARKER, UNKNOWN }


  protected Entry(String name) {
    /** @todo  */
//    if (name == null) {
//      throw new MetaMetaDataException("Entry must have a name");
//    }

    this.name = name;
  }


  public final String getName() {
    return name;
  }


  public final void setParent(Parent value) {
    parent = value;
  }


  public final Parent getParent() {
    return parent;
  }


  public final MetaMetaData getMetaMetaData() {
    Parent result = this;

    while (result.getParent() != null) {
      result = result.getParent();
    }

    return (MetaMetaData) result;
  }


  protected void checkKind(Kind kind) throws MetaMetaDataException {
    if ((kind != Kind.UNKNOWN) && (kind != getKind())) {
      throw new MetaMetaDataException("Wrong kind: " + getKind() + " instead of " + kind);
    }
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


  protected abstract Kind getKind();


  public abstract void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException;


  private final String name;


  private Parent parent;


  private Type type;


  private boolean isSkip;
}
