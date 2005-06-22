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
    try {
      in.mark();
      readPrologue();
      result = true;
      in.reset();
    } catch (IOException e) {
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


  protected final void processHeap(long offset, long length, int tag)
    throws IOException
  {
    Heap parentHeap = currentHeap;

    if (currentHeap != null) {
      currentHeap = currentHeap.getHeap(tag);
    } else {
      if ((level == 0) && (metaMetaData != null)) {
        currentHeap = metaMetaData.getInitialHeap(tag);
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


  protected abstract void readHeap(long offset, long length) throws IOException;


  protected final void readEntry(long offset, long offsetBase)
    throws IOException
  {
    in.seek(offset);
    readEntry(offsetBase);
  }


  protected abstract void readEntry(long offsetBase) throws IOException;


  protected final void processRecord(long offset, long length, TypeNG type, int count, int tag) {
    /* It is much simpler to just do seek right here, but if the data is not
     needed, the seek() would be wasted... */
    this.offset = offset;

    handler.readRecord(tag, type, length, count, this);
  }


  private final void seekToData() throws IOException {
    in.seek(offset);
  }


  public void stream(long length, OutputStream os) throws IOException {
    seekToData();

    for (long i = 0; i < length; i++) {
      int b = in.read();
      os.write(b);
    }

    os.close();
  }


  protected final ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaDataNG metaMetaData;


  private Heap currentHeap;


  private int level;


  private long offset;
}
