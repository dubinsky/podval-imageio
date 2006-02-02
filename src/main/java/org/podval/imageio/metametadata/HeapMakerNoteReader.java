/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;

import java.io.IOException;


public abstract class HeapMakerNoteReader implements Readable {

  private HeapMakerNoteReader() {
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    reader.getInputStream().seek(offset);

    /** @todo read signature */

    /** @todo more seeking */

    reader.getMetaMetaData().getHeap(getHeapName()).readInPlace(reader, offset, length, tag, false);
  }


  protected abstract String getHeapName();



  /**
   */
  public static class Canon extends HeapMakerNoteReader {
    protected String getHeapName() {
      return "canon-maker-note";
    }
  }
}
