package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class ExifDecoder {

  public static void read(ImageInputStream in, MetadataHandler handler)
    throws IOException
  {
    long offsetBase = readPrologue(in);

    Directory ifd = handler.getInitDirectory();
    readIfd(ifd, in, offsetBase, handler);
    /*
     Since virtually all the tags (except 513 and 514) seem to be allowed
     both in IFD0 and IFD1 (including EXIF and GPS IFDs),
     the same directory descriptor can be used for IFD1 too.
     At this point I do not read IFD1 at all, though, since none of the images I
     have have it, and since I need one root for the resulting metadata.
     This can be changed if need be.

         readIfd(ifd, in, offsetBase, handler);
    */
  }


  private static int[] EXIF_SIGNATURE = {'E', 'x', 'i', 'f', 0, 0};


  private static long readPrologue(ImageInputStream in) throws IOException {
    if (!Util.readSignature(in, EXIF_SIGNATURE))
      throw new IOException("Bad EXIF signature.");

    long offsetBase = in.getStreamPosition();

    Util.determineByteOrder(in);

    if (in.readUnsignedShort() != 0x2A)
      throw new IOException("Bad TIFF magic.");

    return offsetBase;
  }


  private static void readIfd(Directory ifd, ImageInputStream in, long offsetBase,
    MetadataHandler handler) throws IOException
  {
    long offset = in.readUnsignedInt();
    if (offset != 0) {
      in.seek(offsetBase + offset);
      readIfdInPlace(ifd, in, offsetBase, handler);
    }
  }


  private static void readIfdInPlace(Directory ifd, ImageInputStream in, long offsetBase,
    MetadataHandler handler) throws IOException
  {
    handler.startGroup(ifd);

    int numEntries = in.readUnsignedShort();
    long entriesOffset = in.getStreamPosition();

    for (int i = 0; ; i++) {
      in.seek(entriesOffset + 12*i);

      if (i == numEntries)
        break;

      readEntry(ifd, in, offsetBase, handler);
    }

    handler.endGroup();

    // At this point we are positioned at the offset of the linked IFD.
  }


  private static void readEntry(Directory ifd, ImageInputStream in, long offsetBase,
    MetadataHandler handler) throws IOException
  {
    int tag = in.readUnsignedShort();
    Type type = decodeType(in.readUnsignedShort());
    long count = in.readUnsignedInt();

    if (count * type.getLength() > 4) {
      long offset = in.readUnsignedInt();
      in.seek(offsetBase + offset);
    }

    Object entry = ifd.getEntry(tag, type);

    if (entry != null) {
      if (entry instanceof Record)
        ((Record) entry).readWithCount(in, type, count, handler);
      else
      if (entry instanceof Directory)
        readIfd((Directory) entry, in, offsetBase, handler);
      else
      if (entry == MakerNote.MARKER) {
        MakerNote makerNote = handler.getMakerNote();
        readIfdInPlace(makerNote.getDirectory(), in, offsetBase, handler);
      } else
        assert false : "Unknown IFD entry " + entry;
    }
  }


  private static Type decodeType(int code) throws IOException {
    Type result;

    switch (code) {
    case  1: result = Type.U8       ; break; // "byte"
    case  2: result = Type.STRING   ; break; // "ASCII string"
    case  3: result = Type.U16      ; break; // "short"
    case  4: result = Type.U32      ; break; // "long"
    case  5: result = Type.RATIONAL ; break; // "rational" (two longs)
    //case  6: result = Type.S8       ; break;
    case  7: result = Type.X8       ; break; // "undefined"
    //case 8: result = Type.S16; break;
    case  9: result = Type.S32      ; break; // "slong"
    case 10: result = Type.SRATIONAL; break; // "srational"
    //case 11: result = Type.F32; break; // "single float"
    //case 12: result = Type.F64; break; // "double float"

    default:
      throw new IOException("Unknown data type " + code);
    }

    return result;
  }
}
