/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


/** @todo where is the right place to load xml files? */

public final class MetaMetaData {

  public MetaMetaData() {
  }


  public Heap getInitialHeap() {
    if (initialHeap == null) {
      initialHeap = new Heap("initialHeap", null);
    }

    return initialHeap;
  }


  public void registerHeap(Heap heap) {
    name2heap.put(heap.getName(), heap);
    if (initialHeap == null) {
      initialHeap = heap;
    }
  }


  public Heap getHeap(String name, TypeNG type) {
    Heap result = name2heap.get(name);
    if (result == null) {
      result = new Heap(name, type);
      registerHeap(result);
    }
    return result;
  }


  public Heap getHeap(Heap parent, int tag, TypeNG type) throws IOException {
    Entry result = parent.getEntry(tag, type);

    if (result == null) {
      result = new Heap(unknown(tag), type);
      learn(parent, tag, result);
    }

    if ((result != null) &&!(result instanceof Heap)) {
      throw new IOException("Not a heap: " + tag + "-" + type);
    }

    return (Heap) result;
  }


  public Entry getEntry(Heap parent, int tag, TypeNG type, long length, int count) {
    Entry result = parent.getEntry(tag, type);

    if (result == null) {
      boolean isRecordAllowed = true; /** @todo this depends on type... */
      result = (isRecordAllowed) ?
        new RecordNG(unknown(tag), type) :
        new Heap(unknown(tag), type);

      learn(parent, tag, result);
    }

    return result;
  }


  public RecordNG getRecord(Heap parent, int tag, TypeNG type, long length, int count)
    throws IOException
  {
    Entry result = parent.getEntry(tag, type);

    if (result == null) {
      result = new RecordNG(unknown(tag), type);
      learn(parent, tag, result);
    }

    if ((result != null) && !(result instanceof RecordNG)) {
      throw new IOException("Not a record: " + tag + "-" + type);
    }

    return (result instanceof RecordNG) ? (RecordNG) result : null;
  }


  private String unknown(int tag) {
    return "unknown-" + tag;
  }


  private void learn(Heap parent, int tag, Entry entry) {
    /** @todo should "learning" be disableable? */
    /** @todo mark as auto-learned */
    parent.addEntry(tag, entry);
  }


  private Heap initialHeap;


  private final Map<String, Heap> name2heap = new HashMap<String,Heap>();
}
