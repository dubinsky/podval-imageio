/* $Id$ */

package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.MakerNoteMarker;

import org.podval.imageio.util.SaxDumper;

import java.util.List;

import java.io.OutputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.helpers.AttributesImpl;


public class MetaMetaDataDumper extends SaxDumper {

  public static void dump(MetaMetaData metaMetaData, OutputStream os)
    throws
    TransformerFactoryConfigurationError,
    TransformerException,
    IllegalArgumentException
  {
    new MetaMetaDataDumper(metaMetaData).dump(os);
  }


  private MetaMetaDataDumper(MetaMetaData metaMetadata) {
    this.metaMetadata = metaMetadata;
  }


  protected void dump() {
    dumpHeap(metaMetadata.getRootHeap(), null);
  }


  private void dumpHeap(Heap heap, String tag) {
    startElement("directory", getEntryAttributes(heap, tag));

    for (Heap.Key key : heap.getKeys()) {
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

    /** @todo count */

    if (record.isVector()) {
      addAttribute(attributes, "vector", "true");
    }

    startElement("record", attributes);

    List<Field> fields = record.getFields();
    for (int index = (record.isVector()) ? 1 : 0; index<fields.size(); index++) {
      Field field = fields.get(index);
      if (field != null) {
        dumpField(index, field);
      }
    }

    endElement("record");
  }


  private void dumpField(int index, Field field) {
    AttributesImpl attributes = getEntryAttributes(field, "index", Integer.toString(index));

    /** @todo enumeration */

    startElement("field", attributes);

    /** @todo subfields */

    endElement("field");
  }


  private void dumpMakerNoteMarker(MakerNoteMarker marker, String tag) {
    /** @todo  */
  }


  private AttributesImpl getEntryAttributes(Entry entry, String tag) {
    return getEntryAttributes(entry, "tag", tag);
  }


  private AttributesImpl getEntryAttributes(Entry entry, String tagName, String tag) {
    AttributesImpl attributes = new AttributesImpl();

    /** @todo skip */

    if (tag != null) {
      addAttribute(attributes, tagName, tag);
    }

    Type type = entry.getType();

    if (type != null) {
      addAttribute(attributes, "type", type.toString());
    }

    addNameAttribute(attributes, entry.getName());

    return attributes;
  }


  private final MetaMetaData metaMetadata;
}
