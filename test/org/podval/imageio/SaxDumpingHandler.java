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

  public static void dump(Reader reader) throws
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute("indent-number", new Integer(2));
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//    transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
      transformer.transform(
        new SAXSource(new SaxDumpingHandler(reader), null),
        new StreamResult(new OutputStreamWriter(System.out))
      );
  }


  public SaxDumpingHandler(Reader reader) {
    this.reader = reader;
  }


  protected void read() throws IOException {
    reader.read(this);
  }


  public boolean startHeap(int idCode) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(null, "tag", "tag", "string", Integer.toString(idCode));
      contentHandler.startElement(null, "directory", "directory", attributes);
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


  public void readRecord(Reader reader) {
    try {
      AttributesImpl attributes = new AttributesImpl();
      attributes.addAttribute(null, "tag", "tag", "string", Integer.toString(reader.getDataTag()));
      attributes.addAttribute(null, "type", "type", "string", reader.getDataType().toString());
      int count = reader.getDataCount();
      if (count != 1) {
        attributes.addAttribute(null, "count", "count", "string", Integer.toString(count));
      }
      attributes.addAttribute(null, "length", "length", "string", Long.toString(reader.getDataLength()));
      contentHandler.startElement(null, "record", "record", attributes);
      contentHandler.endElement(null, "record", "record");
    } catch (SAXException e) {
    }
  }


  private final Reader reader;
}
