/* $Id$ */

package org.podval.imageio;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public class SaxDumpingHandler extends SaxDumper implements ReaderHandler {

  public SaxDumpingHandler(Reader reader, ImageInputStream in, MetaMetaData metaMetaData) {
    this.in = in;
    this.reader = reader;
    this.metaMetaData = metaMetaData;
  }


  protected void read() throws IOException {
    reader.read(in, this, metaMetaData);
  }


  public boolean startHeap(int tag, Heap heap) {
    startFolder(tag, heap, "directory");
    return true;
  }


  public void endHeap() {
    endFolder("directory");
  }


  public boolean startRecord(int tag, RecordNG record) {
    startFolder(tag, record, "record");
    return true;
  }


  public void endRecord() {
    endFolder("record");
  }


  private void startFolder(int tag, Entry entry, String kind) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, entry);

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


  public void handleValue(int tag, Object value, TypeNG type, RecordNG record) {
    AttributesImpl attributes = createAttributes(tag, type, record);
    signal(attributes, value);
  }


  public void handleLongValue(int tag, int count, TypeNG type, RecordNG record, Reader reader) {
    AttributesImpl attributes = createAttributes(tag, type, record);
    if (count != 1) {
      addAttribute(attributes, "count", Integer.toString(count));
    }

    Object value = null;
    try {
      value = reader.readBytes(reader.getMaxCounter());
    } catch (IOException e) {
      System.out.println(e);
    }

    signal(attributes, value);
  }


  private AttributesImpl createAttributes(int tag, TypeNG type, RecordNG record) {
    AttributesImpl result = new AttributesImpl();

    addAttribute(result, "tag", Integer.toString(tag));
    addNameAttribute(result, record);
    addAttribute(result, "type", type.toString());

    return result;
  }


  private void signal(AttributesImpl attributes, Object value) {
    if (value != null) {
      addAttribute(attributes, "value", valueToString(value));
    }

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


  private static final String[] HEX = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };


  private static String toHex(byte b) {
    return HEX[(b & 0xF0) >> 4] + HEX[b & 0xF];
  }


  private void addNameAttribute(AttributesImpl attributes, Entry entry) {
    if (entry != null) {
      String name = entry.getName();
      if (name != null) {
        addAttribute(attributes, "name", name);
      }
    }
  }


  private void addAttribute(AttributesImpl attributes, String name, String value) {
    attributes.addAttribute(null, null, name, "string", value);
  }


  private final ImageInputStream in;


  private final Reader reader;


  private final MetaMetaData metaMetaData;
}
