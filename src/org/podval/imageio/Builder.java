/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public abstract class Builder {

  protected Builder(Builder previous) {
    this.previous = previous;
  }


  public abstract Builder startElement(String name, Attributes attributes) throws SAXException;


  public final Builder endElement() {
    check();
    return previous;
  }


  protected void check() {
  }


  protected final Heap getHeap(Attributes attributes) throws SAXException {
    return getMetaMetaData().getHeap(
      getName(attributes),
      getType(attributes)
    );
  }


  protected final Heap getMakerNote(Attributes attributes) throws SAXException {
    return getMetaMetaData().getMakerNote(
      getName(attributes),
      getAttribute("make", attributes),
      attributes.getValue("signature")
    );
  }


  protected final MetaMetaData getMetaMetaData() {
    Builder candidate = this;
    while (!(candidate instanceof DocumentBuilder)) {
      candidate = candidate.previous;
    }
    return ((DocumentBuilder) candidate).metaMetaData;
  }


  protected final String getName(Attributes attributes) throws SAXException {
    return getAttribute("name", attributes);
  }


  private String getAttribute(String name, Attributes attributes)
    throws SAXException
  {
    String result = attributes.getValue(name);

    if (result == null) {
      throw new SAXException("Missing required attribute " + name);
    }

    return result;
  }


  protected final boolean getBooleanAttribute(String name, Attributes attributes) {
    return Boolean.valueOf(attributes.getValue(name));
  }


  protected final int getIntegerAttribute(String name, Attributes attributes)
    throws SAXException
  {
    try {
      String value = attributes.getValue(name);
      return (value == null) ? 0 : Integer.valueOf(value);
    } catch (NumberFormatException e) {
      throw new SAXException(e);
    }
  }


  protected final Type getType(Attributes attributes)
    throws SAXException
  {
    Type result =  null;

    String typeName = attributes.getValue("type");

    if (typeName != null) {
      try {
        /** @todo check that typeName is in lower case */
        typeName = typeName.toUpperCase();
        result = Type.valueOf(typeName);
      } catch (IllegalArgumentException e) {
        throw new SAXException("Unknown type " + typeName);
      }
    }

    return result;
  }


  private Builder previous;
}
