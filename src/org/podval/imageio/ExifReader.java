package org.podval.imageio;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.nio.ByteOrder;

import org.w3c.dom.Node;


/*
  It is possible to use TIFFImageMetadata from the new jai_imageio package from Sun.
  But:
  1) It is not aware of all the EXIF 2.2 tags, I do not think.
  2) It is not easily expandable through xml files.
  3) There are mistakes in its tag definitions, for instance:
     a) Tag 0x0112 (274) 'Orientation' is missing value '8' (Left-Bottom).
     b) Tag 0x927C (37500) 'MakerNote' is incorrectly named 'MarkerNote'.
     c) In BaseLine..., tags 0x0201 and 0x0202 (JpegIFOffset/JpegIFByteCount) are missing?
  4) It does not read interoperability IFDs.
  5) It does not decode maker note.
*/


public class ExifReader {

  public static Metadata transcodeJpegMetadata(IIOMetadata metadata) throws IOException {
    /** @todo check that it is a JPEG metadata.... */
    IIOMetadataNode exifSegment = (IIOMetadataNode) findExifSegment(
      metadata.getAsTree(metadata.getNativeMetadataFormatName()));

    return (exifSegment == null) ? null :
      readExifStream(ImageIO.createImageInputStream(
        new ByteArrayInputStream((byte[]) exifSegment.getUserObject())));
  }


  // Looking for: <unknown MarkerTag="225"/> (APP1 marker)
  private static Node findExifSegment(Node root) {
    if (root.getNodeType() != Node.ELEMENT_NODE)
      return null;

    if (root.getNodeName().equals("unknown") &&
        root.getAttributes().getNamedItem("MarkerTag").getNodeValue().equals("225"))
      return root;

    for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
      Node result = findExifSegment(child);
      if (result != null)
        return result;
    }

    return null;
  }


  public static Metadata readJpegStream(ImageInputStream in) throws IOException {
    /* Start Of Image marker. */
    if (readMarker(in) != 0xD8)
      throw new IOException("Bad JPEG signature.");

    return (findExifMarker(in)) ? readExifStream(in) : null;
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


  public static final String NATIVE_FORMAT_NAME = "org_podval_imageio_exif_1.0";


  private static Metadata readExifStream(ImageInputStream in) throws IOException {
    DefaultMetadataHandler handler = new DefaultMetadataHandler(NATIVE_FORMAT_NAME);
    ExifDecoder.read(in, NATIVE_FORMAT_NAME, handler);
    return handler.getResult();
  }
}