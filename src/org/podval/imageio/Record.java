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


  public void addIsVector(boolean value) {
    if (value && !getType().isVectorAllowed()) {
      throw new IllegalArgumentException("Can not be a vector");
    }

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

    conversion = value;
  }


  public int getCount() {
    return count;
  }


  public void addField(int index, Record field) {
    if (fields == null) {
      fields = new ArrayList<Record>(index+1);
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


  public Record getField(int index) {
    Record result = null;

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


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    /** @todo default field */
    /** @todo variable length fields */

    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (getCount() > 1) || isVector()) && !type.isVariableLength();

    if (treatAsFolder) {
      if (reader.getHandler().startRecord(tag, getName())) {
        for (int index = 0; index < count; index++) {
          Record field = reader.getMetaMetaData().getField(this, index);
          Type fieldType = field.getType();
          if (!isVector() || (index != 0)) {
            reader.handleRecord(offset, index, fieldType, 1, field);
          } else {
            /** @todo check vector length */
          }
          offset += fieldType.getLength();
        }
      }

      reader.getHandler().endRecord();

    } else {
      reader.handleRecord(offset, tag, type, count, this);
    }
  }


  protected String getKind() {
    return "Record";
  }


  private boolean isVector;


  private int count;


  private boolean skip;


  private String conversion;


  private Enumeration enumeration;


  private ArrayList<Record> fields;
}
