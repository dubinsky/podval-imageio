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


  public Metadata getResult() {
    return result;
  }


  private final Metadata result;


  private Group tip;
}
