/* $Id$ */

package org.podval.imageio;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public interface ReaderHandler {

  public static enum ValueAction { SKIP, RAW, VALUE }


  /**
   *
   * @return boolean true if the folder should be processed; false if it should be skipped
   */
  public boolean startFolder(int tag, String name);


  public void endFolder();


  public ValueAction atValue(int tag, String name, int count);


  public void handleValue(int tag, String name, int count, Object value);


  public void handleRawValue(int tag, String name, int count, ImageInputStream is)
    throws IOException;
}
