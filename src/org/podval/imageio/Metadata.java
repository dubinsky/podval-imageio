package org.podval.imageio;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import javax.imageio.stream.ImageInputStream;

import java.util.Iterator;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Node;

import com.sun.imageio.plugins.jpeg.JPEGMetadata;


public class Metadata extends IIOMetadata {

  public Metadata(String nativeMetadataFormatName) {
    super(false, nativeMetadataFormatName, null, null, null);
    root = new Group();
  }


  public boolean isReadOnly() {
    return true;
  }


  public void reset() {
    throw new IllegalStateException("This is a read-only metadata!");
  }


  public void mergeTree(String formatName, Node root) {
    throw new IllegalStateException("This is a read-only metadata!");
  }


  public Node getAsTree(String formatName) {
    if (formatName.equals(getNativeMetadataFormatName()))
      return getNativeTree();
    else
      throw new IllegalArgumentException("Not a recognized format " + formatName);
  }


  public Group getRoot() {
    return root;
  }


  public int getIntValue(String name) {
    int result = -1;
    Object value = find(name);
    if ((value != null) && (value instanceof Long)) {
      result = ((Long) value).intValue();
    }
    return result;
  }


  public String getStringValue(String name) {
    String result = null;
    Object value = find(name);
    if ((value != null) && (value instanceof String)) {
      result = (String) value;
    }
    return result;
  }


  public Object find(String name) {
    return root.find(name);
  }


  private Node getNativeTree() {
    return root.getNativeTree(getNativeMetadataFormatName());
  }


  private final Group root;




  public static Metadata read(File file) throws IOException {
    IIOMetadata result = null;

    ImageInputStream in = ImageIO.createImageInputStream(file);
    Iterator readers = ImageIO.getImageReaders(in);
    ImageReader reader = (readers.hasNext()) ? (ImageReader) readers.next() : null;

    if (reader != null) {
      reader.setInput(in);
      result = read(reader);
      reader.dispose();
    }

    in.close();

    return (Metadata) result;
  }


  public static Metadata read(ImageReader reader) throws IOException {
    IIOMetadata result = reader.getImageMetadata(0);

    /** @todo this should be done through a transcoder? */
    if (result instanceof JPEGMetadata)
      result = ExifReader.transcodeJpegMetadata(result);

    return (Metadata) result;
  }


  public void dump() throws
    javax.xml.transform.TransformerFactoryConfigurationError,
    IllegalArgumentException,
    javax.xml.transform.TransformerException
  {
    Node tree = getNativeTree();
    javax.xml.transform.Transformer transformer =
      javax.xml.transform.TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty("indent", "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(
      new javax.xml.transform.dom.DOMSource(tree),
      new javax.xml.transform.stream.StreamResult(System.out)
    );
  }
}
