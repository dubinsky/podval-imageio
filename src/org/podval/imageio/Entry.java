package org.podval.imageio;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadataNode;


public abstract class Entry {

  public final void setName(String name) {
    this.name = name;
  }


  public final String getName() {
    return name;
  }


  public abstract Entry getEntry(String name);


  public abstract Node getNativeTree();


  private String name;
}
