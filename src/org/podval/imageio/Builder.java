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


  protected final Record getRecord(Attributes attributes) throws SAXException {
    Record result = getMetaMetaData().getRecord(
      getName(attributes),
      getType(attributes)
    );
    addAttributes(result, attributes);
    return result;
  }


  protected final Record getField(Attributes attributes, Type defaultType)
    throws SAXException
  {
    String name = getName(attributes);
    Type type = getType(attributes);

    if (type == null) {
      type = defaultType;
    }

    Record result = new Record(name, type);

    addAttributes(result, attributes);

    return result;
  }


  private void addAttributes(Record record, Attributes attributes) {
    record.addIsVector(getBooleanAttribute("vector", attributes));
    record.addSkip(getBooleanAttribute("skip", attributes));
    /** @todo resolve conversion */
    record.addConversion(attributes.getValue("conversion"));

    /** @todo handlers? */
//      ... getAttribute("handler", attributes);
    /** @todo handle tags the same way... */
//      ... getIntegerAttribute("count", attributes);
  }


  protected final Enumeration getEnumeration(Attributes attributes) {
    return new Enumeration(null /** @todo class!!! */);
  }


  private MetaMetaData getMetaMetaData() {
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


  private boolean getBooleanAttribute(String name, Attributes attributes) {
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
