package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.lang.NoSuchFieldException;


public class Enumeration {

  public Enumeration(Class enumClass) {
    this.enumClass = enumClass;
  }


  public void addValue(int tag, String name) {
    Object oldValue = getRawValue(tag);
    if (oldValue != null)
      throw new IllegalArgumentException(
        "Attempt to change value for " + tag +
        " from " + oldValue +
        " to " + name
      );

    Object value = name;
    if (enumClass != null) {
      try {
        /** @todo check that the field is static... */
        value = enumClass.getField(name).get(null);
      } catch (NoSuchFieldException e) {
        /** @todo ignore? */
      } catch (IllegalAccessException e) {
      /** @todo ignore? */
      }
    }


    values.put(new Integer(tag), value);
  }


  private Object getRawValue(int tag) {
    return values.get(new Integer(tag));
  }


  public Object getValue(int tag) {
    Object result = getRawValue(tag);

    if (result == null) {
      String unknown = "unknown-" + tag;
      result = unknown;
      addValue(tag, unknown);
    }

    return result;
  }


  private final Class enumClass;


  private final Map values = new HashMap();
}
