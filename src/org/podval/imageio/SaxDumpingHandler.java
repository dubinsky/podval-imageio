/* $Id$ */

package org.podval.imageio;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

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


  public boolean startFolder(int tag, String name) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, name);
      contentHandler.startElement(null, null, "folder", attributes);
    } catch (SAXException e) {
    }
    return true;
  }


  public void endFolder() {
    try {
      contentHandler.endElement(null, null, "folder");
    } catch (SAXException e) {
    }
  }


  public ValueAction atValue(int tag, String name, Type type, int count)
    throws IOException
  {
    return (count <= MAX_COUNT) ? ValueAction.VALUE : ValueAction.RAW;
  }


  public void handleValue(int tag, String name, Type type, int count, Object value) {
    if ("make".equals(name) && (type == Type.STRING)) {
      make = (String) value;
    }

    handleRecord(tag, name, type, count, value);
  }


  public void handleRawValue(int tag, String name, Type type, int count, ImageInputStream is)
    throws IOException
  {
    byte[] value = new byte[MAX_COUNT];
    in.readFully(value);

    handleRecord(tag, name, type, count, value);
  }


  private void handleRecord(int tag, String name, Type type, int count, Object value) {
    AttributesImpl attributes = new AttributesImpl();

    addAttribute(attributes, "tag", Integer.toString(tag));

    addNameAttribute(attributes, name);

    addAttribute(attributes, "type", type.toString());

    if (count != 1) {
      addAttribute(attributes, "count", Integer.toString(count));
    }

    /** @todo vector... */

    if (value != null) {
      addAttribute(attributes, "value", valueToString(value));
    }

    try {
      contentHandler.startElement(null, null, "item", attributes);
      contentHandler.endElement(null, null, "item");
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
