package org.podval.imageio;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadataNode;


public abstract class Entry {

  protected Entry(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }


  public Node getNativeTree() {
    IIOMetadataNode result = new IIOMetadataNode(getName());

    buildNativeTree(result);

    return result;
  }


  protected abstract void buildNativeTree(IIOMetadataNode result);


  public Entry flatten() {
    return this;
  }


  public Entry getEntry(String name) {
    return (getName().equals(name)) ? this : null;
  }


  private final String name;
}
