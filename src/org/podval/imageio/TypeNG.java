/* $Id$ */

package org.podval.imageio;


public enum TypeNG {

  U8(1),
  STRUCTURE(1), // X8
  STRING(1),
  U16(2),
  S16(2),
  U32(4),
  S32(4),
  F32(4),
  RATIONAL(8),
  SRATIONAL(8),
  U16_OR_U32(0);


  private TypeNG(int length) {
    this.length = length;
  }


  public int getLength() {
    return length;
  }


  private final int length;
}
