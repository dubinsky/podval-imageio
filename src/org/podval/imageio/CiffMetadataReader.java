package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class CiffMetadataReader {

  public static void read(ImageInputStream in, MetadataBuilder builder)
    throws IOException
  {
    MetaMetadata.load();

    long heapLength = readPrologue(in);
    readHeap(Directory.get("ciff-root"), in, heapLength, builder);
  }


  private static long readPrologue(ImageInputStream in) throws IOException {
    Util.determineByteOrder(in);

    long headerLength = in.readUnsignedInt();

    if (!readSignature(in))
      throw new IOException("Bad CIFF signature.");

    if (in.readUnsignedInt() != 0x00010002)
      throw new IOException("Bad CIFF version signature.");

    long heapLength = in.length() - headerLength;

    in.seek(headerLength);

    return heapLength;
  }


  private static final int[] CIFF_SIGNATURE = {'H', 'E', 'A', 'P', 'C', 'C', 'D', 'R'};


  public static boolean readSignature(ImageInputStream in) throws IOException {
    return Util.readSignature(in, CIFF_SIGNATURE);
  }


  /**
   * At the begining must be positioned at the start of the heap.
   */
  private static void readHeap(Directory heap, ImageInputStream in, long length,
    MetadataBuilder builder) throws IOException
  {
    builder.startDirectory(heap);

    long offset = in.getStreamPosition();
    in.seek(offset + length - 4);

    long offsetTblOffset = in.readUnsignedInt();
    in.seek(offset + offsetTblOffset);

    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    for (int i = 0; i < numEntries; i++) {
      in.seek(entriesOffset + 10 * i);

      int typeCode = in.readUnsignedShort();

      if ((typeCode != 0 /* Null entry. */) && (typeCode != 1 /* Free entry. */)) {
        long dataLength;

        if (inHeapSpace(typeCode)) {
          dataLength = in.readUnsignedInt();
          long dataOffset = in.readUnsignedInt();
          in.seek(offset + dataOffset);
        } else {
          dataLength = 8;
        }

        int idCode = typeCode & 0x3FFF;
        readEntry(heap, idCode, in, dataLength, builder);
      }
    }

    builder.endDirectory();
  }


  private static boolean inHeapSpace(int typeCode) throws IOException {
    boolean result = false;

    int storageMethod = (typeCode >> 14) & 0x03;

    switch (storageMethod) {
    case 0: result = true ; break;
    case 1: result = false; break;

    case 2:
    case 3:
      throw new IOException("Unknown storage method.");
    }

    return result;
  }


  private static void readEntry(Directory heap, int idCode, ImageInputStream in, long length,
    MetadataBuilder builder) throws IOException
  {
    int tag = idCode & 0x07FF;
    Type type = decodeType((idCode >> 11) & 0x07);

    Object entry = heap.getEntry(tag, type);

    if (entry != null) {
      if (entry instanceof Record)
        ((Record) entry).readWithLength(in, type, length, builder);
      else
      if (entry instanceof Directory)
        readHeap((Directory) entry, in, length, builder);
      else
        assert false : "Unknown heap record entry " + entry;
    }
  }


  private static Type decodeType(int dataType) throws IOException {
    Type result;

    switch (dataType) {
    case 0: result = Type.U8    ; break;
    case 1: result = Type.STRING; break;
    case 2: result = Type.U16   ; break;
    case 3: result = Type.U32   ; break;
    case 4: result = Type.X8    ; break; // "structure"
    case 5: result = Type.ONE   ; break;
    case 6: result = Type.TWO   ; break;
    default:
      throw new IOException("Unknown data type.");
    }

    return result;
  }
}
