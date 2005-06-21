/* $Id$ */

package org.podval.imageio;


public interface ReaderHandler {

  /**
   *
   * @return boolean true if the heap should be processed; false if it should be skipped
   */
  public boolean startHeap(int idCode);


  /** @todo rename (to directory?) */
  public void endHeap();


  /** @todo rename (to entry?) */
  public void readRecord(Reader reader);
}
