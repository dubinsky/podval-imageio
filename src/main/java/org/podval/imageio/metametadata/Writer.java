package org.podval.imageio.metametadata;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLStreamException;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.io.OutputStream;


public final class Writer {

    /** @todo type names are dumped uppercase, but the loader accepts lowercase... */
    /** @todo U32_OR_U16 records are dumped twice... */
    public static void dump(final Heap heap, final OutputStream os) throws XMLStreamException {
        new Writer(heap, os).dump();
    }


    private Writer(final Heap heap, final OutputStream os) throws XMLStreamException {
        this.heap = heap;
        this.out = XMLOutputFactory.newInstance().createXMLStreamWriter(os);
    }


    protected void dump() throws XMLStreamException {
        dumpHeap(heap, null);
    }


    private void dumpHeap(final Heap heap, final String tag) throws XMLStreamException {
        out.writeStartElement(Tags.DIRECTORY);
        dumpEntryAttributes(heap, tag);

        for (final Key key : heap.getKeys()) {
            final Entry entry = heap.getEntry(key);
            final String entryTag = Integer.toString(key.tag);

            if (entry instanceof Heap) {
                dumpHeap((Heap) entry, entryTag);
            } else if (entry instanceof Record) {
                dumpRecord((Record) entry, entryTag);
            } else if (entry instanceof MakerNoteMarker) {
                dumpMakerNoteMarker((MakerNoteMarker) entry, entryTag);
            } else {
            }
        }

        out.writeEndElement();
    }


    private void dumpRecord(Record record, String tag) throws XMLStreamException {
        out.writeStartElement(Tags.RECORD);
        dumpEntryAttributes(record, tag);

        if (record.hasDefaultField()) {
            try {
                Field defaultField = record.getDefaultField();
                dumpField(defaultField);
            } catch (MetaMetaDataException e) {
                throw new IllegalArgumentException(e);
            }
        } else {


            if (record.isVector()) {
                dumpNullableAttribute("vector", "true");
                dumpIntegerAttribute("count", record.getFields().size());
            }

            List<Field> fields = record.getFields();
            for (int index = (record.isVector()) ? 1 : 0; index < fields.size(); index++) {
                final Field field = fields.get(index);
                out.writeStartElement(Tags.FIELD);
                dumpEntryAttributes(field, "index", Integer.toString(index));
                if (field != null) {
                    dumpField(field);
                }
            }
        }

        out.writeEndElement();
    }


    private void dumpMakerNoteMarker(MakerNoteMarker marker, String tag) throws XMLStreamException {
        out.writeStartElement("makerNoteMarker");
        dumpEntryAttributes(marker, tag);
        out.writeEndElement();
    }


    private void dumpField(final Field field) throws XMLStreamException {
        dumpNullableAttribute("conversion", field.getConversion());

        dumpEnumeration(field);

        List<Field> subFields = field.getSubFields();
        if (subFields != null) {
            for (Field subField : subFields) {
                out.writeStartElement(Tags.FIELD);
                dumpEntryAttributes(subField, null, null);
                dumpField(subField);
                out.writeEndElement();
            }
        }
    }


    private void dumpEnumeration(Field field) throws XMLStreamException {
        Enumeration enumeration = field.getEnumeration();
        if (enumeration != null) {
            out.writeStartElement(Tags.ENUMERATION);
            dumpNullableAttribute("class", enumeration.getClassName());
            dumpItems(enumeration);
            out.writeEndElement();
        }
    }


    private void dumpItems(Enumeration enumeration) throws XMLStreamException {
        List<Integer> tags = new ArrayList<Integer>(enumeration.getTags());
        Collections.sort(tags);

        for (int tag : tags) {
            EnumerationItem item = enumeration.getItem(tag);

            out.writeStartElement(Tags.ITEM);

            dumpIntegerAttribute("tag", tag);
            dumpNullableAttribute("name", item.name);
            dumpNullableAttribute("description", item.description);

            out.writeEndElement();
        }
    }


    private void dumpEntryAttributes(Entry entry, String tag) throws XMLStreamException {
        dumpEntryAttributes(entry, "tag", tag);
    }


    private void dumpEntryAttributes(Entry entry, String tagName, String tag) throws XMLStreamException {
        dumpNullableAttribute(tagName, tag);
        dumpNullableAttribute("type", entry.getType());
        dumpNullableAttribute("name", entry.getName());

        if (entry.isSkip()) {
            dumpNullableAttribute("skip", "true");
        }
    }


    private final void dumpNullableAttribute(final String name, final Object value) throws XMLStreamException {
        if (value != null) {
            out.writeAttribute(name, value.toString());
        }
    }


    private final void dumpIntegerAttribute(final String name, final int value) throws XMLStreamException {
        dumpNullableAttribute(name, Integer.toString(value));
    }


    private final Heap heap;


    private final XMLStreamWriter out;
}
