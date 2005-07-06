package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


public class Directory extends Typed {

  /**
   * ... autoregisters.
   * @param name
   * @return
   */
  public static Directory get(String name) {
    Directory result = (Directory) directories.get(name);

    if (result == null) {
      result = new Directory(name);
      directories.put(name, result);
    }

    return result;
  }


  private static final Map directories = new HashMap();


  private Directory(String name) {
    super(name);
  }


  protected String getKind() {
    return "Directory";
  }


  protected boolean checkType() {
    return getType().isDirectoryAllowed();
  }


  public void addEntry(int tag, Typed entry) {
    for (Iterator i = entry.getType().getActualTypes().iterator(); i.hasNext();)
      addEntry(tag, (Type) i.next(), entry);
  }


  public void addEntry(int tag, Type type, Object entry) {
    Key key = new Key(tag, type);
    Object oldEntry = entries.get(key);
    if (oldEntry == null) {
      entries.put(key, entry);
    } else {
      throw new IllegalArgumentException(
        "Attempt to replace " + key + ":" + oldEntry + " with " + entry);
    }
  }


  public Object getEntry(int tag, Type type) {
    Key key = new Key(tag, type);
    Object result = entries.get(key);

    if ((result == null) /*&& MetaMetadata.isDecodeUnknown()*/) {
      Typed entry = null;

      String name = "unknown-" + key;

      if (type.isRecordAllowed())
        entry = new Record(name);
      else
      if (type.isDirectoryAllowed())
        entry = new Directory(name);
      else
        assert false : "Unknown directory entry type " + type;

      entry.setType(type);
      addEntry(tag, entry);
      result = entry;
    }

    return result;
  }



  private static class Key {

    public Key(int tag, Type type) {
      this.tag = tag;
      this.type = type;
    }


    public boolean equals(Object o) {
      Key other = (Key) o;
      return (this.tag == other.tag) && (this.type == other.type);
    }


    public int hashCode() {
      return tag;
    }


    public String toString() {
      return tag + "-" + type;
    }


    private final int tag;


    private final Type type;
  }


  private final Map entries = new HashMap();
}
