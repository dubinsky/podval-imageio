package org.podval.imageio;

import javax.imageio.metadata.IIOMetadataNode;


public abstract class SimpleValue extends Entry {

  protected SimpleValue(String name) {
    super(name);
  }


  protected void buildNativeTree(IIOMetadataNode result) {
    result.setAttribute("value", getValueAsString());
  }


  protected abstract String getValueAsString();
}