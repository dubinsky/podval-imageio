package org.podval.imageio;

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



  private Orientation(boolean flipAroundHorizontal, Rotation rotation) {
    this.flipAroundHorizontal = flipAroundHorizontal;
    this.rotation = rotation;
  }


  public String toString() {
    return (((flipAroundHorizontal) ? "flip and " : "") + rotation);
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


  public static final Orientation NORMAL = new Orientation(false, Rotation.NOTHING);


  public static final Orientation LEFT = new Orientation(false, Rotation.LEFT);


  public static final Orientation RIGHT = new Orientation(false, Rotation.RIGHT);


  public static final Orientation OVER = new Orientation(false, Rotation.OVER);


  public static final Orientation FLIP = new Orientation(true, Rotation.NOTHING);


  public static final Orientation FLIP_AND_LEFT = new Orientation(true, Rotation.LEFT);


  public static final Orientation FLIP_AND_RIGHT = new Orientation(true, Rotation.RIGHT);


  public static final Orientation FLIP_AND_OVER = new Orientation(true, Rotation.OVER);



  private final boolean flipAroundHorizontal;


  private final Rotation rotation;


  public Orientation get(boolean flipAroundHorizontal, Rotation rotation) {
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
}
