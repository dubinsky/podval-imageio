package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;


public class Record extends Typed {

  private static final Map records = new HashMap();


  public static Record get(String name) {
    return (Record) records.get(name);
  }


  public static void add(Record record) {
    /** @todo sanity check: not yet there. */
    records.put(record.getName(), record);
  }


  public Record(String name) {
    super(name);
  }


  public String getKind() {
    return "Record";
  }


  protected boolean checkType() {
    return getType().isRecordAllowed();
  }


  public void setIsVector(boolean value) {
    Boolean isVector = Boolean.valueOf(value);
    if (this.isVector==null) {
      this.isVector = isVector;
      if (isVector() && (!getType().isVectorAllowed() || getType().isVariableLength())) {
        throw new IllegalArgumentException(this+" can not be a vector.");
      }
    } else {
      if (this.isVector!=isVector)
        throw new IllegalArgumentException("Attempt to change vectorness of "+this);
    }
  }


  private boolean isVector() {
    boolean result = false;
    if (isVector != null)
      result = isVector.booleanValue();
    return result;
  }


  public void setCount(int count) {
    if (!isSetCount) {
      this.count = count;
      isSetCount = true;
    } else {
      if (this.count!=count)
        throw new IllegalArgumentException("Attempt to change count of "+this);
    }
  }


  private long getCount() {
    long result;
    if (isSetCount)
      result = count;
    else {
      if (getType().isVariableLength())
        result = 0;
      else
        result = 1;
    }
    return result;
  }


  private Field getField(int index) {
    fields.ensureCapacity(index);
    for (int i = fields.size(); i<index; i++)
      fields.add(i, null);

    return (Field) fields.get(index-1);
  }


  public void addField(int index, Field field) {
    if (!getType().isFieldAllowed(field.getType()))
      throw new IllegalArgumentException(field + " is not allowed in " + this);

    Field oldField = getField(index);
    if (oldField != null) {
      throw new IllegalArgumentException(
        "Attempt to replace " + oldField +
        " with " + field +
        " in " + this);
    }

    if (getType().isVariableLength() && (index>1) && (getField(index-1) == null)) {
      throw new IllegalArgumentException("Hole in " + this + " before " + field);
    }

    fields.set(index-1, field);
  }


  public Field createDefaultField() {
    Field result = new Field(getName());
    result.setType(getType().getDefaultFieldType());
    return result;
  }


  public void readWithLength(ImageInputStream in, Type type, long length,
    MetadataHandler handler) throws IOException
  {
    // All STRUCTURED fields in a record MUST be of the same length.
    // This is enforced by the Type.isFieldAllowed predicate.

    long count;
    if (!getType().isVariableLength()) {
      int fieldLength = type.getLength();

      if ((length % fieldLength) != 0)
        throw new IOException("Odd record length!");

      count = length / fieldLength;
    } else {
      count = length;
    }

    readWithCount(in, type, count, handler);
  }


  public void readWithCount(ImageInputStream in, Type type, long count,
    MetadataHandler handler) throws IOException
  {
    if (isVector()) {
      long length = in.readUnsignedShort();

      if ((length % 2) != 0)
        throw new IOException("Odd vector length!");

      if ((length / 2)  != count)
        throw new IOException("Wrong vector length!");

      count--;
    }

    if ((fields.size() == 0) && ((count == 1) || (type.isVariableLength()))) {
      addField(1, createDefaultField());
    }


    long nmb;
    long cnt;
    if (!getType().isVariableLength()) {
      nmb = count;
      cnt = 1;
    } else {
      nmb = fields.size();
      cnt = count;
    }

    handler.startGroup(this);

    for (int index=1; index<=nmb; index++) {
      readField(in, index, type, cnt, handler);
    }

    handler.endGroup();
  }


  private void readField(ImageInputStream in, int index, Type type, long count,
    MetadataHandler handler) throws IOException
  {
    Field field = getField(index);

    if ((field == null) && MetaMetadata.isDecodeUnknown()) {
      Type fieldType = type.getDefaultFieldType();
      field = createUnknownField(index, fieldType);
      addField(index, field);
    }

    if (field != null) {
      field.read(in, type, count, handler);
    }
  }


  private Field createUnknownField(int index, Type type) {
    Field result = new Field("unknown-" + index + "-" + type);
    result.setType(type);
    return result;
  }


  private Boolean isVector = null;


  private long count;


  private boolean isSetCount;


  private final ArrayList fields = new ArrayList();
}
