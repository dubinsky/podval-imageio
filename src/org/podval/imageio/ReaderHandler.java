/* $Id$ */

package org.podval.imageio;

import java.io.IOException;


public interface ReaderHandler {

  /**
   *
   * @return boolean true if the heap should be processed; false if it should be skipped
   */
  public boolean startHeap(int tag, String name);


  /** @todo rename (to directory?) */
  public void endHeap();


  /**
   *
   * @return boolean true if the record should be processed; false if it should be skipped
   */
  public boolean startRecord(int tag, String name);


  public void endRecord();


  /**
   * @return Object null, TRUE, Integer or OutputStream
   */
  public Object atValue(int tag, String name, TypeNG type, int count)
    throws IOException;


  public void handleValue(int tag, String name, TypeNG type, int count, Object value);
}
