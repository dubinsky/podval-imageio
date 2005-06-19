/* $Id$ */

package org.podval.imageio;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;


public class Reader {

  /**
   * Determines byte order used from a special marker, and arranges so that it
   * is used for all subsequant reads.
   * @throws IOException
   */
  protected static void determineByteOrder(ImageInputStream in) throws IOException {
    int byte1 = in.read();
    int byte2 = in.read();

    ByteOrder byteOrder;

    if ((byte1 == 'I') && (byte2 == 'I')) {
      byteOrder = ByteOrder.LITTLE_ENDIAN;
    } else
    if ((byte1 == 'M') && (byte2 == 'M')) {
      byteOrder = ByteOrder.BIG_ENDIAN;
    } else {
      throw new IOException("Bad byte order marker.");
    }

    in.setByteOrder(byteOrder);
  }


  protected static boolean readSignature(ImageInputStream in, int[] signature)
    throws IOException
  {
    boolean result = true;

    for (int i = 0; i<signature.length; i++) {
      if (in.read() != signature[i]) {
        result = false;
        break;
      }
    }

    return result;
  }
}
