/* $Id$ */

package org.podval.imageio;

import java.util.Map;
import java.util.HashMap;

import java.io.IOException;


public class Heap extends Entry {

  public Heap(String name) {
    super(name);
  }


  public Heap(String name, Type type) {
    super(name, type);
  }


  protected final boolean checkType() {
    return true;
  }


  public void addEntry(int tag, Entry entry) {
    if (entry.getType() == null) {
      throw new IllegalArgumentException("Attempt to add an entry " + this.getName() + "." + entry.getName() + " without type");
    }

    /** @todo add under all of the entry's types
         for (Iterator i = entry.getType().getActualTypes().iterator(); i.hasNext();)
           addEntry(tag, (Type) i.next(), entry);
     */
    addEntry(tag, entry.getType(), entry);
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
    return entries.get(new Key(tag, type));
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
      int result = tag*317;
      if (type != null) {
        result += type.hashCode();
      }
      return result;
    }


    public String toString() {
      return tag + "-" + type;
    }


    private final int tag;


    private final Type type;
  }



  private final Map<Key, Entry> entries = new HashMap<Key,Entry>();
}
