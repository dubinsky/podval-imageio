/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.metametadata.loader.MetaMetaDataLoader;

import java.util.Map;
import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import java.io.IOException;


public final class MetaMetaData implements Parent {

  public static MetaMetaData get(String name) throws IOException, XMLStreamException, MetaMetaDataException {
    MetaMetaData result = name2data.get(name);

    if (result == null) {
      result = new MetaMetaData();
      new MetaMetaDataLoader(result).load(name + ".list");
      add(name, result);
    }

    return result;
  }


  public static void add(String name, MetaMetaData value) {
    name2data.put(name, value);
  }


  private static Map<String, MetaMetaData> name2data = new HashMap<String, MetaMetaData>();


  public Parent getParent() {
    return null;
  }


  public Heap getHeap(String name) {
    Heap result = name2heap.get(name);
    if (result == null) {
      result = new Heap(name);
      result.setParent(this);
      name2heap.put(name, result);
    }
    return result;
  }


  public Record getRecord(String name) {
    Record result = name2record.get(name);
    if (result == null) {
      result = new Record(name);
      result.setParent(this);
      name2record.put(name, result);
    }
    return result;
  }


  public void addMakerNoteReaderClass(String make, String readerClassName) {
    make2note.put(make, readerClassName);
  }


  public String getMakerNoteReaderClassName(String make) {
    return make2note.get(make);
  }


  private final Map<String, Heap> name2heap = new HashMap<String, Heap>();


  private final Map<String, Record> name2record = new HashMap<String, Record>();


  private final Map<String, String> make2note = new HashMap<String, String>();
}
