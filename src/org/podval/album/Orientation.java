package org.podval.album;

public class Orientation {

  public static class Rotation {

    private Rotation(String name) {
      this.name = name;
    }


    public static final Rotation NO = new Rotation("no");


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
      NO   .setTransform(LEFT , OVER , RIGHT, NO   );
      LEFT .setTransform(OVER , RIGHT, NO   , RIGHT);
      OVER .setTransform(RIGHT, NO   , LEFT , OVER );
      RIGHT.setTransform(NO   , LEFT , OVER , LEFT );
    }
  }



  private Orientation(boolean flipAroundHorizontal, Rotation rotation) {
    this.flipAroundHorizontal = flipAroundHorizontal;
    this.rotation = rotation;
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
    /** @todo XXXX */
    return null;
  }


  public static final Orientation NORMAL = new Orientation(false, Rotation.NO);


  public static final Orientation LEFT = new Orientation(false, Rotation.LEFT);


  public static final Orientation RIGHT = new Orientation(false, Rotation.RIGHT);


  public static final Orientation OVER = new Orientation(false, Rotation.OVER);


  public static final Orientation FLIPPED = new Orientation(true, Rotation.NO);


  public static final Orientation FLIPPED_AND_LEFT = new Orientation(true, Rotation.LEFT);


  public static final Orientation FLIPPED_AND_RIGHT = new Orientation(true, Rotation.RIGHT);


  public static final Orientation FLIPPED_AND_OVER = new Orientation(true, Rotation.OVER);



  private final boolean flipAroundHorizontal;


  private final Rotation rotation;


  public Orientation get(boolean flipAroundHorizontal, Rotation rotation) {
    Orientation result = null;

    if (!flipAroundHorizontal) {
      if (rotation == Rotation.NO   ) result = NORMAL; else
      if (rotation == Rotation.LEFT ) result = LEFT  ; else
      if (rotation == Rotation.OVER ) result = OVER  ; else
      if (rotation == Rotation.RIGHT) result = RIGHT ;

    } else {
      if (rotation == Rotation.NO   ) result = FLIPPED           ; else
      if (rotation == Rotation.LEFT ) result = FLIPPED_AND_LEFT  ; else
      if (rotation == Rotation.OVER ) result = FLIPPED_AND_OVER  ; else
      if (rotation == Rotation.RIGHT) result = FLIPPED_AND_RIGHT ;
    }

    return result;
  }
}
