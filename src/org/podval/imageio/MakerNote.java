package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;


public class MakerNote {

  public static final Object MARKER = new Object();


  public static void register(String make, String signature, Directory directory) {
    makerNotes.put(make, new MakerNote(signature, directory));
  }


  public static MakerNote get(String make) {
    MakerNote result = (MakerNote) makerNotes.get(make);
    if (result == null) {
      result = new MakerNote(null, Directory.get("unknownMakerNote-" + make));
      makerNotes.put(make, result);
    }
    return result;
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
