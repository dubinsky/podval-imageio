/* $Id$ */

package org.podval.imageio;


public class FieldNG {

  public FieldNG(String name, TypeNG type) {
    this.name = name;
    this.type = type;
  }


  public final String getName() {
    return name;
  }


  private final String name;


  private final TypeNG type;
}
