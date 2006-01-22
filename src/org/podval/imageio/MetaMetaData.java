/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


/** @todo where is the right place to load xml files? */

public final class MetaMetaData {

  public MetaMetaData() {
  }


  public void setDefaultHeapType(TypeNG value) {
    defaultHeapType = value;
  }


  public TypeNG getDefaultHeapType() {
    return defaultHeapType;
  }


  public void setDefaultRecordType(TypeNG value) {
    defaultRecordType = value;
  }


  public TypeNG getDefaultRecordType() {
    return defaultRecordType;
  }


  public Heap getHeap(String name, TypeNG type) {
    Heap result = name2heap.get(name);
    if (result == null) {
      result = new Heap(name, type);
      name2heap.put(name, result);
    }
    return result;
  }


  public void registerRecord(RecordNG record) {
    name2record.put(record.getName(), record);
  }


  public Entry getEntry(Entry.Kind kind, Heap heap, int tag, TypeNG type)
    throws IOException
  {
    Entry result = null;

    switch (kind) {
    case HEAP   : result = getHeap  (heap, tag, type); break;
    case RECORD : result = getRecord(heap, tag, type); break;
    case UNKNOWN: result = getEntry (heap, tag, type); break;
      /** @todo maker note... */
//    if (entry == MakerNote.MARKER) {
//      MakerNote makerNote = handler.getMakerNote();
//      readIfdInPlace(makerNote.getDirectory(), in, offsetBase, handler);
//    } else
//      assert false : "Unknown IFD entry " + entry;
    }

    return result;
  }


  public RecordNG getRecord(String name) {
    return name2record.get(name);
  }


  public MakerNote getMakerNote(String name, String make, String signature) {
    MakerNote result = new MakerNote(name, make, signature);
    name2heap.put(name, result);
    make2note.put(make, result);
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


  public Entry getEntry(Heap parent, int tag, TypeNG type) {
    Entry result = parent.getEntry(tag, type);

    if (result == null) {
      boolean isRecordAllowed = true; /** @todo this depends on type... */
      result = (isRecordAllowed) ?
        unknownRecord(tag, type) :
        new Heap(unknown(tag), type);

      learn(parent, tag, result);
    }

    return result;
  }


  public RecordNG getRecord(Heap parent, int tag, TypeNG type)
    throws IOException
  {
    Entry result = parent.getEntry(tag, type);

    if (result == null) {
      result = unknownRecord(tag, type);
      learn(parent, tag, result);
    }

    if ((result != null) && !(result instanceof RecordNG)) {
      throw new IOException("Not a record: " + tag + "-" + type);
    }

    return (result instanceof RecordNG) ? (RecordNG) result : null;
  }


  public RecordNG getField(RecordNG record, int index) {
    RecordNG result = record.getField(index);

    if (result == null) {
      result = unknownRecord(index, record.getType());
      learn(record, index, result);
    }

    return result;
  }


  private RecordNG unknownRecord(int tag, TypeNG type) {
    return new RecordNG(unknown(tag), type);
  }


  private String unknown(int tag) {
    return "unknown-" + tag;
  }


  private void learn(Heap parent, int tag, Entry entry) {
    /** @todo should "learning" be disableable? */
    /** @todo mark as auto-learned */
    parent.addEntry(tag, entry);
  }


  private void learn(RecordNG record, int index, RecordNG field) {
    record.addField(index, field);
  }


  private TypeNG defaultHeapType;


  private TypeNG defaultRecordType;


  private final Map<String, Heap> name2heap = new HashMap<String, Heap>();


  private final Map<String, RecordNG> name2record = new HashMap<String, RecordNG>();


  private final Map<String, Heap> make2note = new HashMap<String, Heap>();
}
