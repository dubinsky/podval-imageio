/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class ExifReader extends Reader {

  public ExifReader(ImageInputStream in) {
    super(in);
  }


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


  protected void doRead() throws IOException {
    readIfd(0);

    /*
     Since virtually all the tags (except 513 and 514) seem to be allowed
     both in IFD0 and IFD1 (including EXIF and GPS IFDs),
     the same directory descriptor can be used for IFD1 too.
     At this point I do not read IFD1 at all, though, since none of the images I
     have have it, and since I need one root for the resulting metadata.
     This can be changed if need be.

    */
   /* If 0th ifd was skipped, the reader will not be properly positioned to read the 1st ifd!!! */
   readIfd(1);
  }


  private void readIfd(int tag) throws IOException {
    long offset = in.readUnsignedInt();
    if (offset != 0) {
      in.seek(offsetBase + offset);
      readIfdInPlace(tag);
    }
  }


  private void readIfdInPlace(int tag) throws IOException {
    processHeap(0, 0, tag, null);
  }


  protected void readHeap(long dummyOffset, int dummyLength, int tag, TypeNG type)
    throws IOException
  {
    readIfd(tag);
  }


  protected void readHeap(long dummyOffset, int dummyLength) throws IOException {
    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    for (int i = 0; i < numEntries; i++) {
      readEntry(entryOffset(entriesOffset, i), offsetBase);
    }

    in.seek(entryOffset(entriesOffset, numEntries));
    // At this point we are positioned at the offset of the linked IFD.
  }


  private long entryOffset(long entriesOffset, int entryNumber) {
    return entriesOffset + 12*entryNumber;
  }


  protected void readEntry(long offsetBase) throws IOException {
    int tag = in.readUnsignedShort();
    TypeNG type = decodeType(in.readUnsignedShort());
    int count = readUnsignedInt();
    int length = count * type.getLength();
    long offset;

    if (length > 4) {
      offset = offsetBase + in.readUnsignedInt();
    } else {
      offset = in.getStreamPosition();
    }

    processEntry(offset, length, count, tag, type);
  }


  private static TypeNG decodeType(int code) throws IOException {
    TypeNG result;

    switch (code) {
    case  1: result = TypeNG.U8       ; break; // "byte"
    case  2: result = TypeNG.STRING   ; break; // "ASCII string"
    case  3: result = TypeNG.U16      ; break; // "short"
    case  4: result = TypeNG.U32      ; break; // "long"
    case  5: result = TypeNG.RATIONAL ; break; // "rational" (two longs)
    //case  6: result = Type.S8       ; break;
    case  7: result = TypeNG.STRUCTURE; break; // "undefined"
    //case 8: result = Type.S16       ; break;
    case  9: result = TypeNG.S32      ; break; // "slong"
    case 10: result = TypeNG.SRATIONAL; break; // "srational"
    //case 11: result = TypeNG.F32; break; // "single float"
    //case 12: result = TypeNG.F64; break; // "double float"

    default:
      throw new IOException("Unknown data type " + code);
    }

    return result;
  }


  private long offsetBase;
}
