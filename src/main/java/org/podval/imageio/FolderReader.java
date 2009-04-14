package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;

import java.io.IOException;


public abstract class FolderReader extends EntryReader {

    public FolderReader(final ReaderNg reader, final Entry entry) {
        super(reader, entry);
        state = State.START;
    }


    private enum State { START, CONTENT, END }


    public final boolean isStart() {
        return state == State.START;
    }


    public final boolean isEnd() {
        return state == State.END;
    }


    public final EntryReader next() throws IOException {
        if (isEnd()) {
            throw new IllegalStateException(); // @todo
        }

        if (isStart()) {
            start();
            state = State.CONTENT;
        }

        final EntryReader result = readNext();

        if (result == null) {
            state = State.END;
        }

        return result;
    }


    protected abstract void start() throws IOException;


    protected abstract EntryReader readNext() throws IOException;


    private State state;
}
