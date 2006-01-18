/* $Id$ */

package org.podval.imageio;

import java.io.OutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;


public abstract class Reader {

  private static final int DEFAULT_MAX_COUNT = 64;


  public final void setMaxCount(int value) {
    maxCount = value;
  }


  public final int getMaxCount() {
    return maxCount;
  }


  /**
   * Checks if the stream seems to be of the appropriate format.
   * Strem position is unchanged by this method, but other stream attributes
   * might change (e.g., byte order).
   * Attempts to read format-specific prolog and returns <code>true</code>
   * if succesfull and <code>false</code> if it fails.

   * @return boolean
   */
  public final boolean canRead(ImageInputStream in) {
    if (in == null) {
      throw new NullPointerException("in");
    }

    this.in = in;

    boolean result = false;

    in.mark();
    try {
      readPrologue();
      result = true;
    } catch (IOException e) {
    } finally {
      try {
        in.reset();
      } catch (IOException e) {
      }
    }

    return result;
  }


  public final void read(ImageInputStream in, ReaderHandler handler) throws IOException {
    read(in, handler, new MetaMetaData());
  }


  public final void read(ImageInputStream in, ReaderHandler handler, MetaMetaData metaMetaData)
    throws IOException
  {
    if (in == null) {
      throw new NullPointerException("in");
    }

    if (handler == null) {
      throw new NullPointerException("handler");
    }

    if (metaMetaData == null) {
      throw new NullPointerException("metaMetaData");
    }

    this.in = in;
    this.handler = handler;
    this.metaMetaData = metaMetaData;

    readPrologue();

    read();
  }


  /**
   * Reads format-specific prolog.
   *
   * @throws IOException
   */
  protected abstract void readPrologue() throws IOException;


  /**
   * Reads format-specific content.
   *
   * @throws IOException
   */
  protected abstract void read() throws IOException;


  /**
   * Determines byte order used from a special marker, and arranges so that it
   * is used for all subsequent reads.
   *
   * @throws IOException
   */
  protected final void determineByteOrder() throws IOException {
    int byte1 = in.read();
    int byte2 = in.read();

    ByteOrder byteOrder;

    if ((byte1 == 'I') && (byte2 == 'I')) {
      byteOrder = ByteOrder.LITTLE_ENDIAN;
    } else

    if ((byte1 == 'M') && (byte2 == 'M')) {
      byteOrder = ByteOrder.BIG_ENDIAN;

    } else {
      throw new IOException("Bad byte order marker.");
    }

    in.setByteOrder(byteOrder);
  }


  protected final boolean readSignature(int[] signature) throws IOException {
    boolean result = true;
    for (int i = 0; i < signature.length; i++) {
      if (in.read() != signature[i]) {
        result = false;
        break;
      }
    }

    return result;
  }


//  protected final void readHeap(long offset, int length, int tag)
//    throws IOException
//  {
//    currentHeap = metaMetaData.getInitialHeap();
//  }


  /**
   * .
   * Called by the format-specific reader when it encounters a heap.
   * Parameters contain what is known about the heap at this point.
   * Metadata for the heap is obtained, a handler is notified - and is given an
   * opportunity to indicate that the heap should be skipped.
   * Actual reading of the heap is delegated to a format-specific heap reader.
   *
   * @param offset long
   * @param length int
   * @param tag int
   * @param type TypeNG
   * @throws IOException
   */
  protected final void foundHeap(long offset, int length, int tag, TypeNG type)
    throws IOException
  {
    if (!seekToHeap()) {
      return;
    }

    Heap parentHeap = currentHeap;

    if (currentHeap == null) {
      if (level == 0) {
        currentHeap = metaMetaData.getInitialHeap();
      }
    } else {
      currentHeap = metaMetaData.getHeap(currentHeap, tag, type);
    }

    level++;

    if (handler.startHeap(tag, currentHeap)) {
      readHeap(offset, length);
    }

    handler.endHeap();

    currentHeap = parentHeap;

    level--;
  }


  /**
   *
   */
  protected static class HeapInformation {

    public HeapInformation(long entriesOffset, int numEntries) {
      this.entriesOffset = entriesOffset;
      this.numEntries = numEntries;
    }


    public final long entriesOffset;


    public final int numEntries;
  }



  private void readHeap(long offset, int length) throws IOException {
    HeapInformation heapInformation = readHeapInformation(offset, length);
    long entriesOffset = heapInformation.entriesOffset;
    int numEntries = heapInformation.numEntries;

    for (int i = 0; i < numEntries; i++) {
      seekToEntry(entriesOffset, i);
      readEntry(offset);
    }

    if (seekAfterHeap()) {
      seekToEntry(entriesOffset, numEntries);
    }
  }


  private void seekToEntry(long entriesOffset, int entryNumber)
    throws IOException
  {
    in.seek(entriesOffset + getEntryLength()*entryNumber);
  }


  protected abstract boolean seekAfterHeap();


  protected abstract HeapInformation readHeapInformation(long offset, int length)
    throws IOException;


  protected abstract int getEntryLength();


  protected enum EntryKind { HEAP, RECORD, UNKNOWN }



  /**
   *
   */
  protected static class EntryInformation {

