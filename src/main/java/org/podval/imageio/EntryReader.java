package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;

import javax.imageio.stream.ImageInputStream;


public abstract class EntryReader {

    public EntryReader(final ReaderNg reader, final Entry entry) {
        this.reader = reader;
        this.entry = entry;
    }


    protected final ImageInputStream getIn() {
        return reader.getInputStream();
    }


    protected final ReaderNg reader;


    protected final Entry entry;
}
