package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public class Orientation {

  public static class Rotation {

    public static final Rotation NOTHING = new Rotation();


    public static final Rotation LEFT = new Rotation();


    public static final Rotation OVER = new Rotation();


    public static final Rotation RIGHT = new Rotation();


    public Rotation left() { return left; }


    public Rotation over() { return over; }


    public Rotation right() { return right; }


    public Rotation inverse() { return inverse; }


    private void setTransform(Rotation left, Rotation over, Rotation right, Rotation inverse) {
      this.left = left;
      this.over = over;
      this.right = right;
      this.inverse = inverse;
    }


    private Rotation left;


    private Rotation over;


    private Rotation right;


    private Rotation inverse;


    static {
      NOTHING.setTransform(LEFT   , OVER   , RIGHT  , NOTHING);
      LEFT   .setTransform(OVER   , RIGHT  , NOTHING, RIGHT  );
      OVER   .setTransform(RIGHT  , NOTHING, LEFT   , OVER   );
      RIGHT  .setTransform(NOTHING, LEFT   , OVER   , LEFT   );
    }
  }



  public static Orientation get(boolean flipAroundHorizontal, Rotation rotation) {
    Orientation result = null;

    if (!flipAroundHorizontal) {
      if (rotation == Rotation.NOTHING) result = TOP_LEFT; else
      if (rotation == Rotation.LEFT   ) result = LEFT_BOTTOM  ; else
      if (rotation == Rotation.OVER   ) result = BOTTOM_RIGHT  ; else
      if (rotation == Rotation.RIGHT  ) result = RIGHT_TOP ;

    } else {
      if (rotation == Rotation.NOTHING) result = BOTTOM_LEFT           ; else
      if (rotation == Rotation.LEFT   ) result = RIGHT_BOTTOM  ; else
      if (rotation == Rotation.OVER   ) result = TOP_RIGHT  ; else
      if (rotation == Rotation.RIGHT  ) result = LEFT_TOP ;
    }

    return result;
  }


  public static Orientation get(String name) {
    return (Orientation) values.get(name);
  }


  private Orientation(boolean flipAroundHorizontal, Rotation rotation, String name) {
    this.flipAroundHorizontal = flipAroundHorizontal;
    this.rotation = rotation;
    this.name = name;

    values.put(name, this);
  }


  public boolean isFlipAroundHorizontal() {
    return flipAroundHorizontal;
  }


  public Rotation getRotation() {
    return rotation;
  }


  public String toString() {
    return name;
  }


  public Orientation rotateLeft() {
    return get(flipAroundHorizontal, rotation.left());
  }


  public Orientation rotateOver() {
    return get(flipAroundHorizontal, rotation.over());
  }


  public Orientation rotateRight() {
    return get(flipAroundHorizontal, rotation.right());
  }


  public Orientation flipAroundHorizontal() {
    return get(!flipAroundHorizontal, rotation.inverse());
  }


  public Orientation flipAroundVertical() {
    return get(!flipAroundHorizontal, rotation.left().inverse().right());
  }


  public Orientation inverse() {
    return get(flipAroundHorizontal, (flipAroundHorizontal) ? rotation : rotation.inverse());
  }


  /**
   * Map<String, Orientation>
   * This must be initialized before the Orientation constants, since they
   * register themselves in the 'values' Map!
   */
  private static Map values = new HashMap();


  public static final Orientation TOP_LEFT =
    new Orientation(false, Rotation.NOTHING, "top-left");


  public static final Orientation TOP_RIGHT =
    new Orientation(true, Rotation.OVER, "top-right");


  public static final Orientation BOTTOM_RIGHT =
    new Orientation(false, Rotation.OVER, "bottom-right");


  public static final Orientation BOTTOM_LEFT =
    new Orientation(true, Rotation.NOTHING, "bottom-left");


  public static final Orientation LEFT_TOP =
    new Orientation(true, Rotation.RIGHT, "left-top");


  public static final Orientation RIGHT_TOP =
    new Orientation(false, Rotation.RIGHT, "right-top");


  public static final Orientation RIGHT_BOTTOM =
    new Orientation(true, Rotation.LEFT, "right-bottom");


  public static final Orientation LEFT_BOTTOM =
    new Orientation(false, Rotation.LEFT, "left-bottom");



  private final boolean flipAroundHorizontal;


  private final Rotation rotation;


  private final String name;
}
