/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Entry;


public abstract class EntryBuilder extends Builder {

  public EntryBuilder(Builder previous) {
    super(previous);
  }


  protected abstract Entry getEntry();


  public final String toString() {
    return getEntry().toString();
  }
}
