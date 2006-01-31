/* $Id$ */

package org.podval.imageio.util;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.AttributesImpl;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;


public abstract class SaxDumper implements XMLReader {

  public final void dump(OutputStream os) throws
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
        new SAXSource(this, null),
        new StreamResult(new OutputStreamWriter(os))
      );
  }


  public final boolean getFeature(String name) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public final void setFeature(String name, boolean value) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public final Object getProperty(String name) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public final void setProperty(String name, Object value) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public final void setEntityResolver(EntityResolver resolver) {
  }



  public final EntityResolver getEntityResolver() {
    return null;
  }



  public final void setDTDHandler(DTDHandler handler) {
  }



  public final DTDHandler getDTDHandler() {
    return null;
  }



  public final void setContentHandler(ContentHandler handler) {
    this.contentHandler = handler;
  }



  public final ContentHandler getContentHandler() {
    return contentHandler;
  }



  public final void setErrorHandler(ErrorHandler handler) {
  }



  public final ErrorHandler getErrorHandler() {
    return null;
  }



  public final void parse(String systemId) {
    throw new UnsupportedOperationException();
  }


  public final void parse(InputSource input) throws IOException, SAXException {
    contentHandler.startDocument();
    dump();
    contentHandler.endDocument();
  }


  protected abstract void dump() throws IOException;


  protected final void addNameAttribute(AttributesImpl attributes, String name) {
    if (name != null) {
      addAttribute(attributes, "name", name);
    }
  }


  protected final void addAttribute(AttributesImpl attributes, String name, String value) {
    attributes.addAttribute(null, null, name, "string", value);
  }


  protected final void startElement(String name, AttributesImpl attributes) {
    try {
      contentHandler.startElement(null, null, name, attributes);
    } catch (SAXException e) {
    }
  }


  protected final void endElement(String name) {
    try {
      contentHandler.endElement(null, null, name);
    } catch (SAXException e) {
    }
  }


  protected ContentHandler contentHandler;
}
