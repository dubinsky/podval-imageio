/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class CiffReader extends Reader {

  public CiffReader(ImageInputStream in) {
    super(in);
  }


  private static final int[] CIFF_SIGNATURE = {'H', 'E', 'A', 'P', 'C', 'C', 'D', 'R'};


  protected void readPrologue() throws IOException {
    determineByteOrder();

    headerLength = readUnsignedInt();

    if (!readSignature(CIFF_SIGNATURE)) {
      throw new IOException("Bad CIFF signature.");
    }

    if (readUnsignedInt() != 0x00010002) {
      throw new IOException("Bad CIFF version.");
    }
  }


  protected void readHeap(long offset, int length, int tag) throws IOException {
    processHeap(offset, length, tag);
  }


  protected void doRead() throws IOException {
    int heapLength = TypeNG.toInt(in.length() - headerLength);
    processHeap(headerLength, heapLength, 0);
  }


  protected void readHeap(long offset, int length) throws IOException {
    in.seek(offset + length - 4);

    int offsetTblOffset = readUnsignedInt();
    in.seek(offset + offsetTblOffset);

    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    for (int i = 0; i < numEntries; i++) {
      readEntry(entriesOffset + 10*i, offset);
    }
  }


  protected void readEntry(long offsetBase) throws IOException {
    int typeCode = in.readUnsignedShort();

    if ((typeCode != 0 /* Null entry. */) &&
        (typeCode != 1 /* Free entry. */))
    {
      int storageMethod = (typeCode & 0xC000) >> 14;
      int dataType      = (typeCode & 0x3800) >> 11;
      int idCode        =  typeCode & 0x07FF;

      if ((storageMethod & 0x02) != 0) {
        throw new IOException("Unknown storage method.");
      }

      boolean inHeapSpace = ((storageMethod & 0x01) == 0);

      if (dataType == 0x07) {
        throw new IOException("Unknown data type.");
      }

      boolean isHeap = ((dataType == 0x05) || (dataType == 0x06));

      long offset;
      int length;

      if (inHeapSpace) {
        length = readUnsignedInt();
        offset = offsetBase + readUnsignedInt();
      } else {
        length = 8;
        offset = in.getStreamPosition();
      }

      if (isHeap) {
        processHeap(offset, length, idCode);

      } else {
        TypeNG type = decodeType(dataType);
        processRecord(offset, length, type, length / type.length, idCode);
      }
    }
  }


  private static TypeNG decodeType(int dataType) {
    TypeNG result;

    switch (dataType) {
    case 0: result = TypeNG.U8       ; break;
    case 1: result = TypeNG.STRING   ; break;
    case 2: result = TypeNG.U16      ; break;
    case 3: result = TypeNG.U32      ; break;
    case 4: result = TypeNG.STRUCTURE; break;
    default:
      throw new IllegalArgumentException("dataType");
    }

    return result;
  }


  private int headerLength;
}
