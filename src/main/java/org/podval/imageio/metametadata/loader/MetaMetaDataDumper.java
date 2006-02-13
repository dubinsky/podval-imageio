/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.Key;
import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.Enumeration;
import org.podval.imageio.metametadata.EnumerationItem;
import org.podval.imageio.metametadata.MakerNoteMarker;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.podval.imageio.util.SaxDumper;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.OutputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.helpers.AttributesImpl;


public final class MetaMetaDataDumper extends SaxDumper {

/** @todo type names are dumped uppercase, but the loader accepts lowercase... */
/** @todo U32_OR_U16 records are dumped twice... */

  public static void dump(Heap heap, OutputStream os)
    throws
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    new MetaMetaDataDumper(heap).dump(os);
  }


  private MetaMetaDataDumper(Heap heap) {
    this.heap = heap;
  }


  protected void dump() {
    dumpHeap(heap, null);
  }


  private void dumpHeap(Heap heap, String tag) {
    startElement("directory", getEntryAttributes(heap, tag));

    for (Key key : heap.getKeys()) {
      Entry entry = heap.getEntry(key);
      String entryTag = Integer.toString(key.tag);

      if (entry instanceof Heap) {
        dumpHeap((Heap) entry, entryTag);
      } else
      if (entry instanceof Record) {
        dumpRecord((Record) entry, entryTag);
      } else
      if (entry instanceof MakerNoteMarker) {
        dumpMakerNoteMarker((MakerNoteMarker) entry, entryTag);
      } else {

      }
    }

    endElement("directory");
  }


  private void dumpRecord(Record record, String tag) {
    AttributesImpl attributes = getEntryAttributes(record, tag);

    if (record.hasDefaultField()) {
      try {
        Field defaultField = record.getDefaultField();
        dumpField("record", attributes, defaultField);
      } catch (MetaMetaDataException e) {
        throw new IllegalArgumentException(e);
      }
    } else {

      if (record.isVector()) {
        addNullableAttribute(attributes, "vector", "true");
        addIntegerAttribute(attributes, "count", record.getFields().size());
      }

      startElement("record", attributes);

      List<Field> fields = record.getFields();
      for (int index = (record.isVector()) ? 1 : 0; index < fields.size(); index++) {
        Field field = fields.get(index);
        if (field != null) {
          dumpField("field", getEntryAttributes(field, "index", Integer.toString(index)), field);
        }
      }

      endElement("record");
    }
  }


  private void dumpMakerNoteMarker(MakerNoteMarker marker, String tag) {
    AttributesImpl attributes = getEntryAttributes(marker, tag);

    startElement("makerNoteMarker", attributes);
    endElement("makerNoteMarker");
  }


  private void dumpField(String element, AttributesImpl attributes, Field field) {
    addNullableAttribute(attributes, "conversion", field.getConversion());

    startElement(element, attributes);

    dumpEnumeration(field);

    List<Field> subFields = field.getSubFields();
    if (subFields != null) {
      for (Field subField : subFields) {
        dumpField("field", getEntryAttributes(subField, null, null), subField);
      }
    }

    endElement(element);
  }


  private void dumpEnumeration(Field field) {
    Enumeration enumeration = field.getEnumeration();
    if (enumeration != null) {
      AttributesImpl attributes = new AttributesImpl();
      addNullableAttribute(attributes, "class", enumeration.getClassName());

      startElement("enumeration", attributes);
      dumpItems(enumeration);
      endElement("enumeration");
    }
  }


  private void dumpItems(Enumeration enumeration) {
    List<Integer> tags = new ArrayList<Integer>(enumeration.getTags());
    Collections.sort(tags);

    for (int tag : tags) {
      EnumerationItem item = enumeration.getItem(tag);

      AttributesImpl attributes = new AttributesImpl();

      addIntegerAttribute(attributes, "tag", tag);
      addNullableAttribute(attributes, "name", item.name);
      addNullableAttribute(attributes, "description", item.description);

      startElement("item", attributes);
      endElement("item");
    }
  }


  private AttributesImpl getEntryAttributes(Entry entry, String tag) {
    return getEntryAttributes(entry, "tag", tag);
  }


  private AttributesImpl getEntryAttributes(Entry entry, String tagName, String tag) {
    AttributesImpl attributes = new AttributesImpl();

    addNullableAttribute(attributes, tagName, tag);
    addNullableAttribute(attributes, "type", entry.getType());
    addNullableAttribute(attributes, "name", entry.getName());

    if (entry.isSkip()) {
      addNullableAttribute(attributes, "skip", "true");
    }

    return attributes;
  }


  private final Heap heap;
}
