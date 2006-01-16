/* $Id$ */

package org.podval.imageio;


public class Entry {

  protected Entry(String name, TypeNG type) {
//    if (type == null) {
//      throw new NullPointerException("null type for " + name);
//    }

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
