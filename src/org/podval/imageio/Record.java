package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;


public class Record extends Typed {

  public static Record define(org.podval.imageio.jaxb.Record xml) {
    String name = xml.getName();
    Record result = (Record) records.get(name);

    if (result == null) {
      result = new Record(name);
      records.put(name, result);
    }

    result.add(xml);

    return result;
  }


//  public static Record use(String name) {
//    Record result = (Record) records.get(name);
//
//    if (result == null)
//      throw new IllegalArgumentException("Undefined record " + name);
//
//    return result;
//  }


  private static final Map records = new HashMap();


  public Record(String name) {
    super(name);
  }


  public String getKind() {
    return "Record";
  }


  protected boolean checkType() {
    return getType().isRecordAllowed();
  }


  private void add(org.podval.imageio.jaxb.Record xml) {
    super.add(xml);

    setIsVector(xml);
    setCount(xml);

    org.podval.imageio.jaxb.Enumeration enumeration = xml.getEnumeration();
    if (enumeration != null) {
      /** @todo check the count */
      addField(1, new Field(getName(), getType().getDefaultFieldType(), enumeration));
    } else
      addFields(xml.getFields());
  }


  private void setIsVector(org.podval.imageio.jaxb.Record xml) {
    if (xml.isSetVector()) {
      Boolean isVector = Boolean.valueOf(xml.isVector());
      if (this.isVector == null) {
        this.isVector = isVector;
        if (isVector() && (!getType().isVectorAllowed() || getType().isVariableLength())) {
          throw new IllegalArgumentException(this+" can not be a vector.");
        }
      } else {
        if (this.isVector != isVector)
          throw new IllegalArgumentException("Attempt to change vectorness of " + this);
      }
    }
  }


  private boolean isVector() {
    boolean result = false;
    if (isVector != null)
      result = isVector.booleanValue();
    return result;
  }


  private void setCount(org.podval.imageio.jaxb.Record xml) {
    Object o = xml.getCount();
    if (o != null) {
      int count = (o instanceof Integer) ? ((Integer) o).intValue() : 0;
      if (!isSetCount) {
        this.count = count;
        isSetCount = true;
      } else {
        if (this.count!=count)
          throw new IllegalArgumentException("Attempt to change count of "+this);
      }
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


  private void addFields(List fields) {
    int index = 0;
    for (Iterator i = fields.iterator(); i.hasNext();) {
      org.podval.imageio.jaxb.Field fieldXml =
        (org.podval.imageio.jaxb.Field) i.next();

      if (fieldXml.isSetIndex())
        index = fieldXml.getIndex();
      else
        index++;

      Field field = new Field(fieldXml);

      addField(index, field);
    }
  }


  private Field getField(int index) {
    fields.ensureCapacity(index);
    for (int i = fields.size(); i<index; i++)
      fields.add(i, null);

    return (Field) fields.get(index-1);
  }


  private void addField(int index, Field field) {
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


  public Entry readWithLength(ImageInputStream in, Type type, long length) throws IOException {
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

    return readWithCount(in, type, count);
  }


  public Entry readWithCount(ImageInputStream in, Type type, long count) throws IOException {
    Group result = new Group(getName());

    if (isVector()) {
      long length = in.readUnsignedShort();

      if ((length % 2) != 0)
        throw new IOException("Odd vector length!");

      if ((length / 2)  != count)
        throw new IOException("Wrong vector length!");

      count--;
    }

    if ((fields.size() == 0) && ((count == 1) || (type.isVariableLength()))) {
      addField(1, new Field(getName(), getType().getDefaultFieldType()));
    }


    if (!getType().isVariableLength()) {
      for (int index=1; index<=count; index++)
        readField(in, result, index, type, 1);
    } else {
      for (int index=1; index<=fields.size(); index++) {
        /** @todo correct count for the many-fields case */
        readField(in, result, index, type, count);
      }
    }

    return result.flatten();
  }


  private void readField(ImageInputStream in, Group result, int index, Type type, long count) throws IOException {
    Field field = getField(index);

    if ((field == null) && MetaMetadata.isDecodeUnknown()) {
      Type fieldType = type.getDefaultFieldType();
      field = new Field("unknown-" + index + "-" + fieldType, fieldType);
      addField(index, field);
    }

    if (field != null) {
      result.addEntry(field.read(in, type, count));
    }
  }


  private Boolean isVector = null;


  private long count;


  private boolean isSetCount;


  private final ArrayList fields = new ArrayList();
}
