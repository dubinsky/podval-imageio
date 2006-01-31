/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ReaderHandler;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;


public final class Field extends Entry {

  public Field(String name, Type type) throws MetaMetaDataException {
    super(name, type);
  }


  protected void checkType() {
  }


  public String getKind() {
    return "Field";
  }


  public void setConversion(String value) throws MetaMetaDataException {
    if ((value != null) && (conversion != null) && (value != conversion)) {
        throw new IllegalArgumentException("Attempt to change conversion");
    }

    conversion = value;

    check();
  }


  public void setEnumeration(Enumeration value) throws MetaMetaDataException {
    if (!getType().isEnumerationAllowed()) {
      throw new MetaMetaDataException("Enumeration is not allowed for " + this);
    }

    enumeration = value;

    check();
  }


  public Enumeration getEnumeration() {
    return enumeration;
  }


  public void addSubField(Field field) throws MetaMetaDataException {
    if (!getType().isSubFieldAllowed(field.getType())) {
      throw new MetaMetaDataException(field + " of this type is not allowed in " + this);
    }

    if (!hasSubFields()) {
      subFields = new LinkedList<Field>();
    }

    subFields.add(field);

    check();
  }


  public void checkSubFields() throws MetaMetaDataException {
    if (hasSubFields()) {
      int length = 0;
      for (Field field : subFields) {
        length += field.getType().getLength();
      }

      if (length != getType().getLength()) {
        throw new MetaMetaDataException("Wrong total length of sub-fields in " + this);
      }
    }
  }


  public void read(Reader reader, long offset, int count, int tag, Type type)
    throws IOException
  {
    if (!hasSubFields()) {
      ReaderHandler.ValueAction action = reader.atValue(tag, getName(), count);

      if ((action != null) && (action != ReaderHandler.ValueAction.SKIP)) {
        reader.seek(offset);

        switch (action) {
        case RAW  : reader.handleRawValue(tag, getName(), count, reader.getInputStream());        break;
        case VALUE: reader.handleValue   (tag, getName(), count, readValue(reader, type, count)); break;
        }
      }

    } else {
      if (count != 1) {
        throw new IOException("Count must be 1 for fields with sub-fields");
      }

      if (reader.startFolder(tag, getName())) {
        if (type != Type.U32) {
          throw new IOException("Reading of subfields implemented for fields of type U32 only");
        }

        int value = ((Integer) type.read(reader.getInputStream()));
        int index = 0;

        for (Field subField : subFields) {
          /** @todo redo with the number of bits */

          Type subFieldType = subField.getType();
          int subFieldValue;

          if (subFieldType == Type.U16) {
            subFieldValue = (value >> 16) & 0x0000FFFF;
            value = value << 16;
          } else
          if (subFieldType == Type.U8) {
            subFieldValue = (value >> 24) & 0x000000FF;
            value = value << 8;
          } else {
            throw new IOException("Sub-field reading not implemented for type " + subFieldType);
          }

          reader.handleValue(index, subField.getName(), subField.processEnumeration(subFieldValue));

          index++;
        }

        reader.endFolder();
      }
    }
  }


  private Object readValue(Reader reader, Type type, int count) throws IOException {
    /** @todo type/count sanity checks... */
    Object result = null;

    if (type == Type.STRING) {
      result = readString(readBytes(reader, count));
    } else {
      if (count == 1) {
        result = processEnumeration(type.read(reader.getInputStream()));
      } else {
        if ((type == Type.U8) || (type == Type.X8)) {
          result = readBytes(reader, count);
        } else {
          result = readValues(reader, type, count);
        }
      }
    }

    return result;
  }


  /** @todo this should also take care of the conversion!!! */
  private Object processEnumeration(Object result) {
    Enumeration enumeration = getEnumeration();

    if (enumeration != null) {
      result = enumeration.getValue(result);
    }

    return result;
  }


  private String readString(byte[] bytes) throws IOException {
    /** @todo length of 0 indicates 'indefinite'. Limit 'em here? */

    int l = 0;
    for (; l<bytes.length; l++) {
      if (bytes[l] == 0) {
        break;
      }
    }

//      if (l == length) {
////        result.append("|NO ZERO. TRUNCATED?");
//      }

    return new String(bytes, 0, l).trim();
  }


  private byte[] readBytes(Reader reader, int length) throws IOException {
    byte[] result = new byte[length];
    reader.getInputStream().readFully(result);
    return result;
  }


  private Object[] readValues(Reader reader, Type type, int count)
    throws IOException
  {
    Object[] result = new Object[count];
    for (int i = 0; i < count; i++) {
      result[i] = type.read(reader.getInputStream());
    }
    return result;
  }


  private boolean hasSubFields() {
    return (subFields != null);
  }


  private void check() throws MetaMetaDataException {
    if ((hasSubFields() && (conversion != null)) ||
        (hasSubFields() && (enumeration != null)) ||
        ((conversion != null) && (enumeration != null)))
    {
      throw new MetaMetaDataException("Only one of sub-fields, conversion and enumeration is allowed");
    }
  }


  private String conversion;


  private Enumeration enumeration;


  private List<Field> subFields;
}
