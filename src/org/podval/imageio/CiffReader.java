/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class CiffReader extends Reader {

  public static boolean canDecodeInput(ImageInputStream in) {
    boolean result = false;
    try {
      in.mark();
      readPrologue(in);
      result = true;
      in.reset();
    } catch (IOException e) {
    }
    return result;
  }


  private static long readPrologue(ImageInputStream in) throws IOException {
    determineByteOrder(in);

    long headerLength = in.readUnsignedInt();

    if (!readSignature(in, CIFF_SIGNATURE)) {
      throw new IOException("Bad CIFF signature.");
    }

    if (in.readUnsignedInt() != 0x00010002) {
      throw new IOException("Bad CIFF version.");
    }

    return headerLength;
  }


  private static final int[] CIFF_SIGNATURE = {'H', 'E', 'A', 'P', 'C', 'C', 'D', 'R'};


  public static void read(ImageInputStream in, CiffHandler handler) throws IOException {
    long headerLength = readPrologue(in);
    long heapLength = in.length() - headerLength;
    readHeap(in, headerLength, heapLength, 0, handler);
  }


  private static void readHeap(ImageInputStream in, long offset, long length, int idCode, CiffHandler handler)
    throws IOException
  {
    boolean process = handler.startHeap(idCode);

    if (process) {
      in.seek(offset + length - 4);

      long offsetTblOffset = in.readUnsignedInt();
      in.seek(offset + offsetTblOffset);

      int numEntries = in.readUnsignedShort();
      long entriesOffset = in.getStreamPosition();

      for (int i = 0; i < numEntries; i++) {
        in.seek(entriesOffset + 10 * i);
        readEntry(in, offset, handler);
      }
    }

    handler.endHeap();
  }


  private static void readEntry(ImageInputStream in, long offset, CiffHandler handler)
    throws IOException
  {
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

      long dataOffset;
      long dataLength;

      if (inHeapSpace) {
        dataLength = in.readUnsignedInt();
        dataOffset = offset + in.readUnsignedInt();
      } else {
        dataLength = 8;
        dataOffset = in.getStreamPosition();
      }

      if (isHeap) {
        readHeap(in, dataOffset, dataLength, idCode, handler);
      } else {
        CiffType type = CiffType.valueOf(dataType);
        handler.readRecord(in, dataOffset, dataLength, type, idCode);
      }
    }
  }
}
