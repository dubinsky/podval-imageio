package org.podval.imageio.metametadata;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.InputStream;


public final class Reader {

    protected Reader(final InputStream is, final MetaMetaData metaMetaData) throws XMLStreamException {
        this.in = XMLInputFactory.newInstance().createXMLStreamReader(is);
        this.metaMetaData = metaMetaData;
    }


    public void load() throws XMLStreamException, MetaMetaDataException {
        in.nextTag();
        enter(Tags.ROOT);

        while (!in.isEndElement()) {
            if (isLocalName(Tags.DIRECTORY)) {
                loadDirectory();
            } else if (isLocalName(Tags.RECORD)) {
                loadRecord();
            } else if (isLocalName("makerNote")) {
                loadMakerNote();
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }
/////        in.exit(Tags.ROOT);
    }


    private Heap loadDirectory() throws XMLStreamException, MetaMetaDataException {
        final Heap heap = metaMetaData.getHeap(getNameAttribute());
        setType(heap);

        enter(Tags.DIRECTORY);
        while (!in.isEndElement()) {
            final int tag = getIntegerAttribute("tag");

            final Entry entry;
            if (isLocalName(Tags.DIRECTORY)) {
                entry = loadDirectory();
            } else if (isLocalName(Tags.RECORD)) {
                entry = loadRecord();
            } else if (isLocalName("makerNoteMarker")) {
                entry = new MakerNoteMarker(metaMetaData);
                setType(entry);
            } else {
                throw new MetaMetaDataException(getLocalName());
            }

            heap.addEntry(tag, entry);
        }

        exit(Tags.DIRECTORY);

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

        enter(Tags.RECORD);

        while (!in.isEndElement()) {
            if (isLocalName(Tags.FIELD)) {
                if (hasAttribute("index")) {
                    index = getIntegerAttribute("index");
                }
                record.addField(index, loadField(record.getType()));
                index++;
            } else if (isLocalName(Tags.ENUMERATION)) {
                loadEnumeration(record.getDefaultField());
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        exit(Tags.RECORD);

        return record;
    }


    private void loadEnumeration(final Field field) throws XMLStreamException, MetaMetaDataException {
        final Enumeration enumeration = new Enumeration(getAttribute("class"));
        field.setEnumeration(enumeration);

        enter(Tags.ENUMERATION);

        while (!in.isEndElement()) {
            if (isLocalName("item")) {
                final String name = getAttribute("name");
                final String description = getAttribute("description");
                final int tag = getIntegerAttribute("tag");
                final EnumerationItem item = new EnumerationItem(name, description);

                enter(Tags.ITEM);
                exit(Tags.ITEM);

                enumeration.addItem(tag, item);
            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        exit(Tags.ENUMERATION);
    }



    private Field loadField(final Type parentType) throws XMLStreamException, MetaMetaDataException {
        final Field field = new Field(getNameAttribute());
        setType(field);

        if (field.getType() == null) {
            field.setType(parentType);
        }

        field.setSkip(getBooleanAttribute("skip"));
        field.setConversion(getAttribute("conversion"));

        enter(Tags.FIELD);

        while (!in.isEndElement()) {
            if (isLocalName(Tags.FIELD)) {
                final Field subField = loadField(field.getType());
                field.addSubField(subField);
            } else

            if (isLocalName(Tags.ENUMERATION)) {
              loadEnumeration(field);

            } else {
                throw new MetaMetaDataException(getLocalName());
            }
        }

        exit(Tags.FIELD);

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


    public void enter(final String name) throws XMLStreamException {
        if (!in.isStartElement()) {
            throw new XMLStreamException("Not at the start of an element");
        }
        checkName(name);
        in.nextTag();
    }


    public void exit(final String name) throws XMLStreamException {
        if (!in.isEndElement()) {
            throw new XMLStreamException("Not at the end of an element");
        }
        checkName(name);
        in.nextTag();
    }


    private void checkName(final String name) throws XMLStreamException {
        final String localName = getLocalName();
        if (!name.equals(localName)) {
            throw new XMLStreamException("Expected '" + name + "' but got '" + localName + "'");
        }
    }


    private String getLocalName() {
        return in.getLocalName();
    }


    private boolean isLocalName(final String value) {
        return value.equals(getLocalName());
    }


    private String getNameAttribute() throws XMLStreamException {
        return doGetAttribute("name");
    }


    private boolean getBooleanAttribute(final String name) {
        return Boolean.valueOf(getAttribute(name));
    }


    private int getIntegerAttribute(final String name) throws XMLStreamException {
        try {
            String value = getAttribute(name);
            return (value == null) ? 0 : Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new XMLStreamException(e);
        }
    }


    /** @todo eliminate; throw exceptions from the MetaMetaData objects themselves... */
    private String doGetAttribute(final String name) throws XMLStreamException {
        final String result = getAttribute(name);
        if (result == null) {
          throw new XMLStreamException("Missing required attribute " + name);
        }
        return result;
    }


    private boolean hasAttribute(final String name) {
        return getAttribute(name) != null;
    }


    private String getAttribute(final String name) {
        return in.getAttributeValue(null, name);
    }


    private final XMLStreamReader in;


    private final MetaMetaData metaMetaData;


    public static void main(final String[] args) throws Exception {
        final MetaMetaData result = new MetaMetaData();
        final String base = System.getProperty("user.dir") + "/src/main/resources/";
        new Loader(result).load(base + "org/podval/imageio/xml/ciff/ciff-root.xml");
    }
}
