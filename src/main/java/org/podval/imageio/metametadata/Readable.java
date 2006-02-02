/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public interface Readable {

  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException;
}
