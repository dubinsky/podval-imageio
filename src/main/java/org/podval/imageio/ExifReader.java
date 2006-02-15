/* $Id$ */

package org.podval.imageio;

import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.MetaMetaData;

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


  protected void read(MetaMetaData metaMetaData) throws IOException {
    // Since virtually all the tags (except 513 and 514) seem to be allowed
    // both in IFD0 and IFD1 (including EXIF and GPS IFDs),
    // the same directory descriptor can be used for IFD1 too.
    Heap rootHeap = metaMetaData.getHeap("exif-root");
    if (rootHeap.read(this, 0, 0, 0, true)) {
      rootHeap.read(this, 0, 0, 1, false);
    }
  }


  public boolean seekToHeap() throws IOException {
    long offset = in.readUnsignedInt();
    boolean result = (offset != 0);
    if (result) {
      in.seek(offsetBase + offset);
    }
    return result;
  }


  public int readNumberOfHeapEntries(long offset, int length)
    throws IOException
  {
    return in.readUnsignedShort();
  }


  public int getEntryLength() {
    return 12;
  }


  public EntryInformation readEntryInformation(long offsetBase)
    throws IOException
  {
    offsetBase = this.offsetBase;

    int tag = in.readUnsignedShort();
    Type type = decodeType(in.readUnsignedShort());
    int count = readUnsignedInt();
    int length = count * type.getLength();

    long offset = (length > 4) ?
      offsetBase + in.readUnsignedInt() :
      in.getStreamPosition();

    return new EntryInformation(Entry.Kind.UNKNOWN, offset, length, tag, type);
  }


  private static Type decodeType(int code) throws IOException {
    Type result;

    switch (code) {
    case  1: result = Type.U8       ; break; // "byte"
    case  2: result = Type.STRING   ; break; // "ASCII string"
    case  3: result = Type.U16      ; break; // "short"
    case  4: result = Type.U32      ; break; // "long"
    case  5: result = Type.RATIONAL ; break; // "rational" (two longs)
    case  6: result = Type.S8       ; break;
    case  7: result = Type.X8       ; break; // "undefined"
    case  8: result = Type.S16      ; break;
    case  9: result = Type.S32      ; break; // "slong"
    case 10: result = Type.SRATIONAL; break; // "srational"
    case 11: result = Type.F32      ; break; // "single float"
    case 12: result = Type.F64      ; break; // "double float"

    default:
      throw new IOException("Unknown data type " + code);
    }

    return result;
  }


  public final void handleValue(int tag, String name, int count, Object value) {
    if ("make".equals(name)) {
      make = (String) value;
    }

    super.handleValue(tag, name, count, value);
  }


  public final String getMake() {
    return make;
  }


  private String make;


  private long offsetBase;
}
