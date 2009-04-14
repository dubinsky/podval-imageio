/* $Id$ */

package org.podval.imageio.metametadata;

import org.podval.imageio.Reader;
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
      Key key = new Key(tag, type);

      if (entries.get(key) != null) {
        throw new IllegalArgumentException("Attempt to replace " + entry.getName() + " with " + getName() +
          "; key " + key);
      }

      entries.put(key, entry);
    }
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
    if (reader.startFolder(tag, getName())) {
      int numEntries = reader.readNumberOfHeapEntries(offset, length);
      long entriesOffset = reader.getInputStream().getStreamPosition();

      for (int i = 0; i < numEntries; i++) {
        seekToEntry(reader, entriesOffset, i);
        EntryInformation entryInformation = reader.readEntryInformation(offset);
        if (entryInformation != null) {
          getEntry(
            entryInformation.kind,
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

      reader.endFolder();
    }
  }


  public Entry getEntry(Entry.Kind kind, int tag, Type type)
    throws IOException
  {
    Entry result = getEntry(new Key(tag, type));

    try {
      if (result == null) {
        boolean isHeap = (kind == Entry.Kind.HEAP);

        String name = "unknown-" + tag;
        result = (isHeap) ? new Heap(name) : new Record(name);
        result.setType(type);
        addEntry(tag, result);
      }

      result.checkKind(kind);

    } catch (MetaMetaDataException e) {
      throw new IOException(e.getMessage());
    }

    return result;
  }


  private void seekToEntry(Reader reader, long entriesOffset, int entryNumber)
    throws IOException
  {
    reader.getInputStream().seek(entriesOffset + reader.getEntryLength()*entryNumber);
  }


  protected Kind getKind() {
    return Kind.HEAP;
  }


  private final Map<Key, Entry> entries = new HashMap<Key, Entry>();
}
