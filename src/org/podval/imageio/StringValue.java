package org.podval.imageio;


public class StringValue extends SimpleValue {

  public StringValue(String name, String value) {
    super(name);
    this.value = value;
  }


  protected String getValueAsString() {
    return value;
  }


  public String getValue() {
    return value;
  }


  private final String value;
}