    public EntryInformation(EntryKind kind, long offset, int length, int tag, TypeNG type) {
      this.kind = kind;
      this.offset = offset;
      this.length = length;
      this.tag = tag;
      this.type = type;
    }


    public final EntryKind kind;


    public final long offset;


    public final int length;


    public final int tag;


    public final TypeNG type;
  }



  private void readEntry(long offsetBase) throws IOException {
    EntryInformation entryInformation = readEntryInformation(offsetBase);

    if (entryInformation != null) {
      EntryKind kind = entryInformation.kind;
      long offset = entryInformation.offset;
      int length = entryInformation.length;
      int tag = entryInformation.tag;
      TypeNG type = entryInformation.type;

      if (kind == EntryKind.UNKNOWN) {
        Entry entry = metaMetaData.getEntry(currentHeap, tag, type);

        if (entry instanceof Heap) {
          offset = 0;
          length = 0;
          kind = EntryKind.HEAP;
        } else

        if ((entry instanceof RecordNG) || (entry == null)) {
          kind = EntryKind.RECORD;
        }
      }

      switch (kind) {
      case HEAP   : //seekToHeap();
                    foundHeap  (offset, length, tag, type); break;
      case RECORD : foundRecord(offset, length, tag, type); break;
        /** @todo maker note... */
//      if (entry == MakerNote.MARKER) {
//        MakerNote makerNote = handler.getMakerNote();
//        readIfdInPlace(makerNote.getDirectory(), in, offsetBase, handler);
//      } else
//        assert false : "Unknown IFD entry " + entry;
      }
    }
  }


  protected abstract EntryInformation readEntryInformation(long offsetBase)
    throws IOException;


  protected abstract boolean seekToHeap() throws IOException;


  private void foundRecord(long offset, int length, int tag, TypeNG type)
    throws IOException
  {
    RecordNG record = metaMetaData.getRecord(currentHeap, tag, type);

    this.offset = offset;

    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (record.getCount() > 1) || record.isVector()) && !type.isVariableLength;

    if (treatAsFolder) {
      if (handler.startRecord(tag, record)) {
        for (int index = 0; index < count; index++) {
          RecordNG field = metaMetaData.getField(record, index);
          TypeNG fieldType = field.getType();
          int fieldLength = fieldType.getLength();
          if (!record.isVector() || (index != 0)) {
            handleRecord(index, fieldType, fieldLength, 1, field);
          } else {
            /** @todo check vector length */
          }
          this.offset += fieldLength;
        }
      }

      handler.endRecord();

    } else {
      /* It is much simpler to just do seek right here, but if the data is not
       needed, the seek() would be wasted... */
      handleRecord(tag, type, length, count, record);
    }
  }


  private void handleRecord(int tag, TypeNG type, int length, int count, RecordNG record)
    throws IOException
  {
    if (count < maxCount) {
      handler.handleShortValue(tag, type, count, record, readValue(type, count, record));
    } else {
      handler.handleLongValue(tag, type, count, record, this);
    }
  }


  private void seekToData() throws IOException {
    in.seek(offset);
  }


  /** @todo this belongs in a separate interface for value retrieval
   * current record should also be available through it, not passed in. */
  public Object readValue(TypeNG type, int count, RecordNG record) throws IOException {
    /** @todo type/length/count sanity checks... */
    Object result = null;
    seekToData();

    if (type == TypeNG.STRING) {
      result = readString(count);
    } else {
      if (count == 1) {
        result = type.read(in);
        Enumeration enumeration = record.getEnumeration();
        if (enumeration != null) {
          if (result instanceof Integer) {
            result = enumeration.getValue((Integer) result);
          }
        }
      } else {
        if ((type == TypeNG.U8) || (type == TypeNG.X8)) {
          result = doReadBytes(count);
        } else {
          Object[] objects = new Object[count];
          for (int i = 0; i<count; i++) {
            objects[i] = type.read(in);
          }
          result = objects;
        }
      }
    }

    return result;
  }


  private String readString(int length) throws IOException {
    // Length of 0 indicates 'indefinite'. We limit 'em here... - ???

    byte[] bytes = doReadBytes(length);
    int l = 0;
    for (; l<length; l++) {
      if (bytes[l] == 0) {
        break;
      }
    }

//      if (l == length) {
////        result.append("|NO ZERO. TRUNCATED?");
//      }

    return new String(bytes, 0, l).trim();
  }


  /** @todo this belongs in a separate interface for value retrieval */
  public byte[] readBytes(int count) throws IOException {
    seekToData();
    return doReadBytes(count);
  }


  private byte[] doReadBytes(int length) throws IOException {
    byte[] result = new byte[(int) length]; /** @todo cast */
    in.readFully(result);
    return result;
  }


  /** @todo this belongs in a separate interface for value retrieval */
  public void stream(OutputStream os) throws IOException {
    seekToData();

    for (long i = 0; i < length; i++) {
      int b = in.read();
      os.write(b);
    }

    os.close();
  }


  protected int readUnsignedInt() throws IOException {
    return TypeNG.readUnsignedInt(in);
  }


  private int maxCount = DEFAULT_MAX_COUNT;


  protected ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaData metaMetaData;


  private Heap currentHeap;


  private int level;


  private long offset;


  private int length; /** @todo careful - this is kinda global... */
}
