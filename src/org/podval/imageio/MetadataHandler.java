package org.podval.imageio;

public interface MetadataHandler {

  public void startFolder(Typed folder);

  public void endFolder();

  public void addField(Field field, Entry value);

  public MakerNote getMakerNote();
}
