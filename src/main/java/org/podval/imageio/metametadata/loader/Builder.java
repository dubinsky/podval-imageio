/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.xml.sax.Attributes;


public abstract class Builder {

  protected Builder(Builder previous) {
    this.previous = previous;
  }


  public abstract Builder startElement(String name, Attributes attributes)
    throws MetaMetaDataException;


  public final Builder getPrevious() {
    return previous;
  }


  protected void check() throws MetaMetaDataException {
  }


  protected final MetaMetaData getMetaMetaData() {
    Builder candidate = this;
    while (!(candidate instanceof DocumentBuilder)) {
      candidate = candidate.previous;
    }
    return ((DocumentBuilder) candidate).metaMetaData;
  }


  protected final String getName(Attributes attributes) throws MetaMetaDataException {
    return getAttribute("name", attributes);
  }


  protected final String getAttribute(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    String result = attributes.getValue(name);

    if (result == null) {
      throw new MetaMetaDataException("Missing required attribute " + name);
    }

    return result;
  }


  protected final boolean getBooleanAttribute(String name, Attributes attributes) {
    return Boolean.valueOf(attributes.getValue(name));
  }


  protected final int getIntegerAttribute(String name, Attributes attributes)
    throws MetaMetaDataException
  {
    try {
      String value = attributes.getValue(name);
      return (value == null) ? 0 : Integer.valueOf(value);
    } catch (NumberFormatException e) {
      throw new MetaMetaDataException(e);
    }
  }


  protected static Type getType(Attributes attributes)
    throws MetaMetaDataException
  {
    Type result =  null;

    String typeName = attributes.getValue("type");

    if (typeName != null) {
      try {
        typeName = typeName.toUpperCase();
        result = Type.valueOf(typeName);
      } catch (IllegalArgumentException e) {
        throw new MetaMetaDataException("Unknown type " + typeName);
      }
    }

    return result;
  }


  private Builder previous;
}
