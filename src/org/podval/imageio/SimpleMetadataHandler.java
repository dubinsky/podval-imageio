package org.podval.imageio;


public class SimpleMetadataHandler implements MetadataHandler {

  public SimpleMetadataHandler(String name) {
    result = new Metadata(name);
    tip = result.getRoot();
  }


  public void startFolder(Typed folder) {
    Group group = new Group(folder.getName());
    group.setParent(tip);
    tip = group;
  }


  public void endFolder() {
    /** @todo check that we do not overpop, and that the we pop what we pushed... */
    Group parent = tip.getParent();
    parent.addEntry(tip.flatten());
    tip = parent;
  }


  public void integerValue(Field field, long value) {
    value(new IntegerValue(field.getName(), value));
  }


  public void stringValue(Field field, String value) {
    value(new StringValue(field.getName(), value));
  }


  public void floatValue(Field field, float value) {
    value(new FloatValue(field.getName(), value));
  }


  public void binaryValue(Field field, byte[] value) {
    value(new HexValue(field.getName(), value));
  }


  public void pointerValue(Field field, long offset, long length) {
    value(new PointerValue(field.getName(), offset, length));
  }


  private void value(Entry value) {
    tip.addEntry(value);
  }


  public MakerNote getMakerNote() {
//  String make = metadata.getStringValue("make");
//  MakerNote makerNote = MakerNote.get(make);
//...
//  result.addEntry(new Group("UnknownMakerNote-" + make));
    return null; /** @todo XXXXX */
  }


  public Metadata getResult() {
    return result;
  }


  private final Metadata result;


  private Group tip;
}
