package org.podval.imageio;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import javax.imageio.ImageIO;

import javax.imageio.stream.ImageInputStream;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;


public class Metadata extends IIOMetadata {

  public Metadata(String nativeMetadataFormatName) {
    super(false, nativeMetadataFormatName, null, null, null);
    root = new Group();
  }


  public boolean isReadOnly() {
    return true;
  }


  public void reset() {
    throw new IllegalStateException("This is a read-only metadata!");
  }


  public void mergeTree(String formatName, Node root) {
    throw new IllegalStateException("This is a read-only metadata!");
  }


  public Node getAsTree(String formatName) {
    if (formatName.equals(getNativeMetadataFormatName()))
      return getNativeTree();
    else
      throw new IllegalArgumentException("Not a recognized format " + formatName);
  }


  public Group getRoot() {
    return root;
  }


  public int getIntValue(String name) {
    int result = -1;
    Object value = find(name);
    if ((value != null) && (value instanceof Long)) {
      result = ((Long) value).intValue();
    }
    return result;
  }


  public String getStringValue(String name) {
    String result = null;
    Object value = find(name);
    if ((value != null) && (value instanceof String)) {
      result = (String) value;
    }
    return result;
  }


  public Object find(String name) {
    return root.find(name);
  }


  public Node getNativeTree() {
    return root.getNativeTree(getNativeMetadataFormatName());
  }


  private final Group root;
}
