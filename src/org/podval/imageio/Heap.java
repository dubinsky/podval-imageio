/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public class Heap extends Entry {

  public Heap() {
    super(null);
  }


  public Heap(String name) {
    super(name);
  }


  public Heap getHeap(int tag) {
    Entry entry = entries.get(tag);

    /** @todo learning point */

    return (entry instanceof Heap) ? (Heap) entry : null;
  }


  private final Map<Integer, Entry> entries = new HashMap<Integer,Entry>();
}
