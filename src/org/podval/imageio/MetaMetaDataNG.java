/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public final class MetaMetaDataNG {

  public MetaMetaDataNG() {
  }


  public void setInitialHeap(Heap value) {
    initialHeap = value;
  }


  public Heap getInitialHeap() {
    return initialHeap;
  }


  public void registerHeap(Heap heap) {
    name2heap.put(heap.getName(), heap);
  }


  public Heap getHeapByName(String name) {
    return name2heap.get(name);
  }


  private Heap initialHeap;


  private final Map<String, Heap> name2heap = new HashMap<String,Heap>();
}
