package org.podval.imageio;

import java.util.Stack;


public class DefaultMetadataHandler implements MetadataHandler {

  public DefaultMetadataHandler(String name) {
    result = new Metadata(name);
    tip.push(result.getRoot());
  }


  public void startGroup(Typed typed) {
    Group node = new Group();
    add(typed, node);
    tip.push(node);
  }


  public void endGroup() {
    tip.pop();
  }


  public void addField(Field field, Object value) {
    add(field, value);
  }


  private void add(Typed typed, Object value) {
    ((Group) tip.peek()).addBinding(typed, value);
  }


  public MakerNote getMakerNote() {
    return MakerNote.get(result.getStringValue("make"));
  }


  public Metadata getResult() {
    return result;
  }


  private final Metadata result;


  private Stack tip = new Stack();
}
