package org.podval.imageio
  ;

import javax.imageio.metadata.IIOMetadataNode;


public abstract class Value extends Entry {

  protected Value(String name) {
    super(name);
  }


  protected void buildNativeTree(IIOMetadataNode result) {
    result.setAttribute("value", getValueAsString());
  }


  protected abstract String getValueAsString();
}
