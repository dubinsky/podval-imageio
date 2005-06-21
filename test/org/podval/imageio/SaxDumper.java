/* $Id$ */

package org.podval.imageio;

import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXNotRecognizedException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


public abstract class SaxDumper implements XMLReader {

  public boolean getFeature(String name) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public void setFeature(String name, boolean value) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public Object getProperty(String name) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public void setProperty(String name, Object value) throws SAXNotRecognizedException {
    throw new SAXNotRecognizedException();
  }



  public void setEntityResolver(EntityResolver resolver) {
  }



  public EntityResolver getEntityResolver() {
    return null;
  }



  public void setDTDHandler(DTDHandler handler) {
  }



  public DTDHandler getDTDHandler() {
    return null;
  }



  public void setContentHandler(ContentHandler handler) {
    this.contentHandler = handler;
  }



  public ContentHandler getContentHandler() {
    return contentHandler;
  }



  public void setErrorHandler(ErrorHandler handler) {
  }



  public ErrorHandler getErrorHandler() {
    return null;
  }



  public void parse(String systemId) {
    throw new UnsupportedOperationException();
  }


  public void parse(InputSource input) throws IOException, SAXException {
    contentHandler.startDocument();
    read();
    contentHandler.endDocument();
  }


  protected abstract void read() throws IOException;


  protected ContentHandler contentHandler;
}
