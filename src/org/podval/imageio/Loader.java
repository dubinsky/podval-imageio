/* $Id$ */

package org.podval.imageio;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


public class Loader {

  public static void load(String path, MetaMetaDataNG metaMetaData)
    throws ParserConfigurationException, SAXException
  {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
///    parser.parse(new File(path), new DefaultHadnler());
  }
}
