/* $Id$ */

package org.podval.imageio;


public final class Dimension {

  public Dimension(int width, int height) {
    this.width = width;
    this.height = height;
  }


  public int getWidth() {
    return width;
  }


  public int getHeight() {
    return height;
  }


  private final int width;


  private final int height;
}
