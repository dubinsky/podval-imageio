package org.podval.imageio;


public interface MetadataHandler {

  public void startFolder(Typed folder);

  public void endFolder();

  public void integerValue(Field field, long value);

  public void stringValue(Field field, String value);

  public void floatValue(Field field, float value);

  public void binaryValue(Field field, byte[] value);

  public void pointerValue(Field field, long offset, long length);

  public MakerNote getMakerNote();
}
