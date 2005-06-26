/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public class Heap extends Entry {

  public Heap() {
    super(null);
  }


  public Heap(String name) {
    super(name);
  }


  public void addEntry(int tag, TypeNG type, Entry entry) {
    /** @todo type parameter needs to be removed; all entries should have type in them */
    Key key = new Key(tag, type);

    Entry oldEntry = entries.get(key);
    if (oldEntry != null) {
      throw new IllegalArgumentException("Attempt to replace " + entry.getName() + " with " + getName() +
        "; key " /*+ key*/);
    }
    entries.put(key, entry);
  }


  public Entry getEntry(int tag, TypeNG type, long length, int count) {
    Key key = new Key(tag, type);
    Entry result = entries.get(key);

    /** @todo check point!!! */

    /** @todo learning point */

    return result;
  }


  public Heap getHeap(int tag, TypeNG type) throws IOException {
    Key key = new Key(tag, type);

    Entry result = entries.get(key);

    /** @todo learning point */

    if ((result != null) &&!(result instanceof Heap)) {
      throw new IOException("Not a heap: " + key);
    }

    return (Heap) result;
  }


  public RecordNG getRecord(int tag, TypeNG type, long length, int count)
    throws IOException
  {
    Key key = new Key(tag, type);
    Entry result = entries.get(key);

    /** @todo check point!!! */

    /** @todo learning point */

    if ((result != null) && !(result instanceof RecordNG)) {
      throw new IOException("Not a record: " + key);
    }

    return (result instanceof RecordNG) ? (RecordNG) result : null;
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
