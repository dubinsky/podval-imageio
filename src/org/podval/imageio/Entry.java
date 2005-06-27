/* $Id$ */

package org.podval.imageio;


public class Entry {

  protected Entry(String name, TypeNG type) {
    this.name = name;
    this.type = type;
  }


  public final String getName() {
    return name;
  }


  public final TypeNG getType() {
    return type;
  }


  private final String name;


  private final TypeNG type;
}
