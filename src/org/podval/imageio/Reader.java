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


  public final ImageInputStream getInputStream() {
    return in;
  }


  public final ReaderHandler getHandler() {
    return handler;
  }


  public final MetaMetaData getMetaMetaData() {
    return metaMetaData;
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


  public final boolean readInitialHeap(String name, int tag, boolean seekAfter)
    throws IOException
  {
    return readInitialHeap(name, 0, 0, tag, seekAfter);
  }


  public final boolean readInitialHeap(String name, long offset, int length, int tag, boolean seekAfter)
    throws IOException
  {
    return metaMetaData.getHeap(name, null).read(this, offset, length, tag, seekAfter);
  }


  protected abstract HeapInformation readHeapInformation(long offset, int length)
    throws IOException;


  public final void seek(long offset) throws IOException {
    in.seek(offset);
  }


  protected abstract int getEntryLength();


  protected abstract EntryInformation readEntryInformation(long offsetBase)
    throws IOException;


  protected abstract boolean seekToHeap() throws IOException;


  /** @todo eliminate: moved to Field */
  public final void handleRecord(long offset, int tag, Type type, int count, Record record)
    throws IOException
  {
    ReaderHandler.ValueAction action = handler.atValue(tag, record.getName(), type, count);

    if ((action != null) && (action != ReaderHandler.ValueAction.SKIP)) {
      in.seek(offset);

      switch (action) {
      case RAW  : handler.handleRawValue(tag, record.getName(), type, count, in); break;
      case VALUE: handler.handleValue(tag, record.getName(), type, count, readValue(type, count, record)); break;
      }
    }
  }


  /** @todo eliminate: moved to Field */
  public Object readValue(Type type, int count, Record record) throws IOException {
    /** @todo type/count sanity checks... */
    Object result = null;

    if (type == Type.STRING) {
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
        if ((type == Type.U8) || (type == Type.X8)) {
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
    byte[] result = new byte[length];
    in.readFully(result);
    return result;
  }


  protected int readUnsignedInt() throws IOException {
    return Type.readUnsignedInt(in);
  }


  protected ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaData metaMetaData;
}
