/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;


public final class Record extends Entry {

  public Record(String name) {
    super(name);
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


  public boolean hasDefaultField() {
    return hasDefaultField;
  }


  public Field getDefaultField() throws MetaMetaDataException {
    if (fields.isEmpty()) {
      addField(0, getName());
      hasDefaultField = true;
    }

    return fields.get(0);
  }


  private void addField(int index, String name) throws MetaMetaDataException {
    if (hasDefaultField()) {
      throw new MetaMetaDataException("Fields can not be added to a record with a default field");
    }

    addField(index, new Field(name));
  }


  public void addField(int index, Field field) throws MetaMetaDataException {
    if (field.getType() == null) {
      field.setType(getType());
    }

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
            Field field = getField(index);
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
        reader.endFolder();
      }

    } else {
      try {
        handleField(reader, offset, tag, type, count, getDefaultField());
      } catch (MetaMetaDataException e) {
        throw new IOException(e.getMessage());
      }
    }
  }


  private Field getField(int index) throws MetaMetaDataException {
    if ((fields == null) || (index >= fields.size()) || (fields.get(index) == null)) {
      addField(index, "unknown-" + index);
    }

    return fields.get(index);
  }


  private void handleField(Reader reader, long offset, int tag, Type type, int count, Field field)
    throws IOException
  {
    field.read(reader, offset, count, tag, type);
  }


  protected Kind getKind() {
    return Kind.RECORD;
  }


  private boolean isVector;


  private int count;


  private ArrayList<Field> fields = new ArrayList<Field>(1);


  private boolean hasDefaultField;
}
