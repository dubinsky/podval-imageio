/* $Id$ */

package org.podval.imageio;


public class EntryInformation {

  public EntryInformation(Entry.Kind kind, long offset, int length, int tag, Type type) {
    this.kind = kind;
    this.offset = offset;
    this.length = length;
    this.tag = tag;
    this.type = type;
  }


  public final Entry.Kind kind;


  public final long offset;


  public final int length;


  public final int tag;


  public final Type type;
}
