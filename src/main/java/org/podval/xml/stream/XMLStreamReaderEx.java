package org.podval.xml.stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;


public final class XMLStreamReaderEx extends StreamReaderDelegate {

    public XMLStreamReaderEx(final XMLStreamReader parent) {
        super(parent);
    }


    public void enter(final String name) throws XMLStreamException {
        if (!getParent().isStartElement()) {
            throw new XMLStreamException("Not at the start of an element");
        }
        checkName(name);
        nextTag();
    }


    public void exit(final String name) throws XMLStreamException {
        if (!getParent().isEndElement()) {
            throw new XMLStreamException("Not at the end of an element");
        }
        checkName(name);
        nextTag();
    }


    private void checkName(final String name) throws XMLStreamException {
        final String localName = getLocalName();
        if (!name.equals(localName)) {
            throw new XMLStreamException("Expected '" + name + "' but got '" + localName + "'");
        }
    }
}
