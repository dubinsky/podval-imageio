/* @(#)$Id$*/

package org.podval.imageio.metadata;


public enum Rotation {

  NOTHING(0), LEFT(-90), RIGHT(90), OVER(180);


  private static Rotation valueOf(int degrees) {
    Rotation result = null;

    switch (normalize(degrees)) {
    case   0: result = NOTHING; break;
    case 270: result = LEFT   ; break;
    case  90: result = RIGHT  ; break;
    case 180: result = OVER   ; break;
    default:
      throw new IllegalArgumentException();
    }

    return result;
  }


  private static int normalize(int result) {
    while (result < 0) {
      result += 360;
    }

    return (result % 360);
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


  public Dimension apply(Dimension value) {
    Dimension result;

    switch (normalize(degrees)) {
    case   0:
    case 180:
      result = value;
      break;

    case 270:
    case  90:
      result = new Dimension(value.getHeight(), value.getWidth());
      break;

    default:
      throw new IllegalArgumentException();
    }

    return result;
  }
}
