package org.podval.imageio;

import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.MetaMetaData;

import org.podval.imageio.util.Util;

import java.io.IOException;


public class CiffReader extends Reader {

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


  protected void read(MetaMetaData metaMetaData) throws IOException {
    int heapLength = Util.toInt(in.length() - headerLength);
    metaMetaData.getHeap("ciff-root").read(this, headerLength, heapLength, 0, false);
  }


  public boolean seekToHeap() throws IOException {
    return true;
  }


  public int readNumberOfHeapEntries(long offset, int length)
    throws IOException
  {
    in.seek(offset + length - 4);

    int offsetTblOffset = readUnsignedInt();
    in.seek(offset + offsetTblOffset);

    return in.readUnsignedShort();
  }


  public int getEntryLength() {
    return 10;
  }


  public EntryInformation readEntryInformation(long offsetBase)
    throws IOException
  {
    int typeCode = in.readUnsignedShort();

    boolean empty =
      (typeCode == 0 /* Null entry. */) ||
      (typeCode == 1 /* Free entry. */);

    return empty ? null : readEntryInformation(typeCode, offsetBase);
  }


  private EntryInformation readEntryInformation(int typeCode, long offsetBase)
    throws IOException
  {
    int storageMethod = (typeCode & 0xC000) >> 14;
    int dataType      = (typeCode & 0x3800) >> 11;
    int idCode        =  typeCode & 0x07FF;

    if ((storageMethod & 0x02) != 0) {
      throw new IOException("Unknown storage method.");
    }

    boolean inHeapSpace = ((storageMethod & 0x01) == 0);

    long offset;
    int length;

    if (inHeapSpace) {
      length = readUnsignedInt();
      offset = offsetBase + readUnsignedInt();
    } else {
      length = 8;
      offset = in.getStreamPosition();
    }

    Type type = decodeType(dataType);

    boolean isHeap = ((type == Type.ONE) || (type == Type.TWO));

    Entry.Kind kind = (isHeap ? Entry.Kind.HEAP : Entry.Kind.RECORD);

    return new EntryInformation(kind, offset, length, idCode, type);
  }


  private static Type decodeType(int dataType) throws IOException {
    Type result;

    switch (dataType) {
    case 0: result = Type.U8    ; break;
    case 1: result = Type.STRING; break;
    case 2: result = Type.U16   ; break;
    case 3: result = Type.U32   ; break;
    case 4: result = Type.X8    ; break;
    case 5: result = Type.ONE   ; break;
    case 6: result = Type.TWO   ; break;
    default:
      throw new IOException("Unknown data type.");
    }

    return result;
  }


  private int headerLength;
}
