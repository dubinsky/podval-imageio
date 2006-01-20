/* $Id$ */

package org.podval.imageio;

import java.util.ArrayList;

import java.io.IOException;


public final class RecordNG extends Entry {

  public RecordNG(String name, TypeNG type) {
    super(name, type);
  }


  public boolean isVector() {
    return isVector;
  }


  public void addIsVector(boolean value) {
    isVector |= value;
  }


  public void addSkip(boolean value) {
    skip |= value;
  }


  public void addConversion(String value) {
    if ((value != null) && (conversion != null)) {
      if (value != conversion) {
        throw new IllegalArgumentException("Attempt to change conversion");
      }
    }
  }


  public int getCount() {
    return count;
  }


  public void addField(int index, RecordNG field) {
    /** @todo  */
    if (fields == null) {
      fields = new ArrayList<RecordNG>();
    }

    ensureSize(index+1);

    /** @todo check that we do not change the field... */
    fields.set(index, field);
  }


  private void ensureSize(int size) {
    fields.ensureCapacity(size);
    for (int i = fields.size(); i<size; i++) {
      fields.add(i, null);
    }
  }


  public RecordNG getField(int index) {
    RecordNG result = null;

    if ((fields != null) && (index < fields.size())) {
      result = fields.get(index);
    }

    return result;
  }


  public void setEnumeration(Enumeration value) {
    /** @todo checks? */
    enumeration = value;
  }


  public Enumeration getEnumeration() {
    return enumeration;
  }


  public void read(Reader reader, long offset, int length, int tag, TypeNG type)
    throws IOException
  {
    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (getCount() > 1) || isVector()) && !type.isVariableLength;

    if (treatAsFolder) {
      if (reader.getHandler().startRecord(tag, getName())) {
        for (int index = 0; index < count; index++) {
          RecordNG field = reader.getMetaMetaData().getField(this, index);
          TypeNG fieldType = field.getType();
          int fieldLength = fieldType.getLength();
          if (!isVector() || (index != 0)) {
            reader.handleRecord(offset, index, fieldType, 1, field);
          } else {
            /** @todo check vector length */
          }
          offset += fieldLength;
        }
      }

      reader.getHandler().endRecord();

    } else {
      /* It is much simpler to just do seek right here, but if the data is not
       needed, the seek() would be wasted... */
      reader.handleRecord(offset, tag, type, count, this);
    }
  }


  private boolean isVector;


  private int count;


  private boolean skip;


  private String conversion;


  private Enumeration enumeration;


  private ArrayList<RecordNG> fields;
}
