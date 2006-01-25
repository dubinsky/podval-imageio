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


  public int getCount() {
    return count;
  }


  public void addConversion(String value) {
    if ((value != null) && (conversion != null)) {
      if (value != conversion) {
        throw new IllegalArgumentException("Attempt to change conversion");
      }
    }

    conversion = value;
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
      if (reader.getHandler().startFolder(tag, getName())) {
        for (int index = 0; index < count; index++) {
          Record field = reader.getMetaMetaData().getField(this, index);
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
      handleRecord(reader, offset, tag, type, count, this);
    }
  }


  private void handleRecord(Reader reader, long offset, int tag, Type type, int count, Record field)
    throws IOException
  {
    reader.handleRecord(offset, tag, type, count, field);
//    field.read(reader, offset, count, tag, type);
  }


  protected String getKind() {
    return "Record";
  }


  private boolean isVector;


  private int count;


  private String conversion;


  private Enumeration enumeration;


  private ArrayList<Record> fields;
}
