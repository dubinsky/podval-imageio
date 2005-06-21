/* $Id$ */

package org.podval.imageio;

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
    this.handler = handler;
    readPrologue();
    doRead();
  }


  protected abstract void readPrologue() throws IOException;


  protected abstract int[] getSignature();


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


  protected final boolean readSignature() throws IOException {
    boolean result = true;
    int[] signature = getSignature();
    for (int i = 0; i<signature.length; i++) {
      if (in.read() != signature[i]) {
        result = false;
        break;
      }
    }

    return result;
  }


  protected final void processRecord(long dataOffset, long dataLength, TypeNG dataType, int dataCount, int dataTag) {
    this.dataOffset = dataOffset;
    this.dataLength = dataLength;
    this.dataType = dataType;
    this.dataTag = dataTag;
    this.dataCount = dataCount;

    handler.readRecord(this);
  }


  public final ImageInputStream getInputStream() throws IOException {
    in.seek(dataOffset);
    return in;
  }


  public final long getDataLength() {
    return dataLength;
  }


  public final TypeNG getDataType() {
    return dataType;
  }


  public final int getDataCount() {
    return dataCount;
  }


  public int getDataTag() {
    return dataTag;
  }


  protected final ImageInputStream in;


  protected ReaderHandler handler;


  private long dataOffset;


  private long dataLength;


  private TypeNG dataType;


  private int dataCount;


  private int dataTag;


  private int tag;
}
