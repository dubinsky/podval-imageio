package org.podval.imageio;

import javax.imageio.metadata.IIOMetadataNode;


public class IntegerValue extends SimpleValue {

  public IntegerValue(String name, long value) {
    super(name);
    this.value = value;
  }


  public long getValue() {
    return value;
  }


  protected String getValueAsString() {
    return Long.toString(value);
  }


  private final long value;
}
