/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public final class Field extends Entry {

  public Field(String name, Type type) throws MetaMetaDataException {
    super(name, type);
  }


  protected void checkType() {
    /** @todo  */
  }


  public String getKind() {
    return "Field";
  }


  public void setConversion(String value) {
    if ((value != null) && (conversion != null)) {
      if (value != conversion) {
        throw new IllegalArgumentException("Attempt to change conversion");
      }
    }

    conversion = value;
  }


  public void setEnumeration(Enumeration value) throws MetaMetaDataException {
    if (!getType().isEnumerationAllowed()) {
      throw new MetaMetaDataException("Enumeration is not allowed for " + this);
    }
    enumeration = value;
  }


  public Enumeration getEnumeration() {
    return enumeration;
  }


  public void read(Reader reader, long offset, int count, int tag, Type type)
    throws IOException
  {
    ReaderHandler.ValueAction action = reader.getHandler().atValue(tag, getName(), type, count);

    if ((action != null) && (action != ReaderHandler.ValueAction.SKIP)) {
      reader.seek(offset);

      switch (action) {
      case RAW  : reader.getHandler().handleRawValue(tag, getName(), type, count, reader.getInputStream());        break;
      case VALUE: reader.getHandler().handleValue   (tag, getName(), type, count, readValue(reader, type, count)); break;
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
        result = type.read(reader.getInputStream());
        Enumeration enumeration = getEnumeration();
        if (enumeration != null) {
          if (result instanceof Integer) {
            result = enumeration.getValue((Integer) result);
          }
        }
      } else {
        if ((type == Type.U8) || (type == Type.X8)) {
          result = readBytes(reader, count);
        } else {
          Object[] objects = new Object[count];
          for (int i = 0; i<count; i++) {
            objects[i] = type.read(reader.getInputStream());
          }
          result = objects;
        }
      }
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


  private String conversion;


  private Enumeration enumeration;
}
