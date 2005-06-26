/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public enum TypeNG {

  U8(1) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedByte();
    }
  },

  STRUCTURE(1) {  // X8
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedByte();
    }
  },

  STRING(1),

  U16(2) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedShort();
    }
  },

  S16(2) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readShort();
    }
  },

  U32(4) {
    public Object read(ImageInputStream in) throws IOException {
      return readUnsignedInt(in);
    }
  },

  S32(4) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readInt();
    }
  },

  F32(4),

  RATIONAL(8) {
    public Object read(ImageInputStream in) throws IOException {
      return new Rational(readUnsignedInt(in), readUnsignedInt(in));
    }
  },

  SRATIONAL(8) {
    public Object read(ImageInputStream in) throws IOException {
      return new Rational(in.readInt(), in.readInt());
    }
  },

  U16_OR_U32(0),


  ONE(0),

  TWO(0);



  private TypeNG(int length) {
    this.length = length;
  }


  public int getLength() {
    return length;
  }


  protected Object read(ImageInputStream in) throws IOException {
    throw new UnsupportedOperationException();
  }


  public static int readUnsignedInt(ImageInputStream in) throws IOException {
    return toInt(in.readUnsignedInt());
  }


  public static int toInt(long value) throws IOException {
    /** @todo check - or catch and transform? */
    return (int) value;
  }


  public final int length;
}

