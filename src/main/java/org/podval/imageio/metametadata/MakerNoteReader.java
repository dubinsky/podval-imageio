/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public interface MakerNoteReader {

  public void read(String make, MetaMetaData metaMetaData, Reader reader, long offset, int length, int tag)
    throws IOException;
}
