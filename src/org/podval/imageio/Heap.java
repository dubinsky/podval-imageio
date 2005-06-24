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


  public void addEntry(int tag, Entry entry) {
    Entry oldEntry = entries.get(tag);
    if (oldEntry != null) {
      throw new IllegalArgumentException("Can not add " + entry.getName() + " to " + getName() +
        ": entry " + oldEntry.getName() + " with the same tag " + tag + " exists!");
    }
    entries.put(tag, entry);
  }


  public Entry getEntry(int tag, TypeNG type, long length, int count) {
    Entry result = entries.get(tag);

    /** @todo check point!!! */

    /** @todo learning point */

    return result;
  }


  public Heap getHeap(int tag) throws IOException {
    Entry result = entries.get(tag);

    /** @todo learning point */

    if ((result != null) &&!(result instanceof Heap)) {
      throw new IOException("Tag does not correspond to a heap");
    }

    return (Heap) result;
  }


  public RecordNG getRecord(int tag, TypeNG type, long length, int count)
    throws IOException
  {
    Entry result = entries.get(tag);

    /** @todo check point!!! */

    /** @todo learning point */

    if ((result != null) && !(result instanceof RecordNG)) {
      throw new IOException("Tag does not correspond to a record");
    }

    return (result instanceof RecordNG) ? (RecordNG) result : null;
  }


  private final Map<Integer, Entry> entries = new HashMap<Integer,Entry>();
}
