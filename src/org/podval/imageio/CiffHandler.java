/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;


public interface CiffHandler {

  /**
   *
   * @return boolean true if the heap should be processed; false if it should be skipped
   */
  public boolean startHeap(int idCode);


  public void endHeap();


  public void readRecord(ImageInputStream in, long offset, long length, CiffType type, int idCode);
}
