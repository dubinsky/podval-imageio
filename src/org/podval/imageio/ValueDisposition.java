/* $Id$ */

package org.podval.imageio;

import java.io.OutputStream;


public class ValueDisposition {

  public static final ValueDisposition SKIP = new ValueDisposition();


  public static final ValueDisposition VALUE = new ValueDisposition();


  public static final ValueDisposition RAW = new ValueDisposition();


  public static ValueDisposition stream(OutputStream os) {
    return new Stream(os);
  }


  public static ValueDisposition bytes(int number) {
    return new Bytes(number);
  }



  /**
   *
   */
  public static class Stream extends ValueDisposition {

    private Stream(OutputStream os) {
      this.os = os;
    }


    public final OutputStream os;
  }



  /**
   *
   */
  public static class Bytes extends ValueDisposition {

    private Bytes(int number) {
      this.number = number;
    }


    public final int number;
  }



  private ValueDisposition() {
  }
}
