/* $Id$ */

package org.podval.imageio;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.SAXException;

import java.io.OutputStreamWriter;
import java.io.IOException;


public class SaxDumpingHandler extends SaxDumper implements ReaderHandler {

  public static void dump(Reader reader, MetaMetaDataNG metaMetaData) throws
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute("indent-number", 2);
    Transformer transformer = transformerFactory.newTransformer();

    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//    transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

      transformer.transform(
        new SAXSource(new SaxDumpingHandler(reader, metaMetaData), null),
        new StreamResult(new OutputStreamWriter(System.out))
      );
  }


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


  public void readRecord(int tag, TypeNG type, long length, int count, Reader reader, RecordNG record) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      addAttribute(attributes, "tag", Integer.toString(tag));
      addNameAttribute(attributes, record);
      addAttribute(attributes, "type", type.toString());
      if ((count != 1) && (count != length)) {
        addAttribute(attributes, "count", Integer.toString(count));
      }
      addAttribute(attributes, "length", Long.toString(length));

      if (type == TypeNG.STRING) {
        try {
          addAttribute(attributes, "value", reader.getValue(type, length, count).toString());
        } catch (IOException e) {
        }
      }

      contentHandler.startElement(null, "record", "record", attributes);
      contentHandler.endElement(null, "record", "record");
    } catch (SAXException e) {
    }
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
