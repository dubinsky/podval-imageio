package org.podval.imageio;

import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import javax.imageio.stream.ImageInputStream;

/** @todo yakyak! Dependency on IIOMetadataNode here?! OTOH, why not... */
import javax.imageio.metadata.IIOMetadataNode;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class Field extends Typed {

  public Field(String name) {
    super(name);
  }


  protected boolean checkType() {
    return true;
  }


  private boolean isSimple() {
    return ((subfields == null) || (subfields.size() == 0));
  }


  public String getKind() {
    return "Field";
  }


  public void setEnumeration(Enumeration value) {
    if (value != null) {
      if (subfields != null)
        throw new IllegalArgumentException(
          "Subfields and enumeration can not be used together!");

      if (!getType().isEnumerationAllowed())
        throw new IllegalArgumentException("Enumeration is not allowed for " + this);

    }

    enumeration = value;
  }


  public void setConversion(Method value) {
    if (value != null) {
      if (subfields != null)
        throw new IllegalArgumentException(
          "Subfields and conversion can not be used together!");
    }

    conversion = value;
  }


  public Method getConversion() {
    return conversion;
  }


  public void addSubfield(Field subfield) {
    if (enumeration != null)
      throw new IllegalArgumentException(
        "Subfields and enumeration can not be used together!");

    if (subfields == null)
      subfields = new LinkedList();

    if (!getType().isSubfieldAllowed(subfield.getType()))
      throw new IllegalArgumentException("Subfield " + subfield + " not allowed in " + this);

    subfields.add(subfield);

    /** @todo  check that length is correct. When? */
  }


  public void read(ImageInputStream in, Type type, long count,
    MetadataHandler handler) throws IOException
  {
    assert (count == 1) || (type.isVariableLength());

    if (isSimple()) {
      if (!type.isVariableLength())
        readSimple(in, type, handler);
      else
        readVariableLength(in, type, count, handler);
    } else
      readComplex(in, type, handler);
  }


  private void readSimple(ImageInputStream in, Type type,
    MetadataHandler handler) throws IOException
  {
    Type fieldType = getType();
    if (fieldType != Type.U16_OR_U32)
      type = fieldType;

    if (type == Type.U32) integerValue(in.readUnsignedInt()  , handler); else
    if (type == Type.S32) integerValue(in.readInt()          , handler); else
    if (type == Type.U16) integerValue(in.readUnsignedShort(), handler); else
    if (type == Type.S16) integerValue(in.readShort()        , handler); else

    if (type == Type.F32      ) floatingValue(in.readFloat()        , handler); else
    if (type == Type.RATIONAL ) floatingValue(readRational      (in), handler); else
    if (type == Type.SRATIONAL) floatingValue(readSignedRational(in), handler); else

      assert false : "Unexpected field type " + type;
  }


  private static final int MAX_RECORD_LENGTH = 40;


  private void readVariableLength(ImageInputStream in, Type type, long count,
    MetadataHandler handler) throws IOException
  {
    Type fieldType = getType();
    if ((fieldType == Type.X8_STRING) && (type == Type.X8)) {
      type = Type.STRING;
    } else {
      type = fieldType;
    }

    if (type == Type.STRING)
      addValue(readString(in, (int) count), handler);
    else
    if ((type == Type.U8) || (type == Type.X8)) {
      if (count == 1)
        integerValue(in.readUnsignedByte(), handler);
      else {
        if (count < MAX_RECORD_LENGTH) {
          byte[] value = new byte[(int) count];
          in.read(value);
          addValue(new BinaryValue(value), handler);
        } else {
          addValue(new PointerValue(in.getStreamPosition(), count), handler);
        }
      }
    } else

      assert false : "Unexpected field type " + type;
  }


  private void readComplex(ImageInputStream in, Type type, MetadataHandler handler)
    throws IOException
  {
    handler.startGroup(this);

    long complexValue = in.readUnsignedInt(); /** @todo Only U32 fields may have subfields!!! */

    for (Iterator i = subfields.iterator(); i.hasNext();) {
      Field field = (Field) i.next();
      Type fieldType = field.getType();

      long value;

      if (fieldType == Type.U16) {
        value = (complexValue >> 16) & 0xFFFF;
        complexValue = complexValue << 16;
      } else
      if (fieldType == Type.U8) {
        value = (complexValue >> 24) & 0xFF;
        complexValue = complexValue << 8;
      } else
        throw new InternalError("Unexpected subfield type " + fieldType);

      field.integerValue(value, handler);
    }

    handler.endGroup();
  }


  private void integerValue(long value, MetadataHandler handler) {
    Object v;
    if (enumeration == null)
      v = new Long(value);
    else
      v = enumeration.getDescription((int) value);

    addValue(v, handler);
  }


  private void floatingValue(float value, MetadataHandler handler) {
    addValue(new Float(value), handler);
  }


  private void addValue(Object value, MetadataHandler handler) {
    Method conversion = getConversion();
    if (conversion != null) {
      try {
        /** @todo when the method is not static, error message is very confusing... */
        value = conversion.invoke(null, new Object[] {value});
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }

    handler.addField(this, value);
  }


  private static float readRational(ImageInputStream in) throws IOException {
    long numerator = in.readUnsignedInt();
    long denominator = in.readUnsignedInt();
    return ((float) numerator) / ((float) denominator);
  }


  private static float readSignedRational(ImageInputStream in) throws IOException {
    int numerator = in.readInt();
    int denominator = in.readInt();
    return ((float) numerator)/((float) denominator);
  }


  private static final int MAX_STRING_LENGTH = 64;


  private static String readString(ImageInputStream in, int length) throws IOException {
    // Length of 0 indicates 'indefinite'. We limit 'em here...
    if (length == 0)
      length = MAX_STRING_LENGTH;

    StringBuffer result = new StringBuffer(length);

    for (int i=0; ; i++) {
      int b = in.readByte();
      if (b == 0) break;
      if (i == length) {
////        result.append("|NO ZERO. TRUNCATED?");
        break;
      }
      result.append((char) b);
    }

    return new String(result).trim();
  }


  private Enumeration enumeration;


  private Method conversion;


  private List subfields = null;



  /**
   *
   */
  private static final class PointerValue implements Group.ComplexValue {
    public PointerValue(long offset, long length) {
      this.offset = offset;
      this.length = length;
    }

    public void buildNativeTree(IIOMetadataNode result) {
      result.setAttribute("offset", Long.toString(offset));
      result.setAttribute("length", Long.toString(length));
    }

    public long getOffset() { return offset; }
    public long getLength() { return length; }
    private final long offset;
    private final long length;
  }



  /**
   *
   */
  private static final class BinaryValue {
    public BinaryValue(byte[] value) { this.value = value; }

    private static final char[] HEX_DIGIT = new char[]
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static char toHexDigit(int d) { return HEX_DIGIT[d]; }

    public String toString() {
      StringBuffer result = new StringBuffer(2*value.length);
      for (int i=0; i<value.length; i++) {
        byte b = value[i];
        result.append(toHexDigit( b       & 0x0F));
        result.append(toHexDigit((b >> 4) & 0x0F));
      }
      return new String(result);
    }

    private final byte[] value;
  }
}
