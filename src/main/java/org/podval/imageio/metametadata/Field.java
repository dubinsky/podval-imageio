/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.ReaderHandler;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.LinkedList;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


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
    ImageInputStream is = reader.getInputStream();

    if (!hasSubFields()) {
      ReaderHandler.ValueAction action = reader.atValue(tag, getName(), count);

      if ((action != null) && (action != ReaderHandler.ValueAction.SKIP)) {
        reader.seek(offset);

        if (action == ReaderHandler.ValueAction.RAW) {
          reader.handleRawValue(tag, getName(), count, is);
        } else {
          Object value = processValue(type.read(is, count));
          reader.handleValue(tag, getName(), count, value);
        }
      }

    } else {
      if (count != 1) {
        throw new IOException("Count must be 1 for fields with sub-fields");
      }

      if (reader.startFolder(tag, getName())) {
        int value = ((Integer) type.read(is));
        int index = 0;

        for (Field subField : subFields) {
          int numBits = subField.getType().getLength() * 8;
          subField.handleValue(reader, index, value >> (32 - numBits));

          value = value << numBits;
          index++;
        }

        reader.endFolder();
      }
    }
  }


  private void handleValue(Reader reader, int index, int value)
    throws IOException
  {
    reader.handleValue(index, getName(), processValue(value));
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


  private Object processValue(Object value) throws IOException {
    Enumeration enumeration = getEnumeration();

    if (conversion != null) {
      value = convert(conversion, value);
    }

    if (enumeration != null) {
      value = enumeration.getValue(value);
    }

    return value;
  }


  private static Object convert(String conversion, Object value)
    throws IOException
  {
    int lastDot = conversion.lastIndexOf(".");
    if (lastDot == -1) {
      throw new IOException("No \".\" in the conversion name");
    }

    String className = conversion.substring(0, lastDot);
    String methodName = conversion.substring(lastDot+1, conversion.length());

    Class clazz;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IOException("Conversion class not found: " + className);
    }

    Class valueClass = value.getClass();

    Class type;
    if (valueClass == Integer.class) {
      type = Integer.TYPE;
    } else {
      type = valueClass;
    }

    Method method;
    try {
      method = clazz.getMethod(methodName, type);
    } catch (NoSuchMethodException e) {
      throw new IOException("Conversion method not found: " + conversion + "(" + value.getClass().getName() + ")");
    }

    try {
      value = method.invoke(null, value);
    } catch (IllegalAccessException e) {
      throw new IOException("Can't invoke conversion: " + conversion);
    } catch (InvocationTargetException e) {
      throw new IOException("Can't invoke conversion: " + conversion);
    }

    return value;
  }


  private String conversion;


  private Enumeration enumeration;


  private List<Field> subFields;
}
