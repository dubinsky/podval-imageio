package org.podval.imageio;


public interface MetadataHandler {

  public void startGroup(Typed folder);


  public void endGroup();


  public void addField(Field field, Object value);


  public MakerNote getMakerNote();
}
