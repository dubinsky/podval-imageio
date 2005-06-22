/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public final class MetaMetaDataNG {

  public MetaMetaDataNG() {
  }


  public void registerInitialHeap(int tag, Heap heap) {
    if (getInitialHeap(tag) != null) {
      throw new IllegalArgumentException();
    }
    initialHeaps.put(tag, heap);
  }


  public Heap getInitialHeap(int tag) {
    return initialHeaps.get(tag);
  }


  private final Map<Integer, Heap> initialHeaps = new HashMap<Integer,Heap>();
}
