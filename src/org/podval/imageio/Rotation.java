package org.podval.imageio;


public enum Rotation {

  NOTHING, LEFT, OVER, RIGHT;


  public Rotation left() { return left; }


  public Rotation over() {
    return over;
  }


  public Rotation right() {
    return right;
  }


  public Rotation inverse() {
    return inverse;
  }


  private void setTransform(Rotation left, Rotation right, Rotation over, Rotation inverse) {
    this.left = left;
    this.right = right;
    this.over = over;
    this.inverse = inverse;
  }


  private Rotation left;


  private Rotation right;


  private Rotation over;


  private Rotation inverse;


  static {
    NOTHING.setTransform(LEFT   , RIGHT  , OVER   , NOTHING);
    LEFT   .setTransform(OVER   , NOTHING, RIGHT  , RIGHT  );
    OVER   .setTransform(RIGHT  , LEFT   , NOTHING, OVER   );
    RIGHT  .setTransform(NOTHING, OVER   , LEFT   , LEFT   );
  }
}
