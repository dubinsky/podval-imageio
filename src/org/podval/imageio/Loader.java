/* $Id$ */

package org.podval.imageio;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;


public class Loader {

  public static void load(String path, MetaMetaDataNG metaMetaData)
    throws ParserConfigurationException, SAXException, IOException
  {
    new Loader(metaMetaData).load(path);
  }


  private Loader(MetaMetaDataNG metaMetaData) {
    this.metaMetaData = metaMetaData;
  }


  private void load(String path) throws ParserConfigurationException,
    SAXException, IOException {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    parser.parse(new File(path), new DefaultHandler() {

      public void startDocument() throws SAXException {
        currentBuilder = new DocumentBuilder(metaMetaData);
      }


      public void startElement(String uri, String localName, String qName,
        Attributes attributes) throws SAXException {
        currentBuilder = currentBuilder.startElement(qName, attributes);
      }


      public void endElement(String uri, String localName,
        String qName) throws SAXException {
        currentBuilder = currentBuilder.previous;
      }
    });
  }


  private final MetaMetaDataNG metaMetaData;


  private Builder currentBuilder;



  /**
   */
  private static abstract class Builder {

    protected Builder(Builder previous) {
      this.previous = previous;
    }


    public abstract Builder startElement(String name,
      Attributes attributes) throws SAXException;


    protected final Heap getHeap(Attributes attributes) throws SAXException {
      return getMetaMetaData().getHeap(getName(attributes), getType(attributes));
    }


    private MetaMetaDataNG getMetaMetaData() {
      Builder candidate = this;
      while (!(candidate instanceof DocumentBuilder)) {
        candidate = candidate.previous;
      }
      return ((DocumentBuilder) candidate).metaMetaData;
    }


    protected final String getName(Attributes attributes) throws SAXException {
      String result = attributes.getValue("name");
      if (result == null) {
        throw new SAXException();
      }
      return result;
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



  /**
   */
  private static class DocumentBuilder extends Builder {

    public DocumentBuilder(MetaMetaDataNG metaMetaData) {
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


    private final MetaMetaDataNG metaMetaData;
  }



  /**
   */
  private static class RootBuilder extends Builder {

    public RootBuilder(DocumentBuilder previous) {
      super(previous);
    }


    public Builder startElement(String name, Attributes attributes)
      throws SAXException
    {
      if (!"directory".equals(name)) {
        throw new SAXException();
      }

      return new HeapBuilder(this, getHeap(attributes));
    }
  }



  /**
   */
  private static class HeapBuilder extends Builder {

    public HeapBuilder(Builder previous, Heap heap) {
      super(previous);
      this.heap = heap;
    }


    public Builder startElement(String name, Attributes attributes)
      throws SAXException
    {
      Builder result;

      int tag;
      try {
        tag = Integer.valueOf(attributes.getValue("tag"));
      } catch (NumberFormatException e) {
        throw new SAXException(e);
      }

      Entry entry;
      if ("directory".equals(name)) {
        Heap heap = getHeap(attributes);
        entry = heap;
        result = new HeapBuilder(this, heap);
      } else

      if ("record".equals(name)) {
        RecordNG record = new RecordNG(getName(attributes), getType(attributes));
        entry = record;
        result = new RecordBuilder(this, record);
      } else {

        throw new SAXException();
      }

      heap.addEntry(tag, entry);

      return result;
    }


    private final Heap heap;
  }



  /**
   */
  private static class RecordBuilder extends Builder {

    public RecordBuilder(HeapBuilder previous, RecordNG record) {
      super(previous);
      this.record = record;
    }


    public Builder startElement(String name, Attributes attributes)
      throws SAXException
    {
      if ("field".equals(name)) {
        /** @todo  */
        return new NullBuilder(this);
      } else

      if ("enumeration".equals(name)) {
        /** @todo  */
        return new NullBuilder(this);
      } else {

        throw new SAXException();
      }
    }


    private final RecordNG record;
  }



  /**
   */
  private static class NullBuilder extends Builder {

    public NullBuilder(Builder previous) throws SAXException {
      super(previous);
    }

    public Builder startElement(String name,
      Attributes attributes) throws SAXException {
      return new NullBuilder(this);
    }
  }
}
