/* $Id$ */

package org.podval.imageio;

import java.util.ArrayList;

import java.io.IOException;


public final class Record extends Entry {

  public Record(String name) {
    super(name);
  }


  public Record(String name, Type type) {
    super(name, type);
  }


  protected boolean checkType() {
    return getType().isRecordAllowed();
  }


  public boolean isVector() {
    return isVector;
  }


  public void setIsVector(boolean value) {
    if (value && !getType().isVectorAllowed()) {
      throw new IllegalArgumentException("Can not be a vector");
    }

    isVector |= value;
  }


  /** @todo setCount() */


  public int getCount() {
    return count;
  }


  public Field getDefaultField() {
    /** @todo check that there are no other fields - and that there won't be! */
    if (fields.isEmpty()) {
      addField(0, new Field(getName(), getType()));
    }

    return fields.get(0);
  }


  public void addField(int index, Field field) {
    if (!getType().isFieldAllowed(field.getType())) {
      throw new IllegalArgumentException(field + " is not allowed in " + this);
    }

    ensureSize(index+1);

    if (fields.get(index) != null) {
      throw new IllegalArgumentException("Attempt to change field");
    }

    fields.set(index, field);
  }


  private void ensureSize(int size) {
    fields.ensureCapacity(size);
    for (int i = fields.size(); i<size; i++) {
      fields.add(i, null);
    }
  }


  public Field getField(int index) {
    return ((fields != null) && (index < fields.size())) ? fields.get(index) : null;
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    /** @todo variable length fields */

    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (getCount() > 1) || isVector()) && !type.isVariableLength();

    if (treatAsFolder) {
      if (reader.getHandler().startFolder(tag, getName())) {
        for (int index = 0; index < count; index++) {
          Field field = reader.getMetaMetaData().getField(this, index);
          Type fieldType = field.getType();
          if (!isVector() || (index != 0)) {
            handleRecord(reader, offset, index, fieldType, 1, field);
          } else {
            /** @todo check vector length */
          }
          offset += fieldType.getLength();
        }
      }

      reader.getHandler().endFolder();

    } else {
      handleRecord(reader, offset, tag, type, count, getDefaultField());
    }
  }


  private void handleRecord(Reader reader, long offset, int tag, Type type, int count, Field field)
    throws IOException
  {
    field.read(reader, offset, count, tag, type);
  }


  protected String getKind() {
    return "Record";
  }


  private boolean isVector;


  private int count;


  private ArrayList<Field> fields = new ArrayList<Field>(1);
}
