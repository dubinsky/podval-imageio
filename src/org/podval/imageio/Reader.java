/* $Id$ */

package org.podval.imageio;

import java.io.OutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;


public abstract class Reader {

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


  public final boolean readInitialHeap(int tag, boolean seekAfter)
    throws IOException
  {
    return readInitialHeap(0, 0, tag, seekAfter);
  }


  public final boolean readInitialHeap(long offset, int length, int tag, boolean seekAfter)
    throws IOException
  {
    return readHeap(offset, length, tag, metaMetaData.getInitialHeap(), seekAfter);
  }


  private boolean readHeap(long offset, int length, int tag, Heap heap, boolean seekAfter)
    throws IOException
  {
    boolean result = seekToHeap();

    if (result) {
      if (handler.startHeap(tag, heap.getName())) {
        HeapInformation heapInformation = readHeapInformation(offset, length);
        long entriesOffset = heapInformation.entriesOffset;
        int numEntries = heapInformation.numEntries;

        for (int i = 0; i < numEntries; i++) {
          seekToEntry(entriesOffset, i);
          EntryInformation entryInformation = readEntryInformation(offset);
          if (entryInformation != null) {
            readEntry(
              heap,
              entryInformation.kind,
              entryInformation.offset,
              entryInformation.length,
              entryInformation.tag,
              entryInformation.type
            );
          }
        }

        if (seekAfter) {
          seekToEntry(entriesOffset, numEntries);
        }
      }

      handler.endHeap();
    }

    return result;
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


  protected abstract HeapInformation readHeapInformation(long offset, int length)
    throws IOException;



  private void seekToEntry(long entriesOffset, int entryNumber)
    throws IOException
  {
    in.seek(entriesOffset + getEntryLength()*entryNumber);
  }


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


  protected abstract EntryInformation readEntryInformation(long offsetBase)
    throws IOException;



  private void readEntry(Heap heap, EntryKind kind, long offset, int length, int tag, TypeNG type) throws IOException {
    Entry entry = null;
    switch (kind) {
    case HEAP   : entry = metaMetaData.getHeap  (heap, tag, type); break;
    case RECORD : entry = metaMetaData.getRecord(heap, tag, type); break;
    case UNKNOWN: entry = metaMetaData.getEntry (heap, tag, type);
      if (entry instanceof Heap) {
        offset = 0;
        length = 0;
        kind = EntryKind.HEAP;
      } else

      if (entry instanceof RecordNG) {
        kind = EntryKind.RECORD;
      }

      break;
    }

    switch (kind) {
    case HEAP   : readHeap  (offset, length, tag,       (Heap)     entry, false); break;
    case RECORD : readRecord(offset, length, tag, type, (RecordNG) entry       ); break;
      /** @todo maker note... */
//    if (entry == MakerNote.MARKER) {
//      MakerNote makerNote = handler.getMakerNote();
//      readIfdInPlace(makerNote.getDirectory(), in, offsetBase, handler);
//    } else
//      assert false : "Unknown IFD entry " + entry;
    }
  }


  protected abstract boolean seekToHeap() throws IOException;


  private void readRecord(long offset, int length, int tag, TypeNG type, RecordNG record)
    throws IOException
  {
    int count = length / type.getLength();
    boolean treatAsFolder = ((count > 1) || (record.getCount() > 1) || record.isVector()) && !type.isVariableLength;

    if (treatAsFolder) {
      if (handler.startRecord(tag, record.getName())) {
        for (int index = 0; index < count; index++) {
          RecordNG field = metaMetaData.getField(record, index);
          TypeNG fieldType = field.getType();
          int fieldLength = fieldType.getLength();
          if (!record.isVector() || (index != 0)) {
            handleRecord(offset, index, fieldType, 1, field);
          } else {
            /** @todo check vector length */
          }
          offset += fieldLength;
        }
      }

      handler.endRecord();

    } else {
      /* It is much simpler to just do seek right here, but if the data is not
       needed, the seek() would be wasted... */
      handleRecord(offset, tag, type, count, record);
    }
  }


  private void handleRecord(long offset, int tag, TypeNG type, int count, RecordNG record)
    throws IOException
  {
    Object action = handler.atValue(tag, record.getName(), type, count);
    if (action != null) {
      in.seek(offset);
      if (action instanceof OutputStream) {
        stream((OutputStream) action, count);
      } else {

        Object value = null;
        if (action == Boolean.TRUE) {
          value = readValue(type, count, record);
        } else
        if (action instanceof Integer) {
          value = readBytes(Math.min((Integer) action, count));
        }

        handler.handleValue(tag, record.getName(), type, count, value);
      }
    }
  }


  public Object readValue(TypeNG type, int count, RecordNG record) throws IOException {
    /** @todo type/length/count sanity checks... */
    Object result = null;

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
          result = readBytes(count);
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

    byte[] bytes = readBytes(length);
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


  private byte[] readBytes(int length) throws IOException {
    byte[] result = new byte[(int) length]; /** @todo cast */
    in.readFully(result);
    return result;
  }


  public void stream(OutputStream os, int length) throws IOException {
    for (long i = 0; i < length; i++) {
      int b = in.read();
      os.write(b);
    }

    os.close();
  }


  protected int readUnsignedInt() throws IOException {
    return TypeNG.readUnsignedInt(in);
  }


  protected ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaData metaMetaData;
}
