/* @(#)$Id$*/

package org.podval.imageio;


public enum Rotation {

  NOTHING(0), LEFT(-90), RIGHT(90), OVER(180);


  private static Rotation valueOf(int degrees) {
    while (degrees < 0) {
      degrees += 360;
    }

    degrees = degrees % 360;

    Rotation result = null;

    switch (degrees) {
    case   0: result = NOTHING; break;
    case 270: result = LEFT   ; break;
    case  90: result = RIGHT  ; break;
    case 180: result = OVER   ; break;
    default:
      throw new IllegalArgumentException();
    }

    return result;
  }


  private Rotation(int degrees) {
    this.degrees = degrees;
  }


  private int getDegrees() {
    return degrees;
  }


  private final int degrees;


  public Rotation left() {
    return valueOf(getDegrees() + LEFT.getDegrees());
  }


  public Rotation right() {
    return valueOf(getDegrees() + RIGHT.getDegrees());
  }


  public Rotation over() {
    return valueOf(getDegrees() + OVER.getDegrees());
  }


  public Rotation inverse() {
    return valueOf(-getDegrees());
  }
}
