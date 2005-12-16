/* $Id$ */

package org.podval.imageio;


public class MakerNote extends Heap {

  public MakerNote(String name, String maker, String signature) {
    super(name, null);
    this.maker = maker;
    this.signature = signature;
  }


  private final String maker;


  private final String signature;
}
