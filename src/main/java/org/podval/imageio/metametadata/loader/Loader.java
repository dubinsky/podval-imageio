package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.MakerNoteMarker;
import org.podval.imageio.metametadata.Record;
import org.podval.imageio.metametadata.Field;
import org.podval.imageio.metametadata.Enumeration;
import org.podval.imageio.metametadata.EnumerationItem;
import org.podval.imageio.metametadata.Type;
import org.podval.imageio.metametadata.MetaMetaDataException;

import org.podval.xml.stream.XMLStreamReaderEx;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;


public final class Loader {

    protected Loader(final XMLStreamReader in, final MetaMetaData metaMetaData) {
        this.in = new XMLStreamReaderEx(in);
        this.metaMetaData = metaMetaData;
    }


    private static final String ROOT_TAG = "meta-metadata";


    private static final String DIRECTORY_TAG = "directory";


    private static final String RECORD_TAG = "record";


    private static final String FIELD_TAG = "field";


    private static final String ENUMERATION_TAG = "enumeration";


    private static final String ITEM_TAG = "item";


    public void load() throws XMLStreamException, MetaMetaDataException {
        in.nextTag();
        in.enter(ROOT_TAG);

        while (!in.isEndElement()) {
            if (isLocalName(DIRECTORY_TAG)) {
                loadDirectory();
            } else if (isLocalName(RECORD_TAG)) {
                loadRecord();
            } else if (isLocalName("makerNote")) {
                loadMakerNote();
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }
/////        in.exit(ROOT_TAG);
    }


    private Heap loadDirectory() throws XMLStreamException, MetaMetaDataException {
        final Heap heap = metaMetaData.getHeap(getNameAttribute());
        setType(heap);

        in.enter(DIRECTORY_TAG);
        while (!in.isEndElement()) {
            final int tag = getIntegerAttribute("tag");

            final Entry entry;
            if (isLocalName(DIRECTORY_TAG)) {
                entry = loadDirectory();
            } else if (isLocalName(RECORD_TAG)) {
                entry = loadRecord();
            } else if (isLocalName("makerNoteMarker")) {
                entry = new MakerNoteMarker();
                setType(entry);
            } else {
                throw new MetaMetaDataException(getLocalName());
            }

            heap.addEntry(tag, entry);
        }

        in.exit(DIRECTORY_TAG);

        return heap;
    }


    private Record loadRecord() throws XMLStreamException, MetaMetaDataException {
        final Record record = metaMetaData.getRecord(getNameAttribute());
        setType(record);

        /** @todo count */
        record.setIsVector(getBooleanAttribute("vector"));
        record.setSkip(getBooleanAttribute("skip"));

        String conversion = getAttribute("conversion");
        if (conversion != null) {
          record.getDefaultField().setConversion(conversion);
        }

        int index = 0;

        in.enter(RECORD_TAG);

        while (!in.isEndElement()) {
            if (isLocalName(FIELD_TAG)) {
                if (hasAttribute("index")) {
                    index = getIntegerAttribute("index");
                }
                record.addField(index, loadField(record.getType()));
                index++;
            } else if (isLocalName(ENUMERATION_TAG)) {
                loadEnumeration(record.getDefaultField());
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        in.exit(RECORD_TAG);

        return record;
    }


    private void loadEnumeration(final Field field) throws XMLStreamException, MetaMetaDataException {
        final Enumeration enumeration = new Enumeration(getAttribute("class"));
        field.setEnumeration(enumeration);

        in.enter(ENUMERATION_TAG);

        while (!in.isEndElement()) {
            if (isLocalName("item")) {
                final String name = getAttribute("name");
                final String description = getAttribute("description");
                final int tag = getIntegerAttribute("tag");
                final EnumerationItem item = new EnumerationItem(name, description);

                in.enter(ITEM_TAG);
                in.exit(ITEM_TAG);

                enumeration.addItem(tag, item);
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        in.exit(ENUMERATION_TAG);
    }



    private Field loadField(final Type parentType) throws XMLStreamException, MetaMetaDataException {
        final Field field = new Field(getNameAttribute());
        setType(field);

        if (field.getType() == null) {
            field.setType(parentType);
        }

        field.setSkip(getBooleanAttribute("skip"));
        field.setConversion(getAttribute("conversion"));

        in.enter(FIELD_TAG);

        while (!in.isEndElement()) {
            if (isLocalName(FIELD_TAG)) {
                final Field subField = loadField(field.getType());
                field.addSubField(subField);
            } else

            if (isLocalName(ENUMERATION_TAG)) {
              loadEnumeration(field);

            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        in.exit(FIELD_TAG);

        field.checkSubFields();

        return field;
    }


    private void loadMakerNote() {
        // @todo
//        getRequiredAttribute("make");
//        getMetaMetaData().addMakerNoteReaderClass(thing,  getRequiredAttribute("class"));
    }


    private void setType(final Entry entry) throws MetaMetaDataException {
        Type type = null;

        String typeName = getAttribute("type");

        if (typeName != null) {
            try {
                typeName = typeName.toUpperCase();
                type = Type.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                throw new MetaMetaDataException("Unknown type " + typeName);
            }

            entry.setType(type);
        }
    }


    private String getLocalName() {
        return in.getLocalName();
    }


    private boolean isLocalName(final String value) {
        return value.equals(getLocalName());
    }


    private String getNameAttribute() throws MetaMetaDataException {
        return doGetAttribute("name");
    }


    private boolean getBooleanAttribute(final String name) {
        return Boolean.valueOf(getAttribute(name));
    }


    private int getIntegerAttribute(final String name) throws MetaMetaDataException {
        try {
            String value = getAttribute(name);
            return (value == null) ? 0 : Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new MetaMetaDataException(e);
        }
    }


    /** @todo eliminate; throw exceptions from the MetaMetaData objects themselves... */
    private String doGetAttribute(final String name) throws MetaMetaDataException {
        final String result = getAttribute(name);
        if (result == null) {
          throw new MetaMetaDataException("Missing required attribute " + name);
        }
        return result;
    }


    private boolean hasAttribute(final String name) {
        return getAttribute(name) != null;
    }


    private String getAttribute(final String name) {
        return in.getAttributeValue(null, name);
    }


    private final XMLStreamReaderEx in;


    private final MetaMetaData metaMetaData;


    public static void main(final String[] args) throws Exception {
        final MetaMetaData result = new MetaMetaData();
        final String base = System.getProperty("user.dir") + "/src/main/resources/";
        new MetaMetaDataLoader(result).load(base + "org/podval/imageio/xml/ciff/ciff-root.xml");
    }
}
