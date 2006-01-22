/* $Id$ */

package org.podval.imageio;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public interface ReaderHandler {

  public static enum ValueAction { SKIP, RAW, VALUE }


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


  public ValueAction atValue(int tag, String name, TypeNG type, int count)
    throws IOException;


  public void handleValue(int tag, String name, TypeNG type, int count, Object value);


  public void handleRawValue(int tag, String name, TypeNG type, int count, ImageInputStream is)
    throws IOException;
}
