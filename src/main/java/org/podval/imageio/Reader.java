/* $Id$ */

package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;

import org.podval.imageio.util.Util;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;


public abstract class Reader implements ReaderHandler {

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
    read(in, handler, new MetaMetaData(""));
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


  public final void seek(long offset) throws IOException {
    in.seek(offset);
  }


  public final ReaderHandler getHandler() {
    return handler;
  }


  public final MetaMetaData getMetaMetaData() {
    return metaMetaData;
  }


  /**
   * Reads format-specific prologue.
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
    return metaMetaData.getInitialHeap().read(this, offset, length, tag, seekAfter);
  }


  public abstract HeapInformation readHeapInformation(long offset, int length)
    throws IOException;


  public abstract int getEntryLength();


  public abstract EntryInformation readEntryInformation(long offsetBase)
    throws IOException;


  public abstract boolean seekToHeap() throws IOException;


  public final boolean startFolder(int tag, String name) {
    return getHandler().startFolder(tag, name);
  }


  public final void endFolder() {
    getHandler().endFolder();
  }


  public final ValueAction atValue(int tag, String name, int count) {
    return getHandler().atValue(tag, name, count);
  }


  public void handleValue(int tag, String name, int count, Object value) {
    getHandler().handleValue(tag, name, count, value);
  }


  public final void handleValue(int tag, String name, Object value)
    throws IOException
  {
    ValueAction action = atValue(tag, name, 1);

    if ((action != null) && (action != ValueAction.SKIP)) {
      if (action == ValueAction.RAW) {
        throw new IOException("Can not read this value as raw");
      }

      handleValue(tag, name, 1, value);
    }
  }


  public final void handleRawValue(int tag, String name, int count, ImageInputStream is)
    throws IOException
  {
    getHandler().handleRawValue(tag, name, count, is);
  }


  protected int readUnsignedInt() throws IOException {
    return Util.readUnsignedInt(getInputStream());
  }


  protected ImageInputStream in;


  private ReaderHandler handler;


  private MetaMetaData metaMetaData;
}
