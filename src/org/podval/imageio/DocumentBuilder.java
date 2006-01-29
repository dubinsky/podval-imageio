/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public class DocumentBuilder extends Builder {

  public DocumentBuilder(MetaMetaData metaMetaData) {
    super(null);
    this.metaMetaData = metaMetaData;
  }


  public Builder startElement(String name, Attributes attributes) {
    Builder result = null;

    if ("meta-metadata".equals(name)) {
      result = new RootBuilder(this);
    }

    return result;
  }


  public final MetaMetaData metaMetaData;
}
