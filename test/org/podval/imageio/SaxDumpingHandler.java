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
      attributes.addAttribute(null, "tag", "tag", "string", Integer.toString(idCode));
      if (heap != null) {
        String name = heap.getName();
        if (name != null) {
          attributes.addAttribute(null, null, "name", "string", name);
        }
      }
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


  public void readRecord(int tag, TypeNG type, long length, int count, Reader reader) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(null, "tag", "tag", "string", Integer.toString(tag));
      attributes.addAttribute(null, "type", "type", "string", type.toString());
      if (count != 1) {
        attributes.addAttribute(null, "count", "count", "string", Integer.toString(count));
      }
      attributes.addAttribute(null, "length", "length", "string", Long.toString(length));
      contentHandler.startElement(null, "record", "record", attributes);
      contentHandler.endElement(null, "record", "record");
    } catch (SAXException e) {
    }
  }


  private final Reader reader;


  private final MetaMetaDataNG metaMetaData;
}
