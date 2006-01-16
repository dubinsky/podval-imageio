/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public enum TypeNG {

  U8(1 /*, true*/) {
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedByte();
    }
  },

  X8(1, true) {  // structure
    public Object read(ImageInputStream in) throws IOException {
      return in.readUnsignedByte();
    }
  },

  STRING(1, true),

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

  U16_OR_U32(0), /** @todo actual types */


  ONE(0),

  TWO(0);


  private TypeNG(int length) {
    this(length, false);
  }


  private TypeNG(int length, boolean isVariableLength) {
    this.length = length;
    this.isVariableLength = isVariableLength;
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


  public final boolean isVariableLength;


//  public final boolean isDirectoryAllowed;   // U32, ONE, TWO
//  public final boolean isRecordAllowed;
//  public final boolean isVectorAllowed;      // U16
//  public final boolean isEnumerationAllowed; // U16, U8, X8
//  private Type[] allowedFieldTypes;          // U32: U32, S32, F32; U16: U16, S16; self: U32, U8, X8, STRING, RATIONAL, SRATIONAL, U16_OR_U32
//  private Type[] allowedSubfieldTypes;       // U32: U16, U8

//  public boolean isFieldAllowed(Type type) {
//    return (allowedFieldTypes != null) &&
//      (((allowedFieldTypes.length == 0) && (type == this)) ||
//      find(type, allowedFieldTypes));
//  }
//
//
//  public Type getDefaultFieldType() {
//    assert (allowedFieldTypes != null) : "No default field type for non-record types.";
//    return (allowedFieldTypes.length == 0) ? this : allowedFieldTypes[0];
//  }
//
//
//  private void setAllowedSubfieldTypes(Type[] allowedSubfieldTypes) {
//    assert (allowedSubfieldTypes == null) || (allowedSubfieldTypes.length != 0)
//      : "List of allowed subfield types can not be empty!";
//
//    this.allowedSubfieldTypes = allowedSubfieldTypes;
//  }
}
