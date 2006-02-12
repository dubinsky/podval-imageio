/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;

import org.xml.sax.Attributes;


public final class DocumentBuilder extends Builder<MetaMetaData> {

  public DocumentBuilder(MetaMetaData metaMetaData) {
    super(null, metaMetaData);
  }


  public Builder<?> startElement(String name, Attributes attributes) {
    return ("meta-metadata".equals(name)) ? new RootBuilder(this, thing) : null;
  }
}
