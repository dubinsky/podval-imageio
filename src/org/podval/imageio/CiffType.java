/* $Id$ */

package org.podval.imageio;


public enum CiffType {

  U8,
  U16,
  U32,
  STRING,
  STRUCTURE;


  public static CiffType valueOf(int dataType) {
    CiffType result;

    switch (dataType) {
    case 0: result = U8       ; break;
    case 1: result = STRING   ; break;
    case 2: result = U16      ; break;
    case 3: result = U32      ; break;
    case 4: result = STRUCTURE; break;
    default:
      throw new IllegalArgumentException("dataType");
    }

    return result;
  }
}
