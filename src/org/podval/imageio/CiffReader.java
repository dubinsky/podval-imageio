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


  protected void read() throws IOException {
    int heapLength = TypeNG.toInt(in.length() - headerLength);
    foundHeap(headerLength, heapLength, 0, null);
  }


  protected void readHeap(int tag) throws IOException {
    /** @todo this is really unused in this class */
//    processHeap(offset, length, tag, type);
  }


  protected void readHeap(long offset, int length) throws IOException {
    in.seek(offset + length - 4);

    int offsetTblOffset = readUnsignedInt();
    in.seek(offset + offsetTblOffset);

    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    for (int i = 0; i < numEntries; i++) {
      in.seek(entryOffset(entriesOffset, i));
      readEntry(offset);
    }
  }


  private long entryOffset(long entriesOffset, int entryNumber) {
    return entriesOffset + 10*entryNumber;
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

      TypeNG type = decodeType(dataType);

      long offset;
      int length;

      if (inHeapSpace) {
        length = readUnsignedInt();
        offset = offsetBase + readUnsignedInt();
      } else {
        length = 8;
        offset = in.getStreamPosition();
      }

      boolean isHeap = ((type == TypeNG.ONE) || (type == TypeNG.TWO));

      if (isHeap) {
        foundHeap(offset, length, idCode, type);

      } else {
        foundRecord(offset, length, length / type.length, idCode, type);
      }
    }
  }


  private static TypeNG decodeType(int dataType) throws IOException {
    TypeNG result;

    switch (dataType) {
    case 0: result = TypeNG.U8    ; break;
    case 1: result = TypeNG.STRING; break;
    case 2: result = TypeNG.U16   ; break;
    case 3: result = TypeNG.U32   ; break;
    case 4: result = TypeNG.X8    ; break;
    case 5: result = TypeNG.ONE   ; break;
    case 6: result = TypeNG.TWO   ; break;
    default:
      throw new IOException("Unknown data type.");
    }

    return result;
  }


  private int headerLength;
}
