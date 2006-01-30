/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.metametadata.loader.MetaMetaDataLoader;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;


public final class MetaMetaData {

  public static MetaMetaData get(String name)
    throws ParserConfigurationException, SAXException, IOException
  {
    MetaMetaData result = name2data.get(name);
    if (result == null) {
      result = MetaMetaDataLoader.load(name + ".list");
      name2data.put(name, result);
    }
    return result;
  }


  private static Map<String, MetaMetaData> name2data = new HashMap<String,MetaMetaData>();


  public MetaMetaData() {
  }


  public Heap getHeap(String name, Type type) throws MetaMetaDataException {
    Heap result = getHeap(name);
    result.setType(type);
    return result;
  }


  public Heap getHeap(String name) {
    Heap result = name2heap.get(name);
    if (result == null) {
      result = new Heap(name);
      name2heap.put(name, result);
    }
    return result;
  }


  public Record getRecord(String name, Type type) throws MetaMetaDataException {
    Record result = name2record.get(name);
    if (result == null) {
      result = new Record(name);
      name2record.put(name, result);
    }
    result.setType(type);
    return result;
  }


  public MakerNote getMakerNote(String name, String make, String signature) {
    MakerNote result = new MakerNote(name, make, signature);
    name2heap.put(name, result);
    make2note.put(make, result);
    return result;
  }


  public MakerNote getMakerNote(String make) {
    return make2note.get(make);
  }


  public Entry getEntry(Entry.Kind kind, Heap heap, int tag, Type type)
    throws IOException
  {
    Entry result = heap.getEntry(tag, type);

    if (result == null) {
      try {
        switch (kind) {
        case HEAP   : result = unknownHeap  (tag, type); break;
        case RECORD : result = unknownRecord(tag, type); break;
        case UNKNOWN:
          boolean isRecordAllowed = true; /** @todo this depends on type... */
          result = (isRecordAllowed) ?
            unknownRecord(tag, type) :
            unknownHeap(tag, type);
          break;
        }
      } catch (MetaMetaDataException e) {
        throw new IOException(e.getMessage());
      }

      learn(heap, tag, result);
    }

    if ((kind == Entry.Kind.HEAP) && (result != null) &&!(result instanceof Heap)) {
      throw new IOException("Not a heap: " + tag + "-" + type);
    }

    if ((kind == Entry.Kind.RECORD) && (result != null) && !(result instanceof Record)) {
      throw new IOException("Not a record: " + tag + "-" + type);
    }

    return result;
  }


  public Field getField(Record record, int index) throws MetaMetaDataException {
    Field result = record.getField(index);

    if (result == null) {
      result = unknownField(index, record.getType());
      learn(record, index, result);
    }

    return result;
  }


  private Heap unknownHeap(int tag, Type type) throws MetaMetaDataException {
    return new Heap(unknown(tag), type);
  }


  private Record unknownRecord(int tag, Type type) throws MetaMetaDataException {
    return new Record(unknown(tag), type);
  }


  private Field unknownField(int tag, Type type) throws MetaMetaDataException {
    return new Field(unknown(tag), type);
  }


  private String unknown(int tag) {
    return "unknown-" + tag;
  }


  private void learn(Heap heap, int tag, Entry entry) {
    /** @todo should "learning" be disableable? */
    /** @todo mark as auto-learned */
    heap.addEntry(tag, entry);
  }


  private void learn(Record record, int index, Field field) throws MetaMetaDataException {
    record.addField(index, field);
  }


  private final Map<String, Heap> name2heap = new HashMap<String, Heap>();


  private final Map<String, Record> name2record = new HashMap<String, Record>();


  private final Map<String, MakerNote> make2note = new HashMap<String, MakerNote>();
}
