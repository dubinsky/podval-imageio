/* $Id$ */

package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;

import javax.xml.stream.XMLStreamWriter;

import javax.imageio.stream.ImageInputStream;

import java.io.OutputStream;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;


public class SaxDumpingHandler {

  private static final int MAX_COUNT = 64;


  public static void dump(Reader reader, ImageInputStream in, MetaMetaData metaMetaData, OutputStream os)
      throws IOException, XMLStreamException
  {
    new SaxDumpingHandler(reader, in, metaMetaData, os).dump();
  }


  private SaxDumpingHandler(Reader reader, ImageInputStream in, MetaMetaData metaMetaData, OutputStream os) {
    this.in = in;
    this.reader = reader;
    this.metaMetaData = metaMetaData;
    this.out = null; // @todo XMLStreamWriter around os...
  }


  protected void dump() throws IOException {
/////    reader.read(in, this, metaMetaData);
  }


  public boolean startFolder(int tag, String name) throws XMLStreamException {
    out.writeStartElement("folder");
    dumpIntegerAttribute("tag", tag);
    dumpNullableAttribute("name", name);
    return true;
  }


  public void endFolder() throws XMLStreamException {
    out.writeEndElement();
  }


/////  public ValueAction atValue(int tag, String name, int count) {
/////    return (count <= MAX_COUNT) ? ValueAction.VALUE : ValueAction.RAW;
/////  }


  public void handleValue(int tag, String name, int count, Object value) throws XMLStreamException {
    handleItem(tag, name, count, value);
  }


  public void handleRawValue(int tag, String name, int count, ImageInputStream is)
    throws IOException, XMLStreamException
  {
    byte[] value = new byte[MAX_COUNT];
    in.readFully(value);

    handleItem(tag, name, count, value);
  }


  private void handleItem(int tag, String name, int count, Object value) throws XMLStreamException {
    out.writeStartElement("item");

    dumpIntegerAttribute("tag", tag);
    dumpNullableAttribute("name", name);

    if (count != 1) {
      dumpIntegerAttribute("count", count);
    }

    if (value != null) {
      dumpNullableAttribute("value", valueToString(value));
    }

    out.writeEndElement();
  }


    private final void dumpNullableAttribute(final String name, final Object value) throws XMLStreamException {
        if (value != null) {
            out.writeAttribute(name, value.toString());
        }
    }


    private final void dumpIntegerAttribute(final String name, final int value) throws XMLStreamException {
        dumpNullableAttribute(name, Integer.toString(value));
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


  private final ImageInputStream in;


  private final Reader reader;


  private final MetaMetaData metaMetaData;


  private final XMLStreamWriter out;
}
