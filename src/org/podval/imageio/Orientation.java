package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

/** @todo XXXXX: back to "TOP LEFT"...!!! */

public class Orientation {

  public static class Rotation {

    private Rotation(String name) {
      this.name = name;
    }


    public String toString() {
      return name;
    }


    public static final Rotation NOTHING = new Rotation("nothing");


    public static final Rotation LEFT = new Rotation("left");


    public static final Rotation OVER = new Rotation("over");


    public static final Rotation RIGHT = new Rotation("right");


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


    private final String name;


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
      if (rotation == Rotation.NOTHING) result = NORMAL; else
      if (rotation == Rotation.LEFT   ) result = LEFT  ; else
      if (rotation == Rotation.OVER   ) result = OVER  ; else
      if (rotation == Rotation.RIGHT  ) result = RIGHT ;

    } else {
      if (rotation == Rotation.NOTHING) result = FLIP           ; else
      if (rotation == Rotation.LEFT   ) result = FLIP_AND_LEFT  ; else
      if (rotation == Rotation.OVER   ) result = FLIP_AND_OVER  ; else
      if (rotation == Rotation.RIGHT  ) result = FLIP_AND_RIGHT ;
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


  public static final Orientation NORMAL =
    new Orientation(false, Rotation.NOTHING, "normal");


  public static final Orientation LEFT =
    new Orientation(false, Rotation.LEFT, "left");


  public static final Orientation RIGHT =
    new Orientation(false, Rotation.RIGHT, "right");


  public static final Orientation OVER =
    new Orientation(false, Rotation.OVER, "over");


  public static final Orientation FLIP =
    new Orientation(true, Rotation.NOTHING, "flip");


  public static final Orientation FLIP_AND_LEFT =
    new Orientation(true, Rotation.LEFT, "flip and left");


  public static final Orientation FLIP_AND_RIGHT =
    new Orientation(true, Rotation.RIGHT, "flip and right");


  public static final Orientation FLIP_AND_OVER =
    new Orientation(true, Rotation.OVER, "flip and over");


  private final boolean flipAroundHorizontal;


  private final Rotation rotation;


  private final String name;
}
