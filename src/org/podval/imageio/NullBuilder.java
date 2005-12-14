/* $Id$ */

package org.podval.imageio;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class NullBuilder extends Builder {

  public NullBuilder(Builder previous) throws SAXException {
    super(previous);
  }

  public Builder startElement(String name,
    Attributes attributes) throws SAXException {
    return new NullBuilder(this);
  }
}
