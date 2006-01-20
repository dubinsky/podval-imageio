/* $Id$ */

package org.podval.imageio;


public class MakerNoteMarker extends Entry {

  public MakerNoteMarker(TypeNG type) {
    /** @todo we do not really need a name here... */
    /** @todo we do not really need a type here either!!! */
    super("makerNoteMarker", type);
  }


  public void read(Reader reader, long offset, int length, int tag, TypeNG type) {
    /** @todo and - we do not need this, for sure! */
  }
}
