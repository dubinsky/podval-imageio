/* $Id$ */

package org.podval.imageio;


public class RecordNG extends Entry {

  public RecordNG(String name, TypeNG type) {
    this(name, type, false, false, null);
  }


  public RecordNG(String name, TypeNG type, boolean skip, boolean isVector, String conversion) {
    super(name, type);
    this.isVector = isVector;
    this.skip = skip;
    this.conversion = conversion;
  }


  private final boolean isVector;


  private final boolean skip;


  private final String conversion;
}
