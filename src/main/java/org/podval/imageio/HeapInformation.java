/* $Id$ */

package org.podval.imageio;


public class HeapInformation {

  public HeapInformation(long entriesOffset, int numEntries) {
    this.entriesOffset = entriesOffset;
    this.numEntries = numEntries;
  }


  public final long entriesOffset;


  public final int numEntries;
}
