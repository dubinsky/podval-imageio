package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class FloatValue extends SimpleValue {

  public FloatValue(String name, float value) {
    super(name);
    this.value = value;
  }


  public FloatValue(String name, ImageInputStream in)
    throws IOException
  {
    this(name, in.readFloat());
  }


  protected String getValueAsString() {
    return Float.toString(value);
  }


  public static Entry readRational(String name, ImageInputStream in)
    throws IOException
  {
    long numerator = in.readUnsignedInt();
    long denominator = in.readUnsignedInt();
    float value = ((float) numerator) / ((float) denominator);
    return new FloatValue(name, value);
  }


  public static Entry readSignedRational(String name, ImageInputStream in)
    throws IOException
  {
    int numerator = in.readInt();
    int denominator = in.readInt();
    float value = ((float) numerator)/((float) denominator);
    return new FloatValue(name, value);
  }


  private final float value;
}
