package org.podval.imageio;

public interface MetadataBuilder {

  public void beginDirectory(Directory directory);

  public void endDirectory();

  public void beginRecord(Record record);

  public void endRecord();

  public void beginComplexField(Field field);

  public void endComplexField();

  public void addField(Field field, Entry value);

  public MakerNote getMakerNote();
}
