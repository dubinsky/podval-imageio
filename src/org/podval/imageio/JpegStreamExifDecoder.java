package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;

import java.io.IOException;


public class JpegStreamExifDecoder {

  public static Metadata read(ImageInputStream in) throws IOException {
    /* Start Of Image marker. */
    if (readMarker(in) != 0xD8)
      throw new IOException("Bad JPEG signature.");

    return (findExifMarker(in)) ? ExifReader.read(in) : null;
  }


  private static int readMarker(ImageInputStream in) throws IOException {
    // Can there be padding that I need to skip here?

    if (in.readUnsignedByte() != 0xFF)
      throw new IllegalArgumentException("Section does not begin with a marker.");

    return in.readUnsignedByte();
  }


  private static boolean findExifMarker(ImageInputStream in) throws IOException {
    boolean found = false;

    while (true) {
      int marker = readMarker(in);

      /* Start Of Scan (data) or End Of Image (stream). */
      if ((marker == 0xDA) || (marker == 0xD9))
        break;

      /* EXIF (APP1) */
      if (marker == 0xE1) {
        found = true;
        break;
      }

      long sectionStart = in.getStreamPosition();
      int sectionLength = readSectionLength(in);

      in.seek(sectionStart+2+sectionLength);
    }

    return found;
  }


  private static int readSectionLength(ImageInputStream in) throws IOException {
    ByteOrder byteOrder = in.getByteOrder();
    in.setByteOrder(ByteOrder.BIG_ENDIAN);
    int result = in.readUnsignedShort();
    in.setByteOrder(byteOrder);

    /* Length includes first two bytes representing it. */
    result -= 2;

    if (result < 0)
      throw new IOException("Section length too small.");

    return result;
  }
}
