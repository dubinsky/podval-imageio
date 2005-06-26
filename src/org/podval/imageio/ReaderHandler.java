/* $Id$ */

package org.podval.imageio;


public interface ReaderHandler {

  /**
   *
   * @return boolean true if the heap should be processed; false if it should be skipped
   */
  public boolean startHeap(int tag, Heap heap);


  /** @todo rename (to directory?) */
  public void endHeap();


  /** @todo rename (to entry?) */
  public void readRecord(int tag, TypeNG type, int length, int count, Reader reader, RecordNG record);
}
