package org.podval.imageio;

import java.util.Stack;


public class DefaultMetadataHandler implements MetadataHandler {

  public DefaultMetadataHandler(String name) {
    result = new Metadata(name);
    tip.push(result.getRoot());
  }


  public void startFolder(Typed folder) {
    Group group = new Group();
    group.setName(folder.getName());
    ((Group) tip.peek()).addEntry(group);
    tip.push(group);
  }


  public void endFolder() {
    tip.pop();
  }


  public void integerValue(Field field, long value) {
    value(field, new Value.IntegerValue(value));
  }


  public void stringValue(Field field, String value) {
    value(field, new Value.StringValue(value));
  }


  public void floatValue(Field field, float value) {
    value(field, new Value.FloatValue(value));
  }


  public void binaryValue(Field field, byte[] value) {
    value(field, new Value.HexValue(value));
  }


  public void pointerValue(Field field, long offset, long length) {
    value(field, new Value.PointerValue(offset, length));
  }


  private void value(Field field, Value value) {
    value.setName(field.getName());
    ((Group) tip.peek()).addEntry(value);
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
