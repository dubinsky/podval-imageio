package org.podval.imageio;

import java.io.IOException;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import javax.imageio.stream.ImageInputStream;


public class Field extends Typed {

  public Field(org.podval.imageio.jaxb.Field xml) {
    super(xml.getName());
    super.add(xml);

    org.podval.imageio.jaxb.Enumeration enumeration = xml.getEnumeration();
    List subfields = xml.getSubfields();

    if ((enumeration != null) && (subfields.size() != 0))
        throw new IllegalArgumentException(
          "Subfields and enumeration can not be used together!");

    addEnumeration(enumeration);
    addSubfields(subfields);
  }


  public Field(String name, Type type) {
    this(name, type, null);
  }


  public Field(String name, Type type, org.podval.imageio.jaxb.Enumeration enumeration) {
    super(name);
    setType(type);
    addEnumeration(enumeration);
  }


  private void addEnumeration(org.podval.imageio.jaxb.Enumeration enumeration) {
    if (enumeration != null) {
      if (!getType().isEnumerationAllowed())
        throw new IllegalArgumentException("Enumeration is not allowed for " + this);

      this.enumeration = new Enumeration(enumeration);
    } else {
      this.enumeration = null;
    }
  }


  private void addSubfields(List subfields) {
    if (this.subfields == null)
      this.subfields = new LinkedList();

    for (Iterator i = subfields.iterator(); i.hasNext();) {
      Field field = new Field((org.podval.imageio.jaxb.Field) i.next());

      if (!getType().isSubfieldAllowed(field.getType()))
        throw new IllegalArgumentException("Subfield " + field + " not allowed in " + this);

      this.subfields.add(field);
    }

    /** @todo  check that length is correct. */
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

    if (type == Type.F32      ) handler.floatValue(this, in.readFloat()        ); else
    if (type == Type.RATIONAL ) handler.floatValue(this, readRational      (in)); else
    if (type == Type.SRATIONAL) handler.floatValue(this, readSignedRational(in)); else

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
      handler.stringValue(this, readString(in, (int) count));
    else
    if ((type == Type.U8) || (type == Type.X8)) {
      if (count == 1)
        integerValue(in.readUnsignedByte(), handler);
      else {
        if (count < MAX_RECORD_LENGTH) {
          byte[] value = new byte[(int) count];
          in.read(value);
          handler.binaryValue(this, value);
        } else {
          handler.pointerValue(this, in.getStreamPosition(), count);
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
    if (enumeration == null)
      handler.integerValue(this, value);
    else
      handler.stringValue(this, enumeration.getDescription((int) value));
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


  private List subfields = null;
}
