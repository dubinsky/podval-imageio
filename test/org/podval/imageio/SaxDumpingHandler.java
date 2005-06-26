/* $Id$ */

package org.podval.imageio;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.IOException;


public class SaxDumpingHandler extends SaxDumper implements ReaderHandler {

  private static final int MAX_LENGTH = 64;


  public SaxDumpingHandler(Reader reader, MetaMetaDataNG metaMetaData) {
    this.reader = reader;
    this.metaMetaData = metaMetaData;
  }


  protected void read() throws IOException {
    reader.read(this, metaMetaData);
  }


  public boolean startHeap(int idCode, Heap heap) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(idCode));
      addNameAttribute(attributes, heap);

      contentHandler.startElement(null, null, "directory", attributes);
    } catch (SAXException e) {
    }
    return true;
  }


  public void endHeap() {
    try {
      contentHandler.endElement(null, "directory", "directory");
    } catch (SAXException e) {
    }
  }


  public void readRecord(int tag, TypeNG type, int length, int count, Reader reader, RecordNG record) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, record);
      addAttribute(attributes, "type", type.toString());

      Object value = null;
      try {
        value = (length <= MAX_LENGTH) ?
          reader.readValue(length, count, type) :
          reader.readBytes(MAX_LENGTH);
      } catch (IOException e) {
        System.out.println(e);
      }

      if ((type == TypeNG.STRING) || (type == TypeNG.STRUCTURE)) { /** @todo TypeNG.isFlexibleLength() */
        addAttribute(attributes, "length", Long.toString(length));
      }

      if ((count != 1) && (count != length)) {
        addAttribute(attributes, "count", Integer.toString(count));
      }

      addAttribute(attributes, "value", valueToString(value));

      contentHandler.startElement(null, "record", "record", attributes);
      contentHandler.endElement(null, "record", "record");
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


  private final Reader reader;


  private final MetaMetaDataNG metaMetaData;
}
