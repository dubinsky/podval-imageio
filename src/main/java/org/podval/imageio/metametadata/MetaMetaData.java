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
      result = new MetaMetaData(name);
      MetaMetaDataLoader.load(result);
      name2data.put(name, result);
    }

    return result;
  }


  private static Map<String, MetaMetaData> name2data = new HashMap<String, MetaMetaData>();


  public MetaMetaData(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }


  public Heap getRootHeap() {
    return getHeap(getName() + "-root");
  }


  public Heap getHeap(String name) {
    Heap result = name2heap.get(name);
    if (result == null) {
      result = new Heap(name);
      name2heap.put(name, result);
    }
    return result;
  }


  public Record getRecord(String name) {
    Record result = name2record.get(name);
    if (result == null) {
      result = new Record(name);
      name2record.put(name, result);
    }
    return result;
  }


  private final String name;


  private final Map<String, Heap> name2heap = new HashMap<String, Heap>();


  private final Map<String, Record> name2record = new HashMap<String, Record>();
}
