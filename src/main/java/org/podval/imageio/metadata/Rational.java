/* @(#)$Id$ */

package org.podval.imageio.metadata;


public final class Rational {

  public Rational(int numerator, int denominator) {
    this.numerator = numerator;
    this.denominator = denominator;
  }


  public String toString() {
    return numerator + "/" + denominator;
  }


  public float toFloatValue() {
    return (float) numerator / (float) denominator;
  }


  public final int numerator;


  public final int denominator;
}
