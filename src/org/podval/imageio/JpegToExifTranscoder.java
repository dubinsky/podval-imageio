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
    Metadata result = null;
    /** @todo check that it is a JPEG metadata.... */
    Node root = metadata.getAsTree(metadata.getNativeMetadataFormatName());
    Node exifSegment = findExifSegment(root);

    if (exifSegment != null) {
      byte[] data = (byte[]) ((IIOMetadataNode) exifSegment).getUserObject();
      ImageInputStream in = ImageIO.createImageInputStream(
        new ByteArrayInputStream(data));
      result = ExifDecoder.read(in);
    }

    return result;
  }


  private static final String APP1 = "225";


  // Looking for: <unknown MarkerTag="225"/>
  // We assume that there is at most one 'unknown'.
  /** @todo We probably should not :( */
  private static Node findExifSegment(Node root) {
    Node result = findElement(root, "unknown");

    if (result != null) {
      String marker = result.getAttributes().getNamedItem("MarkerTag").getNodeValue();
      if (!marker.equals(APP1))
        result = null;
    }

    return result;
  }


  private static Node findElement(Node root, String name) {
    if (root.getNodeType() != Node.ELEMENT_NODE)
      return null;

    if (root.getNodeName().equals(name))
      return root;

    for (Node child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
      Node result = findElement(child, name);
      if (result != null)
        return result;
    }

    return null;
  }
}
