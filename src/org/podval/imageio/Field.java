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


  public void setSkip(boolean value) {
    skip = value;
  }


  public void read(ImageInputStream in, Type type, long count,
    MetadataHandler handler) throws IOException
  {
    assert (count == 1) || (type.isVariableLength());

    if (isSimple()) {
      addValue(readSimple(in, type, count), handler);
    } else
      readComplex(in, type, handler);
  }


  private static final int MAX_RECORD_LENGTH = 40;

  private Object readSimple(ImageInputStream in, Type type, long count)
    throws IOException
  {
    Object result = null;

    Type fieldType = getType();
    if (fieldType != Type.U16_OR_U32)
      type = fieldType;

    if (type == Type.U32) result = readIntegerValue(in.readUnsignedInt()  ); else
    if (type == Type.S32) result = readIntegerValue(in.readInt()          ); else
    if (type == Type.U16) result = readIntegerValue(in.readUnsignedShort()); else
    if (type == Type.S16) result = readIntegerValue(in.readShort()        ); else

    if (type == Type.F32      ) result = new Float(in.readFloat()        ); else
    if (type == Type.RATIONAL ) result = new Float(readRational(in)      ); else
    if (type == Type.SRATIONAL) result = new Float(readSignedRational(in)); else

    if (type == Type.STRING) result = readString(in, (int) count); else

    if ((type == Type.U8) || (type == Type.X8)) {
      if (count == 1)
        result = readIntegerValue(in.readUnsignedByte());
      else {
        if (count < MAX_RECORD_LENGTH) {
          byte[] value = new byte[(int) count];
          in.read(value);
          result = new BinaryValue(value);
        } else {
          result = new PointerValue(in.getStreamPosition(), count);
        }
      }
    } else

      assert false : "Unexpected field type " + type;

    return result;
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

      field.addValue(field.readIntegerValue(value), handler);
    }

    handler.endGroup();
  }


  private void addValue(Object value, MetadataHandler handler) {
    if (!skip) {
      Method conversion = getConversion();
      if (conversion!=null) {
        try {
            /** @todo when the method is not static, error message is very confusing... */
          value = conversion.invoke(null, new Object[] {value});
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
      }

      handler.addField(this, value);
    }
  }


  private Object readIntegerValue(long value) {
    Object result;
    if (enumeration == null)
      result = new Long(value);
    else
      result = enumeration.getValue((int) value);

    return result;
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


  private boolean skip = false;



  /**
   *
   */
  public static final class PointerValue {
    public PointerValue(long offset, long length) {
      this.offset = offset;
      this.length = length;
    }

    public String toString() {
      return offset+ ":" +length;
    }

    public long getOffset() { return offset; }
    public long getLength() { return length; }
    private final long offset;
    private final long length;
  }



  /**
   *
   */
  public static final class BinaryValue {
    public BinaryValue(byte[] value) { this.value = value; }

    private static final char[] HEX_DIGIT = new char[]
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static char toHexDigit(int d) { return HEX_DIGIT[d]; }

    public byte[] getValue() {
      return value;
    }

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
