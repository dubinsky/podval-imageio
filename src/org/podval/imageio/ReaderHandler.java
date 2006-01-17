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


  /**
   *
   * @return boolean true if the record should be processed; false if it should be skipped
   */
  public boolean startRecord(int tag, RecordNG record);


  public void endRecord();


  public void handleShortValue(int tag, TypeNG type, int count, RecordNG record, Object value);


  public void handleLongValue (int tag, TypeNG type, int count, RecordNG record, Reader reader);
}
