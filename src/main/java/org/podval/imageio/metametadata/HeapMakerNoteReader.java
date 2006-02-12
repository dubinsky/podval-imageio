/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public abstract class HeapMakerNoteReader implements MakerNoteReader {

  private HeapMakerNoteReader() {
  }


  public void read(String make,  MetaMetaData metaMetaData, Reader reader, long offset, int length, int tag)
    throws IOException
  {
    reader.getInputStream().seek(offset);

    /** @todo read signature */

    /** @todo more seeking */

    String heapName = make.toLowerCase() + "-maker-note";

    metaMetaData.getHeap(heapName).readInPlace(reader, offset, length, tag, false);
  }



  /**
   */
  public static class Canon extends HeapMakerNoteReader {
  }
}
