/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public class Heap extends Entry {

//  public Heap() {
//    super(null);
//  }


  public Heap(String name, TypeNG type) {
    super(name, type);
  }


  public void addEntry(int tag, Entry entry) {
    /** @todo add under all of the entry's types
         for (Iterator i = entry.getType().getActualTypes().iterator(); i.hasNext();)
           addEntry(tag, (Type) i.next(), entry);
     */
    addEntry(tag, entry.getType(), entry);
  }


  private void addEntry(int tag, TypeNG type, Entry entry) {
    Key key = new Key(tag, type);

    Entry oldEntry = entries.get(key);
    if (oldEntry != null) {
      throw new IllegalArgumentException("Attempt to replace " + entry.getName() + " with " + getName() +
        "; key " + key);
    }
    entries.put(key, entry);
  }


  public Entry getEntry(int tag, TypeNG type) {
    return entries.get(new Key(tag, type));
  }


  /**
   */
  private static class Key {

    public Key(int tag, TypeNG type) {
      this.tag = tag;
      this.type = type;
    }


    public boolean equals(Object o) {
      Key other = (Key) o;
      return (this.tag == other.tag) && (this.type == other.type);
    }


    public int hashCode() {
      int result = tag*317;
      if (type != null) {
        result += type.hashCode();
      }
      return result;
    }


    public String toString() {
      return tag + "-" + type;
    }


    private final int tag;


    private final TypeNG type;
  }



  private final Map<Key, Entry> entries = new HashMap<Key,Entry>();
}