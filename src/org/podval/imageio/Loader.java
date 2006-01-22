/* $Id$ */

package org.podval.imageio;

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

import java.util.Map;
import java.util.HashMap;


public final class Loader {

  public static MetaMetaData get(String name)
    throws ParserConfigurationException, SAXException, IOException
  {
    MetaMetaData result = name2data.get(name);
    if (result == null) {
      result = load(name + ".list");
      name2data.put(name, result);
    }
    return result;
  }


  private static Map<String, MetaMetaData> name2data = new HashMap<String,MetaMetaData>();


  public static MetaMetaData load(String resourceName)
    throws ParserConfigurationException, SAXException, IOException
  {
    MetaMetaData result = new MetaMetaData();
    new Loader(result).loadResource(resourceName);
    return result;
  }


  private Loader(MetaMetaData metaMetaData) {
    this.metaMetaData = metaMetaData;
  }


  private void loadResource(String resourceName)
    throws ParserConfigurationException, SAXException, IOException
  {
    InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (is == null) {
      throw new IOException("Can't find resource " + resourceName);
    }

    if (resourceName.endsWith(".xml")) {
      loadXml(is);

    } else {
      loadList(is);
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
        currentBuilder = currentBuilder.endElement();
      }
    });
  }


  private final MetaMetaData metaMetaData;


  private Builder currentBuilder;
}
