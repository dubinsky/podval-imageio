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
    return (subfields.size() == 0);
  }


  public String getKind() {
    return "Field";
  }


  public void read(ImageInputStream in, Type type, long count,
    MetadataBuilder builder) throws IOException
  {
    assert (count == 1) || (type.isVariableLength());

    if (!isSimple())
      readComplex(in, type, builder);
    else
    if (!type.isVariableLength())
      builder.addField(this, readSimple(in, type));
    else
      builder.addField(this, readVariableLength(in, type, count));
  }


  private Entry readSimple(ImageInputStream in, Type type) throws IOException {
    Entry result = null;

    Type fieldType = getType();
    if (fieldType != Type.U16_OR_U32)
      type = fieldType;

    if (type == Type.U32) result = wrapIntegerValue(in.readUnsignedInt()); else
    if (type == Type.S32) result = wrapIntegerValue(in.readInt()); else
    if (type == Type.U16) result = wrapIntegerValue(in.readUnsignedShort()); else
    if (type == Type.S16) result = wrapIntegerValue(in.readShort()); else
    if (type == Type.F32) result = new FloatValue(getName(), in); else
    if (type == Type.RATIONAL) result = FloatValue.readRational(getName(), in); else
    if (type == Type.SRATIONAL) result = FloatValue.readSignedRational(getName(), in); else
      assert false : "Unexpected field type " + type;

    return result;
  }


  private Entry readVariableLength(ImageInputStream in, Type type, long count) throws IOException {
    Entry result = null;

    Type fieldType = getType();
    if ((fieldType == Type.X8_STRING) && (type == Type.X8)) {
      type = Type.STRING;
    } else {
      type = fieldType;
    }

    if (type == Type.STRING) {
      result = new StringValue(getName(), in, count);
    } else
    if ((type == Type.U8) || (type == Type.X8)) {
      if (count == 1)
        result = wrapIntegerValue(in.readUnsignedByte());
      else
        result = HexValue.read(getName(), in, count);
    } else
      assert false : "Unexpected field type " + type;

    return result;
  }


  public void readComplex(ImageInputStream in, Type type, MetadataBuilder builder)
    throws IOException
  {
    builder.beginComplexField(this);

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

      builder.addField(field, field.wrapIntegerValue(value));
    }

    builder.endComplexField();
  }


  public Entry wrapIntegerValue(long value) {
    Entry result;

    if (enumeration == null)
      result = new IntegerValue(getName(), value);
    else
      result = new StringValue(getName(), enumeration.getDescription((int) value));

    return result;
  }


  private Enumeration enumeration;


  private final List subfields = new LinkedList();
}
