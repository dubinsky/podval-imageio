package org.podval.imageio;

/**
 */

public class FilteringMetadataHandler implements MetadataHandler {

  public void setNextHandler(MetadataHandler nextHandler) {
    this.nextHandler = nextHandler;
  }


  public void startGroup(Typed folder) {
    nextHandler.startGroup(folder);
  }


  public void endGroup() {
    nextHandler.endGroup();
  }


  public void addField(Field field, Object value) {
    nextHandler.addField(field, value);
  }


  public MakerNote getMakerNote() {
    return nextHandler.getMakerNote();
  }


  private MetadataHandler nextHandler;
}
