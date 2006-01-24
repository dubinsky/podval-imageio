/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;


public enum TypeNG {

  U8(1) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedByte();
    }
  },

  S8(1) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readByte();
    }
  },

  X8(1) {  // structure
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

  F32(4) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readFloat();
    }
  },

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

  F64(8) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readDouble();
    }
  },

  U16_OR_U32(0), /** @todo actual types */

  ONE(0),

  TWO(0);


  private TypeNG(int length) {
    this.length = length;
  }


  public final int getLength() {
    return length;
  }


  protected Object read(ImageInputStream in) throws IOException {
    throw new UnsupportedOperationException();
  }


  public final boolean isVariableLength() {
    return (this == U8) || (this == X8) || (this == STRING);
  }


  public final boolean isHeapAllowed() {
    return (this == U32) || (this == ONE) || (this == TWO);
  }


  public final boolean isEnumerationAllowed() {
    return (this == U16) || (this == U8) || (this == X8);
  }


  public final boolean isRecordAllowed() {
    return !allowedFields.isEmpty();
  }


  public final boolean isVectorAllowed() {
    return (this == U16);
  }


  public final boolean isFieldAllowed(TypeNG type) {
    return allowedFields.contains(type);
  }


  public final boolean isSubFieldAllowed(TypeNG type) {
    return allowedSubFields.contains(type);
  }


  private void addAllowedFields(TypeNG... value) {
    allowedFields.addAll(Arrays.asList(value));
  }


  private void addAllowedSubFields(TypeNG... value) {
    allowedSubFields.addAll(Arrays.asList(value));
  }


  static {
    U8.addAllowedFields(U8);
    X8.addAllowedFields(X8);
    STRING.addAllowedFields(STRING);
//    U16.setAllowedFieldTypes(U16, S16);
    U32.addAllowedFields(U32, S32, F32);
    S32.addAllowedFields(S32);
    RATIONAL.addAllowedFields(RATIONAL);
    SRATIONAL.addAllowedFields(SRATIONAL);
    U16_OR_U32.addAllowedFields(U16_OR_U32);
//    ONE
//    TWO

    U32.addAllowedSubFields(U16, U8);
  }


  public static int readUnsignedInt(ImageInputStream in) throws IOException {
    return toInt(in.readUnsignedInt());
  }


  public static int toInt(long value) throws IOException {
    /** @todo check - or catch and transform? */
    return (int) value;
  }


  private final int length;


  private final Set<TypeNG> allowedFields = new HashSet<TypeNG>();


  private final Set<TypeNG> allowedSubFields = new HashSet<TypeNG>();
}
