package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public class MakerNote {

  public static final Object MARKER = new Object();


  public static void load(org.podval.imageio.jaxb.MakerNote xml) {
    String make = xml.getMake();
    String signature = xml.getSignature();
    Directory directory = Directory.define(xml);

    makerNotes.put(make, new MakerNote(signature, directory));
  }


  public static MakerNote get(String make) {
    return (MakerNote) makerNotes.get(make);
  }


  private static final Map makerNotes = new HashMap();


  private MakerNote(String signature, Directory directory) {
    this.signature = signature;
    this.directory = directory;
  }


  public String getSignature() {
    return signature;
  }


  public Directory getDirectory() {
    return directory;
  }


  private final String signature;


  private final Directory directory;
}
