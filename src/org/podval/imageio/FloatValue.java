package org.podval.imageio;


public class FloatValue extends SimpleValue {

  public FloatValue(String name, float value) {
    super(name);
    this.value = value;
  }


  protected String getValueAsString() {
    return Float.toString(value);
  }


  private final float value;
}
