package org.podval.imageio;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import java.io.IOException;

import java.io.ByteArrayInputStream;

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

public class JpegToExifTranscoder {

  public static Metadata read(IIOMetadata metadata) throws IOException {
    /** @todo check that it is a JPEG metadata.... */
    IIOMetadataNode exifSegment = (IIOMetadataNode) findExifSegment(
      metadata.getAsTree(metadata.getNativeMetadataFormatName()));

    return (exifSegment == null) ? null :
      ExifDecoder.read(ImageIO.createImageInputStream(
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
}
