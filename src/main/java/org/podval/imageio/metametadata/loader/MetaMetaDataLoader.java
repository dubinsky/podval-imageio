/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.MetaMetaDataException;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;


public final class MetaMetaDataLoader {

  public MetaMetaDataLoader(MetaMetaData metaMetaData) {
    this.metaMetaData = metaMetaData;
  }


  public void loadResource(String resourceName)
    throws ParserConfigurationException, SAXException, IOException
  {
    InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (is == null) {
      throw new IOException("Can't find resource " + resourceName);
    }

    if (resourceName.endsWith(".list")) {
      loadList(is);

    } else {
      loadXml(is);
    }

    is.close();
  }


  private void loadList(InputStream is) throws ParserConfigurationException,
    SAXException, IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      line = line.trim();
      if ((line.length() > 0) && !line.startsWith("#")) {
        loadResource(line);
      }
    }
  }


  private void loadXml(InputStream is) throws ParserConfigurationException,
    SAXException, IOException
  {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

    parser.parse(is, new DefaultHandler() {

      public void startDocument() {
        currentBuilder = new DocumentBuilder(metaMetaData);
      }


      public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException
      {
        try {
          Builder<?> nextBuilder = currentBuilder.startElement(qName, attributes);
          if (nextBuilder == null) {
            throw new MetaMetaDataException("Unexpected element " + qName + " in " +
              currentBuilder);
          }
          currentBuilder = nextBuilder;
        } catch (MetaMetaDataException e) {
          throw new SAXException(calculateExceptionMessage(e));
        }
      }


      public void endElement(String uri, String localName, String qName)
        throws SAXException
      {
        try {
          currentBuilder.check();
          currentBuilder = currentBuilder.previous;
        } catch (MetaMetaDataException e) {
          throw new SAXException(calculateExceptionMessage(e));
        }
      }
    });
  }


  private String calculateExceptionMessage(MetaMetaDataException e) {
    String result = e.getMessage();

    Builder<?> context = currentBuilder;
    while (!(context instanceof RootBuilder)) {
      result = context + " / " + result;
      context = context.previous;
    }

    /** @todo print name of the resource being loaded! */

    return result;
  }

  private final MetaMetaData metaMetaData;


  private Builder<?> currentBuilder;
}
