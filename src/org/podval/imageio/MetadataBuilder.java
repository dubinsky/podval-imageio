package org.podval.imageio;

public interface MetadataBuilder {

  public void startDirectory(Directory directory);

  public void endDirectory();

  public void startRecord(Record record);

  public void endRecord();

  public void startComplexField(Field field);

  public void endComplexField();

  public void addField(Field field, Entry value);

  public MakerNote getMakerNote();
}
