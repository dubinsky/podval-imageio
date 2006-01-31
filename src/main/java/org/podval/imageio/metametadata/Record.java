/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;


public final class Record extends Entry {

  public Record(String name) throws MetaMetaDataException {
    super(name);
  }


  public Record(String name, Type type) throws MetaMetaDataException {
    super(name, type);
  }


  protected void checkType() throws MetaMetaDataException {
    if (!getType().isRecordAllowed()) {
      throw new MetaMetaDataException("Wrong record type: " + this);
    }
  }


  public boolean isVector() {
    return isVector;
  }


  public void setIsVector(boolean value) throws MetaMetaDataException {
    if (value && !getType().isVectorAllowed()) {
      throw new MetaMetaDataException("Can not be a vector: " + this);
    }

    isVector |= value;
  }


  /** @todo setCount() */


  public int getCount() {
    return count;
  }


  public Field getDefaultField() throws MetaMetaDataException {
    /** @todo check that there are no other fields - and that there won't be! */
    if (fields.isEmpty()) {
      addField(0, new Field(getName(), getType()));
    }

    return fields.get(0);
  }


  public void addField(int index, Field field) throws MetaMetaDataException {
    if (!getType().isFieldAllowed(field.getType())) {
      throw new MetaMetaDataException(field + " of this type is not allowed in " + this);
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


  public List<Field> getFields() {
    return Collections.unmodifiableList(fields);
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    /** @todo variable length fields */

    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (getCount() > 1) || isVector()) && !type.isVariableLength();

    if (treatAsFolder) {
      if (reader.startFolder(tag, getName())) {
        for (int index = 0; index < count; index++) {
          try {
            Field field = reader.getMetaMetaData().getField(this, index);
            Type fieldType = field.getType();
            if (!isVector() || (index != 0)) {
              handleField(reader, offset, index, fieldType, 1, field);
            } else {
              /** @todo check vector length */
            }
            offset += fieldType.getLength();
          } catch (MetaMetaDataException e) {
            throw new IOException(e.getMessage());
          }
        }
      }

      reader.endFolder();

    } else {
      try {
        handleField(reader, offset, tag, type, count, getDefaultField());
      } catch (MetaMetaDataException e) {
        throw new IOException(e.getMessage());
      }
    }
  }


  private void handleField(Reader reader, long offset, int tag, Type type, int count, Field field)
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
