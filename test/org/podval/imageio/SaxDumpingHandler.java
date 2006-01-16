/* $Id$ */

package org.podval.imageio;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;


public class SaxDumpingHandler extends SaxDumper implements ReaderHandler {

  private static final int MAX_LENGTH = 64;


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


  public void readRecord(int tag, TypeNG type, int length, int count, Reader reader, RecordNG record) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, record);
      addAttribute(attributes, "type", type.toString());

      if (type.isVariableLength) {
        addAttribute(attributes, "length", Long.toString(length));
      }

      if ((count != 1) && (count != length)) {
        addAttribute(attributes, "count", Integer.toString(count));
      }

      if ((type == TypeNG.X8) && (count != 1)) {

      } else {
        Object value = null;
        try {
          value = (length <= MAX_LENGTH) ?
            reader.readValue(length, count, type) :
            reader.readBytes(MAX_LENGTH);
        } catch (IOException e) {
          System.out.println(e);
        }

        addAttribute(attributes, "value", valueToString(value));
      }


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
