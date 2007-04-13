/* $Id$ */

package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Heap;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;

public abstract class Parser {

  protected Parser(Walker walker) {
    this.walker = walker;
  }


  private void setInput(ImageInputStream value) {
    if (value == null) {
      throw new NullPointerException("in");
    }

    this.in = value;
  }


  private void setMetaMetaData(MetaMetaData value) {
    if (value == null) {
      throw new NullPointerException("metaMetaData");
    }

    this.metaMetaData = value;
  }



  public final void start(ImageInputStream in, MetaMetaData metaMetaData)
    throws IOException
  {
    setInput(in);
    setMetaMetaData(metaMetaData);
    walker.readPrologue(in);
    heap = metaMetaData.getHeap(getInitialHeapName());
  }


  protected abstract String getInitialHeapName();


  protected ImageInputStream in;


  protected MetaMetaData metaMetaData;


  protected final Walker walker;


  private Heap heap;
}
