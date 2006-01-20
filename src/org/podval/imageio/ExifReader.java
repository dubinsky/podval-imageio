/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class ExifReader extends Reader {

  private static int[] EXIF_SIGNATURE = {'E', 'x', 'i', 'f', 0, 0};


  protected void readPrologue() throws IOException {
    if (!readSignature(EXIF_SIGNATURE)) {
      throw new IOException("Bad EXIF signature.");
    }

    offsetBase = in.getStreamPosition();

    determineByteOrder();

    if (in.readUnsignedShort() != 0x2A) {
      throw new IOException("Bad TIFF magic.");
    }
  }


  protected void read() throws IOException {
    // Since virtually all the tags (except 513 and 514) seem to be allowed
    // both in IFD0 and IFD1 (including EXIF and GPS IFDs),
    // the same directory descriptor can be used for IFD1 too.
    String heapName = "org_podval_imageio_exif_1.0";
    if (readInitialHeap(heapName, 0, true)) {
      readInitialHeap(heapName, 1, false);
    }
  }


  protected boolean seekToHeap() throws IOException {
    long offset = in.readUnsignedInt();
    boolean result = (offset != 0);
    if (result) {
      in.seek(offsetBase + offset);
    }
    return result;
  }


  protected HeapInformation readHeapInformation(long offset, int length)
    throws IOException
  {
    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    return new HeapInformation(entriesOffset, numEntries);
  }


  protected int getEntryLength() {
    return 12;
  }


  protected EntryInformation readEntryInformation(long offsetBase)
    throws IOException
  {
    offsetBase = this.offsetBase;

    int tag = in.readUnsignedShort();
    TypeNG type = decodeType(in.readUnsignedShort());
    int count = readUnsignedInt();
    int length = count * type.getLength();

    long offset = (length > 4) ?
      offsetBase + in.readUnsignedInt() :
      in.getStreamPosition();

    return new EntryInformation(Entry.Kind.UNKNOWN, offset, length, tag, type);
  }


  private static TypeNG decodeType(int code) throws IOException {
    TypeNG result;

    switch (code) {
    case  1: result = TypeNG.U8       ; break; // "byte"
    case  2: result = TypeNG.STRING   ; break; // "ASCII string"
    case  3: result = TypeNG.U16      ; break; // "short"
    case  4: result = TypeNG.U32      ; break; // "long"
    case  5: result = TypeNG.RATIONAL ; break; // "rational" (two longs)
    case  6: result = TypeNG.S8       ; break;
    case  7: result = TypeNG.X8       ; break; // "undefined"
    case  8: result = TypeNG.S16      ; break;
    case  9: result = TypeNG.S32      ; break; // "slong"
    case 10: result = TypeNG.SRATIONAL; break; // "srational"
    case 11: result = TypeNG.F32      ; break; // "single float"
    case 12: result = TypeNG.F64      ; break; // "double float"

    default:
      throw new IOException("Unknown data type " + code);
    }

    return result;
  }


  private long offsetBase;
}
