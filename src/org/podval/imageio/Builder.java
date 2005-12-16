/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public abstract class Builder {

  protected Builder(Builder previous) {
    this.previous = previous;
  }


  public abstract Builder startElement(String name, Attributes attributes) throws SAXException;


  protected final Heap getHeap(Attributes attributes) throws SAXException {
    return getMetaMetaData().getHeap(getName(attributes), getType(attributes));
  }


  protected final RecordNG getRecord(Attributes attributes) throws SAXException {
    String name = getName(attributes);
    RecordNG result = getMetaMetaData().getRecord(name);
    if (result == null) {
      TypeNG type = getType(attributes);
      boolean isVector = getBooleanAttribute("vector", attributes);
      boolean skip = getBooleanAttribute("skip", attributes);
      /** @todo resolve conversion */
      String conversion = attributes.getValue("conversion");
      /** @todo handlers? */
//      ... getAttribute("handler", attributes);
      /** @todo handle tags the same way... */
//      ... getIntegerAttribute("count", attributes);
      result = new RecordNG(name, type, isVector, skip, conversion);
      getMetaMetaData().registerRecord(result);
    } else {
      /** @todo check that there are no spurious attributes */
    }

    return result;
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


  protected final String getAttribute(String name, Attributes attributes)
    throws SAXException
  {
    String result = attributes.getValue(name);

    if (result == null) {
      throw new SAXException();
    }

    return result;
  }


  protected final boolean getBooleanAttribute(String name, Attributes attributes) {
    return Boolean.valueOf(attributes.getValue(name));
  }


  protected final TypeNG getType(Attributes attributes) throws SAXException {
    TypeNG result;

    String typeName = attributes.getValue("type");

    if (typeName == null) {
      result = null; /** @todo default? exception? */
    } else {
      try {
        /** @todo check that typeName is in lower case */
        typeName = typeName.toUpperCase();
        result = TypeNG.valueOf(typeName);
      } catch (IllegalArgumentException e) {
        throw new SAXException("Unknown type " + typeName);
      }
    }

    return result;
  }


  public final Builder previous;
}
