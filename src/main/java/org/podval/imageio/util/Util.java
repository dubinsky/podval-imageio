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


  public static String readString(ImageInputStream is, int length) throws IOException {
    byte[] bytes = readBytes(is, length);
    /** @todo length of 0 indicates 'indefinite'. Limit 'em here? */

    int l = 0;
    for (; l<bytes.length; l++) {
      if (bytes[l] == 0) {
        break;
      }
    }

//      if (l == length) {
////        result.append("|NO ZERO. TRUNCATED?");
//      }

    return new String(bytes, 0, l).trim();
  }


  public static byte[] readBytes(ImageInputStream is, int length) throws IOException {
    byte[] result = new byte[length];
    is.readFully(result);
    return result;
  }
}
