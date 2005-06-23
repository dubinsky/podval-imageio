/* $Id$ */

package org.podval.imageio;

import java.io.OutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;


public abstract class Reader {

  protected Reader(ImageInputStream in) {
    this.in = in;
  }


  public final boolean canRead() {
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


  public final void read(ReaderHandler handler) throws IOException {
    read(handler, null);
  }


  public final void read(ReaderHandler handler, MetaMetaDataNG metaMetaData)
    throws IOException
  {
    readPrologue();

    this.handler = handler;
    this.metaMetaData = metaMetaData;

    doRead();
  }


  protected abstract void readPrologue() throws IOException;


  protected abstract void doRead() throws IOException;


  /**
   * Determines byte order used from a special marker, and arranges so that it
   * is used for all subsequant reads.
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


  protected final void processHeap(long offset, int length, int tag)
    throws IOException
  {
    Heap parentHeap = currentHeap;

    if (currentHeap != null) {
      currentHeap = currentHeap.getHeap(tag);
    } else {
      if ((level == 0) && (metaMetaData != null)) {
        currentHeap = metaMetaData.getInitialHeap();
      }
    }

    level++;

    if (handler.startHeap(tag, currentHeap)) {
      readHeap(offset, length);
    }

    handler.endHeap();

    currentHeap = parentHeap;

    level--;
  }


  protected abstract void readHeap(long offset, int length) throws IOException;


  protected abstract void readHeap(long offset, int length, int tag) throws IOException;


  protected final void readEntry(long offset, long offsetBase)
    throws IOException
  {
    in.seek(offset);
    readEntry(offsetBase);
  }


  protected abstract void readEntry(long offsetBase) throws IOException;


  protected final void processEntry(long offset, int length, TypeNG type, int count, int tag)
    throws IOException
  {
    Entry entry = (currentHeap == null) ? null : currentHeap.getEntry(tag, type, length, count);

    if (entry instanceof Heap) {
      readHeap(offset, length, tag);
    } else

    if ((entry instanceof RecordNG) || (entry == null)) {
      processRecord(offset, length, type, count, tag);
    }

    /** @todo  */
//      if (entry == MakerNote.MARKER) {
//        MakerNote makerNote = handler.getMakerNote();
//        readIfdInPlace(makerNote.getDirectory(), in, offsetBase, handler);
//      } else
//        assert false : "Unknown IFD entry " + entry;
  }


  protected final void processRecord(long offset, int length, TypeNG type, int count, int tag)
    throws IOException
  {
    /* It is much simpler to just do seek right here, but if the data is not
     needed, the seek() would be wasted... */
    this.offset = offset;

    RecordNG record = (currentHeap == null) ? null : currentHeap.getRecord(tag, type, length, count);

    handler.readRecord(tag, type, length, count, this, record);
  }


  private final void seekToData() throws IOException {
    in.seek(offset);
  }


  /** @todo this belongs in a separate interface for value retrieval */
  public Object readValue(TypeNG type, int length, int count) throws IOException {
    /** @todo type/length/count sanity checks... */
    Object result = null;
    seekToData();

    if (type == TypeNG.STRING) {
      result = readString(length);
    } else {
      if (count == 1) {
        result = type.read(in);
      } else {
        if ((type == TypeNG.U8) || (type == TypeNG.STRUCTURE)) {
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
  public byte[] readBytes(int length) throws IOException {
    seekToData();
    return doReadBytes(length);
  }


  private byte[] doReadBytes(int length) throws IOException {
    byte[] result = new byte[(int) length]; /** @todo cast */
    in.readFully(result);
    return result;
  }


  /** @todo this belongs in a separate interface for value retrieval */
  public void stream(int length, OutputStream os) throws IOException {
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


  protected final ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaDataNG metaMetaData;


  private Heap currentHeap;


  private int level;


  private long offset;
}
