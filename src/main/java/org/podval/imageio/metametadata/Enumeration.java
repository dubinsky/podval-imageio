/* $Id$ */

package org.podval.imageio.metametadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;


public final class Enumeration {

  public Enumeration(String className) {
    /** @todo check (and split) the className */
    this.className = className;
  }


  public String getClassName() {
    return className;
  }


  public void addItem(int tag, EnumerationItem item)
    throws MetaMetaDataException
  {
    if ((className != null) && (item.name == null)) {
      throw new MetaMetaDataException("For enumeration associated with a Java class, all items must have names");
    }

    EnumerationItem oldItem = getItem(tag);
    if (oldItem != null)
      throw new MetaMetaDataException(
        "Attempt to change item for " + tag +
        " from " + oldItem +
        " to " + item
      );

    items.put(tag, item);
  }


  public Object getValue(int tag) {
    EnumerationItem item = getItem(tag);

    if (item == null) {
      item = new EnumerationItem(tag);
      items.put(tag, item);
    }

    Object result = (item.description != null) ? item.description : item.name;

    if ((className != null) && (item.name != null)) {
      try {
        Class<Enum> clazz = (Class<Enum>) Class.forName(className);
        Enum<?> candidate = Enum.valueOf(clazz, item.name);
        if (candidate != null) {
          result = candidate;
        }
      } catch (ClassNotFoundException e) {
        /** @todo  */
      } catch (ClassCastException e) {
        /** @todo  */
      }
    }

    return result;
  }


  public Object getValue(Object result) {
    if (result instanceof Integer) {
      result = getValue(((Integer) result).intValue());
    }

    return result;
  }


  public Collection<Integer> getTags() {
    return Collections.unmodifiableCollection(items.keySet());
  }


  public Collection<EnumerationItem> getItems() {
    return Collections.unmodifiableCollection(items.values());
  }


  public EnumerationItem getItem(int tag) {
    return items.get(tag);
  }


  private final String className;


  private final Map<Integer, EnumerationItem> items = new HashMap<Integer, EnumerationItem>();
}
