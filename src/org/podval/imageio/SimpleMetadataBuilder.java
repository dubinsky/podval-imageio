package org.podval.imageio;


public class SimpleMetadataBuilder implements MetadataBuilder {

  public SimpleMetadataBuilder(Metadata result) {
    this.result = result;
    this.tip = result.getRoot();
  }


  public void startDirectory(Directory directory) {
    push(directory);
  }


  public void endDirectory() {
    pop();
  }


  public void startRecord(Record record) {
    push(record);
  }


  public void endRecord() {
    pop();
  }


  public void startComplexField(Field field) {
    push(field);
  }


  public void endComplexField() {
    pop();
  }


  public void addField(Field field, Entry value) {
    tip.addEntry(value);
  }


  public MakerNote getMakerNote() {
//  String make = metadata.getStringValue("make");
//  MakerNote makerNote = MakerNote.get(make);
//...
//  result.addEntry(new Group("UnknownMakerNote-" + make));
    return null; /** @todo XXXXX */
  }


  private void push(Typed typed) {
    Group group = new Group(typed.getName());
    group.setParent(tip);
    tip = group;
  }

  private void pop() {
    /** @todo check that we do not overpop, and that the we pop what we pushed... */
    Group parent = tip.getParent();
    parent.addEntry(tip.flatten());
    tip = parent;
  }


  private Metadata result;


  private Group tip;
}
