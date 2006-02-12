/* $Id$ */

package org.podval.imageio.metametadata;

import java.util.Map;
import java.util.HashMap;

import java.lang.NoSuchFieldException;


public final class Enumeration {

  public Enumeration(String className) {
    this.className = className;
  }


  public void addItem(int tag, EnumerationItem item) {
    EnumerationItem oldItem = items.get(tag);
    if (oldItem != null)
      throw new IllegalArgumentException(
        "Attempt to change item for " + tag +
        " from " + oldItem +
        " to " + item
      );

    items.put(tag, item);
  }


  public Object getValue(int tag) {
    EnumerationItem item = items.get(tag);

    if (item == null) {
      item = new EnumerationItem(tag);
      addItem(tag, item);
    }

    Object result = (item.value != null) ? item.value : item.name;

    if (className != null) {
      try {
        Class clazz = Class.forName(className);
////        Object x = Enum.valueOf(clazz, item.name);
      } catch (ClassNotFoundException e) {
      }
//      try {
//        /** @todo check that the field is static... */
//        value = enumClass.getField(name).get(null);
//      } catch (NoSuchFieldException e) {
//        /** @todo ignore? */
//      } catch (IllegalAccessException e) {
//      /** @todo ignore? */
//      }
    }

    return result;
  }


  public Object getValue(Object result) {
    if (result instanceof Integer) {
      result = getValue(((Integer) result).intValue());
    }

    return result;
  }


  private final String className;


  private final Map<Integer, EnumerationItem> items = new HashMap<Integer, EnumerationItem>();
}
