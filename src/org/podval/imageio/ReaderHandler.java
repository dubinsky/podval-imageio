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


  public void handleValue(int tag, Object value, TypeNG type, RecordNG record);


  public void handleLongValue(int tag, int count, TypeNG type, RecordNG record, Reader reader);
}
