package org.podval.imageio;

import javax.imageio.metadata.IIOMetadataNode;


public class PointerValue extends Value {

  public PointerValue(String name, long offset, long length) {
    super(name);
    this.offset = offset;
    this.length = length;
  }


  protected void buildNativeTree(IIOMetadataNode result) {
    result.setAttribute("offset", Long.toString(offset));
    result.setAttribute("length", Long.toString(length));
  }


  protected String getValueAsString() {
    /** @todo eliminate... */
    return null;
  }


  private final long offset;


  private final long length;
}
