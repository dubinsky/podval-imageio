/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;

import java.io.File;
import java.io.IOException;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.EntityResolver;
import org.xml.sax.DTDHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
//import org.w3c.dom.Node;


public class Test {

  private static class SaxDumper implements XMLReader, ReaderHandler {

    public SaxDumper(Reader reader) {
      this.reader = reader;
    }


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


    public void parse(String systemId) throws IOException, SAXException {
      throw new UnsupportedOperationException();
    }


    public void parse(InputSource input) throws IOException, SAXException {
      contentHandler.startDocument();
      reader.read(this);
      contentHandler.endDocument();
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

    }


    private final Reader reader;


    private ContentHandler contentHandler;
  }



  public static void dumpXml(Reader reader) throws
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    Transformer transformer =
      TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty("indent", "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(
        new SAXSource(new SaxDumper(reader), null),
        new StreamResult(System.out)
      );
  }


  private static class DumpingHandler implements ReaderHandler {


    public boolean startHeap(int idCode) {
      indent();
      System.out.println("Heap: " + idCode);
      level++;
      return true;
    }


    public void endHeap() {
      level--;
    }


    public void readRecord(Reader reader) {
      indent();
      System.out.println("Record: " + reader.getDataTag() + " :: "+ reader.getDataType() + "[" + reader.getDataCount() + "] (=" + reader.getDataLength() + ")");
    }


    private void indent() {
      for (int i = 0; i<level; i++) {
        System.out.print("  ");
      }
    }


    private int level = 0;
  }



  private static void dumpCiff(String path) throws Exception {
    File file = new File(path);
//    new CiffReader(new FileImageInputStream(file)).read(new DumpingHandler());
    dumpXml(new CiffReader(new FileImageInputStream(file)));
  }


  private static void extractCrwThumbnails(String path) throws IOException {
    File file = new File(path);

    CrwThumbnailExtractor.extractBig(file,
      new File("/tmp/" + file.getName() + ".big.jpeg"));

    CrwThumbnailExtractor.extractSmall(file,
      new File("/tmp/" + file.getName() + ".small.jpeg"));
  }


  public static void dumpExif(String path) throws IOException {
    File file = new File(path);

    ImageInputStream in = new FileImageInputStream(file);
    in = ExifStream.fromJpegStream(in);
    new ExifReader(in).read(new DumpingHandler());
  }


  public static void main(String[] args) throws Exception {
    dumpCiff("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
//    extractCrwThumbnails("/mnt/extra/Photo/ORIGINALS/g2/2/2/2250.crw");
//    dumpExif("/mnt/extra/Photo/ORIGINALS/g2/2/2/2249.jpg");
  }
}
