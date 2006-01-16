/* $Id$ */

package org.podval.imageio;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;


public final class Loader {

  public static void load(String path, MetaMetaData metaMetaData)
    throws ParserConfigurationException, SAXException, IOException
  {
    new Loader(metaMetaData).load(path);
  }


  private Loader(MetaMetaData metaMetaData) {
    this.metaMetaData = metaMetaData;
  }


  private void load(String path) throws ParserConfigurationException,
    SAXException, IOException
  {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    parser.parse(new File(path), new DefaultHandler() {

      public void startDocument() throws SAXException {
        currentBuilder = new DocumentBuilder(metaMetaData);
      }


      public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
      {
        Builder nextBuilder = currentBuilder.startElement(qName, attributes);
        if (nextBuilder == null) {
          throw new SAXException("Unexpected element " + qName + " in " + currentBuilder);
        }
        currentBuilder = nextBuilder;
      }


      public void endElement(String uri, String localName, String qName)
        throws SAXException
      {
        /** @todo currentBuilder.check() ? */
        currentBuilder = currentBuilder.getPrevious();
      }
    });
  }


  private final MetaMetaData metaMetaData;


  private Builder currentBuilder;
}
