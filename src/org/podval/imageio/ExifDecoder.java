package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.io.IOException;


public class ExifDecoder {

  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_exif_1.0";


  public static Metadata read(ImageInputStream in) throws IOException {
    /** @todo move this to SPI? */
    MetaMetadata.load();

    long offsetBase = readPrologue(in);

    Metadata result = new Metadata(NATIVE_FORMAT_NAME);
    /*
     Since virtually all the tags (except 513 and 514) seem to be allowed
     both in IFD0 and IFD1 (including EXIF AND GPS IFDs), I just use the same
     IFD twice!
    */
    readIfd(Directory.use("exif-root"), in, offsetBase, result.getRoot(), result);
    readIfd(Directory.use("exif-root"), in, offsetBase, result.getRoot(), result);

    return result;
  }


  private static int[] EXIF_SIGNATURE = {'E', 'x', 'i', 'f', 0, 0};


  private static long readPrologue(ImageInputStream in) throws IOException {
    for (int i=0; i< EXIF_SIGNATURE.length; i++) {
      int b = in.readUnsignedByte();
      if (b != EXIF_SIGNATURE[i])
        throw new IOException("Bad EXIF signature.");
    }

    long offsetBase = in.getStreamPosition();

    Util.determineByteOrder(in);

    if (in.readUnsignedShort() != 0x2A)
      throw new IOException("Bad TIFF magic.");

    return offsetBase;
  }


  private static void readIfd(Directory ifd,
    ImageInputStream in, long offsetBase,
    Group result,
    Metadata metadata
  ) throws IOException
  {
    long offset = in.readUnsignedInt();
    if (offset != 0) {
      in.seek(offsetBase+offset);
      readIfdInPlace(ifd, in, offsetBase, result, metadata);
    }
  }


  private static void readIfdInPlace(Directory ifd,
    ImageInputStream in, long offsetBase,
    Group result,
    Metadata metadata
  ) throws IOException
  {
    Group group = new Group(ifd.getName());
    result.addEntry(group);
    result = group;

    long offset = in.getStreamPosition()-offsetBase;

    int numEntries = in.readUnsignedShort();

    for (int i = 0; ; i++) {
      long entryOffset = offset + 2 + 12*i;
      in.seek(offsetBase+entryOffset);

      if (i == numEntries)
        break;

      readEntry(ifd, in, offsetBase, result, metadata);
    }

    // At this point we are positioned at the offset of the linked IFD.
  }


  private static void readEntry(Directory ifd,
    ImageInputStream in, long offsetBase,
    Group result,
    Metadata metadata
  ) throws IOException
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
        result.addEntry(((Record) entry).readWithCount(in, type, count));
      else
      if (entry instanceof Directory)
        readIfd((Directory) entry, in, offsetBase, result, metadata);
      else
      if (entry == MakerNote.MARKER) {
        String make = metadata.getStringValue("make");
        MakerNote makerNote = MakerNote.get(make);
        if (makerNote != null)
          readIfdInPlace(makerNote.getDirectory(), in, offsetBase, result, metadata);
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
