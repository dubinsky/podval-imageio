/* @(#)$Id$*/

package org.podval.imageio;


public enum Orientation {

  TOP_LEFT    (false, Rotation.NOTHING),
  TOP_RIGHT   (true , Rotation.OVER   ),
  BOTTOM_RIGHT(false, Rotation.OVER   ),
  BOTTOM_LEFT (true , Rotation.NOTHING),
  LEFT_TOP    (true , Rotation.RIGHT  ),
  RIGHT_TOP   (false, Rotation.RIGHT  ),
  RIGHT_BOTTOM(true , Rotation.LEFT   ),
  LEFT_BOTTOM (false, Rotation.LEFT   );


  public static final Orientation NORMAL = TOP_LEFT;


  public static Orientation valueOf(boolean flipAroundHorizontal, Rotation rotation) {
    Orientation result = null;

    if (!flipAroundHorizontal) {
      switch (rotation) {
      case NOTHING: result = TOP_LEFT    ; break;
      case LEFT   : result = LEFT_BOTTOM ; break;
      case OVER   : result = BOTTOM_RIGHT; break;
      case RIGHT  : result = RIGHT_TOP   ; break;
      }
    } else {
      switch (rotation) {
      case NOTHING: result = BOTTOM_LEFT ; break;
      case LEFT   : result = RIGHT_BOTTOM; break;
      case OVER   : result = TOP_RIGHT   ; break;
      case RIGHT  : result = LEFT_TOP    ; break;
      }
    }

    return result;
  }


  private Orientation(boolean flipAroundHorizontal, Rotation rotation) {
    this.flipAroundHorizontal = flipAroundHorizontal;
    this.rotation = rotation;
  }


  public boolean isFlipAroundHorizontal() {
    return flipAroundHorizontal;
  }


  public Rotation getRotation() {
    return rotation;
  }


  public Orientation rotateLeft() {
    return valueOf(flipAroundHorizontal, rotation.left());
  }


  public Orientation rotateRight() {
    return valueOf(flipAroundHorizontal, rotation.right());
  }


  public Orientation rotateOver() {
    return valueOf(flipAroundHorizontal, rotation.over());
  }


  public Orientation flipAroundHorizontal() {
    return valueOf(!flipAroundHorizontal, rotation.inverse());
  }


  public Orientation flipAroundVertical() {
    return valueOf(!flipAroundHorizontal, rotation.left().inverse().right());
  }


  public Orientation inverse() {
    return valueOf(flipAroundHorizontal, (flipAroundHorizontal) ? rotation : rotation.inverse());
  }


  public Dimension apply(Dimension value) {
    return getRotation().apply(value);
  }


  private final boolean flipAroundHorizontal;


  private final Rotation rotation;
}
