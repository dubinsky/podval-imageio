/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
import org.podval.imageio.HeapInformation;
import org.podval.imageio.EntryInformation;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import java.io.IOException;


public class Heap extends Entry {

  public Heap(String name) {
    super(name);
  }


  public Heap(String name, Type type) throws MetaMetaDataException {
    super(name, type);
  }


  protected final void checkType() throws MetaMetaDataException {
    if (!getType().isHeapAllowed()) {
      throw new MetaMetaDataException("Wrong heap type: " + this);
    }
  }


  public void addEntry(int tag, Entry entry) {
    if (entry.getType() == null) {
      throw new IllegalArgumentException("Attempt to add an entry " + this.getName() + "." + entry.getName() + " without type");
    }

    for (Type type : entry.getType().getActualTypes()) {
      addEntry(tag, type, entry);
    }
  }


  private void addEntry(int tag, Type type, Entry entry) {
    Key key = new Key(tag, type);

    Entry oldEntry = entries.get(key);
    if (oldEntry != null) {
      throw new IllegalArgumentException("Attempt to replace " + entry.getName() + " with " + getName() +
        "; key " + key);
    }
    entries.put(key, entry);
  }


  public final Entry getEntry(int tag, Type type) {
    return getEntry(new Key(tag, type));
  }


  public final Entry getEntry(Key key) {
    return entries.get(key);
  }


  public final List<Key> getKeys() {
    List<Key> result = new LinkedList<Key>(entries.keySet());
    Collections.sort(result);
    return result;
  }


  public void read(Reader reader, long offset, int length, int tag, Type type)
    throws IOException
  {
    read(reader, offset, length, tag, false);
  }


  public final boolean read(Reader reader, long offset, int length, int tag, boolean seekAfter)
    throws IOException
  {
    boolean result = reader.seekToHeap();

    if (result) {
      readInPlace(reader, offset, length, tag, seekAfter);
    }

    return result;
  }


  protected final void readInPlace(Reader reader, long offset, int length, int tag, boolean seekAfter)
    throws IOException
  {
    if (reader.getHandler().startFolder(tag, getName())) {
      HeapInformation heapInformation = reader.readHeapInformation(offset, length);
      long entriesOffset = heapInformation.entriesOffset;
      int numEntries = heapInformation.numEntries;

      for (int i = 0; i < numEntries; i++) {
        seekToEntry(reader, entriesOffset, i);
        EntryInformation entryInformation = reader.readEntryInformation(offset);
        if (entryInformation != null) {
          reader.getMetaMetaData().getEntry(
            entryInformation.kind,
            this,
            entryInformation.tag,
            entryInformation.type
          ).read(
            reader,
            entryInformation.offset,
            entryInformation.length,
            entryInformation.tag,
            entryInformation.type
          );
        }
      }

      if (seekAfter) {
        seekToEntry(reader, entriesOffset, numEntries);
      }

      reader.getHandler().endFolder();
    }
  }


  private void seekToEntry(Reader reader, long entriesOffset, int entryNumber)
    throws IOException
  {
    reader.seek(entriesOffset + reader.getEntryLength()*entryNumber);
  }


  protected String getKind() {
    return "Heap";
  }



  /**
   */
  public static final class Key implements Comparable<Key> {

    public Key(int tag, Type type) {
      this.tag = tag;
      this.type = type;
    }


    public int compareTo(Key other) {
      int result;

      if (tag < other.tag) {
        result = -1;
      } else
      if (tag > other.tag) {
        result = +1;
      } else {
        result = type.compareTo(other.type);
      }

      return result;
    }


    public boolean equals(Object o) {
      Key other = (Key) o;
      return (this.tag == other.tag) && (this.type == other.type);
    }


    public int hashCode() {
      int result = tag*317;
      if (type != null) {
        result += type.hashCode();
      }
      return result;
    }


    public String toString() {
      return tag + "-" + type;
    }


    public final int tag;


    public final Type type;
  }



  private final Map<Key, Entry> entries = new HashMap<Key, Entry>();
}
