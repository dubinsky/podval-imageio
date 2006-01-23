/* $Id$ */

package org.podval.imageio;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;


public class SaxDumpingHandler extends SaxDumper implements ReaderHandler {

  private static final int MAX_COUNT = 64;


  public static void dump(Reader reader, ImageInputStream in, MetaMetaData metaMetaData, OutputStream os)
    throws
    IOException,
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    new SaxDumpingHandler(reader, in, metaMetaData).dump(os);
  }


  private SaxDumpingHandler(Reader reader, ImageInputStream in, MetaMetaData metaMetaData) {
    this.in = in;
    this.reader = reader;
    this.metaMetaData = metaMetaData;
  }


  protected void read() throws IOException {
    reader.read(in, this, metaMetaData);
  }


  public boolean startHeap(int tag, String name) {
    startFolder(tag, name, "directory");
    return true;
  }


  public void endHeap() {
    endFolder("directory");
  }


  public boolean startRecord(int tag, String name) {
    startFolder(tag, name, "record");
    return true;
  }


  public void endRecord() {
    endFolder("record");
  }


  private void startFolder(int tag, String name, String kind) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, name);
      contentHandler.startElement(null, null, kind, attributes);
    } catch (SAXException e) {
    }
  }


  private void endFolder(String kind) {
    try {
      contentHandler.endElement(null, null, kind);
    } catch (SAXException e) {
    }
  }


  public ValueAction atValue(int tag, String name, TypeNG type, int count)
    throws IOException
  {
    return (count <= MAX_COUNT) ? ValueAction.VALUE : ValueAction.RAW;
  }


  public void handleValue(int tag, String name, TypeNG type, int count, Object value) {
    if ("make".equals(name) && (type == TypeNG.STRING)) {
      make = (String) value;
    }
    handleRecord(getAttributes(tag, name, type, count, value));
  }


  public void handleRawValue(int tag, String name, TypeNG type, int count, ImageInputStream is)
    throws IOException
  {
    byte[] value = new byte[MAX_COUNT];
    long offset = is.getStreamPosition();
    in.readFully(value);

    AttributesImpl attributes = getAttributes(tag, name, type, count, value);
    addAttribute(attributes, "offset", Long.toString(offset));
    handleRecord(attributes);
  }


  private AttributesImpl getAttributes(int tag, String name, TypeNG type, int count, Object value) {
    AttributesImpl result = new AttributesImpl();

    addAttribute(result, "tag", Integer.toString(tag));

    addNameAttribute(result, name);

    addAttribute(result, "type", type.toString());

    if (count != 1) {
      addAttribute(result, "count", Integer.toString(count));
    }

    /** @todo vector... */

    if (value != null) {
      addAttribute(result, "value", valueToString(value));
    }

    return result;
  }


  private void handleRecord(AttributesImpl attributes) {
    try {
      contentHandler.startElement(null, null, "record", attributes);
      contentHandler.endElement(null, null, "record");
    } catch (SAXException e) {
    }
  }


  private static String valueToString(Object value) {
    String result = "";

    if (value instanceof Object[]) {
      boolean first = true;
      for (Object o : (Object[]) value) {
        if (!first) {
          result += " ";
        }
        result += valueToString(o);
        first = false;
      }
    } else

    if (value instanceof byte[]) {
      boolean first = true;
      for (byte b : (byte[]) value) {
        if (!first) {
          result += " ";
        }
        result += toHex(b);
        first = false;
      }
    } else {

      result = value.toString();
    }

    return result;
  }


  public String getMake() {
    return make;
//    return null;
  }


  private static final String[] HEX = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };


  private static String toHex(byte b) {
    return HEX[(b & 0xF0) >> 4] + HEX[b & 0xF];
  }


  private void addNameAttribute(AttributesImpl attributes, String name) {
    if (name != null) {
      addAttribute(attributes, "name", name);
    }
  }


  private void addAttribute(AttributesImpl attributes, String name, String value) {
    attributes.addAttribute(null, null, name, "string", value);
  }


  private final ImageInputStream in;


  private final Reader reader;


  private final MetaMetaData metaMetaData;


  private String make;
}
