package org.podval.imageio;


public interface MetadataHandler {

  public Directory getInitDirectory();

  public void startGroup(Typed folder);

  public void endGroup();

  public void integerValue(Field field, long value);

  public void stringValue(Field field, String value);

  public void floatValue(Field field, float value);

  public void binaryValue(Field field, byte[] value);

  public void pointerValue(Field field, long offset, long length);

  public MakerNote getMakerNote();
}
