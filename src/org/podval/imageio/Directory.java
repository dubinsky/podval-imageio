package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


public class Directory extends Typed {

  public static Directory load(org.podval.imageio.jaxb.Directory xml) {
    Directory result = get(xml.getName());
    result.add(xml);
    return result;
  }


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


  private void add(org.podval.imageio.jaxb.Directory xml) {
    super.add(xml);

    for (Iterator i = xml.getEntries().iterator(); i.hasNext();) {
      Object o = i.next();

      if (o instanceof org.podval.imageio.jaxb.SubDirectory) {
        org.podval.imageio.jaxb.SubDirectory directoryXml =
          (org.podval.imageio.jaxb.SubDirectory) o;
        addEntry(directoryXml.getTag(), Directory.load(directoryXml));
      } else

      if (o instanceof org.podval.imageio.jaxb.MakerNoteMarker) {
        org.podval.imageio.jaxb.MakerNoteMarker markerXml =
          (org.podval.imageio.jaxb.MakerNoteMarker) o;
        int tag = markerXml.getTag();
        Type type = Type.parse(markerXml.getType());
        entries.put(new Key(tag, type), MakerNote.MARKER);
      } else

      if (o instanceof org.podval.imageio.jaxb.SubRecord) {
        org.podval.imageio.jaxb.SubRecord recordXml =
          (org.podval.imageio.jaxb.SubRecord) o;
        addEntry(recordXml.getTag(), Record.loadLocal(recordXml));
      } else

        assert false : "Unknown directory entry " + o;
    }
  }


  private void addEntry(int tag, Typed entry) {
    Type type = entry.getType();

    for (Iterator i = type.getActualTypes().iterator(); i.hasNext();) {
      Key key = new Key(tag, (Type) i.next());
      Object oldEntry = entries.get(key);
      if (oldEntry == null) {
        entries.put(key, entry);
      } else {
        throw new IllegalArgumentException(
          "Attempt to replace " + key + ":" + oldEntry + " with " + entry);
      }
    }
  }


  public Object getEntry(int tag, Type type) {
    Key key = new Key(tag, type);
    Object result = entries.get(key);

    if ((result == null) && MetaMetadata.isDecodeUnknown()) {
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
