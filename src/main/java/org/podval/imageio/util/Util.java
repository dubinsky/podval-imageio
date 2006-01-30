/* $Id$ */

package org.podval.imageio.util;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public final class Util {

  private Util() {
  }


  public static int readUnsignedInt(ImageInputStream in) throws IOException {
    return toInt(in.readUnsignedInt());
  }


  public static int toInt(long value) throws IOException {
    /** @todo check - or catch and transform? */
    return (int) value;
  }
}
