/* $Id$ */

package org.podval.imageio;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;


public abstract class Builder {

  protected Builder(Builder previous) {
    this.previous = previous;
  }


  public abstract Builder startElement(String name, Attributes attributes) throws SAXException;


  public final Builder getPrevious() {
    return previous;
  }


  protected final Heap getHeap(Attributes attributes) throws SAXException {
    TypeNG type = getType(attributes, getMetaMetaData().getDefaultHeapType());
    return getMetaMetaData().getHeap(getName(attributes), type);
  }


  protected final Heap getMakerNote(Attributes attributes) throws SAXException {
    return getMetaMetaData().getMakerNote(
      getName(attributes),
      getAttribute("make", attributes),
      attributes.getValue("signature")
    );
  }


  protected final RecordNG getRecord(Attributes attributes) throws SAXException {
    String name = getName(attributes);
    RecordNG result = getMetaMetaData().getRecord(name);

    if (result == null) {
      TypeNG type = getType(attributes, getMetaMetaData().getDefaultRecordType());
      result = new RecordNG(name, type);
      getMetaMetaData().registerRecord(result);
    } else {
      /** @todo check that there are no spurious attributes */
    }

    addAttributes(result, attributes);

    return result;
  }


  protected final RecordNG getField(Attributes attributes, TypeNG defaultType)
    throws SAXException
  {
    String name = getName(attributes);
    TypeNG type = getType(attributes, defaultType);

    RecordNG result = new RecordNG(name, type);

    addAttributes(result, attributes);

    return result;
  }


  private void addAttributes(RecordNG record, Attributes attributes) {
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


  protected final TypeNG getType(Attributes attributes, TypeNG defaultType)
    throws SAXException
  {
    TypeNG result;

    String typeName = attributes.getValue("type");

    if (typeName == null) {
      result = defaultType;
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


  private Builder previous;
}
