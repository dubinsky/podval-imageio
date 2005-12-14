/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class DocumentBuilder extends Builder {

  public DocumentBuilder(MetaMetaData metaMetaData) {
    super(null);
    this.metaMetaData = metaMetaData;
  }


  public Builder startElement(String name, Attributes attributes)
    throws SAXException
  {
    if (!"meta-metadata".equals(name)) {
      throw new SAXException();
    }

    return new RootBuilder(this);
  }


  public final MetaMetaData metaMetaData;
}
