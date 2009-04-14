package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;

import java.io.IOException;


public abstract class HeapReader extends FolderReader {

    public HeapReader(
        final ReaderNg reader,
        final Entry entry,
        final long heapOffset,
        final int heapLength)
    {
        super(reader, entry);
        this.heapOffset = heapOffset;
        this.heapLength = heapLength;
    }


    @Override
    protected final void start() throws IOException {
        final boolean haveHeap = seekToHeap();
        if (haveHeap) {
            numEntries = readNumberOfHeapEntries();
            entriesOffset = getIn().getStreamPosition();
        } else {
            numEntries = 0;
        }

        entryNum = 0;
    }


    @Override
    protected final EntryReader readNext() throws IOException {
        final EntryReader result;
        if (entryNum < numEntries) {
            seekToEntry(entryNum);
            EntryInformation entryInformation = readEntryInformation();

            if (entryInformation != null) {
                entryTag = entryInformation.tag;

                final Entry heapEntry = ((Heap) entry).getEntry(
                    entryInformation.kind,
                    entryTag,
                    entryInformation.type);

                if (heapEntry instanceof Heap) {
                    result = createHeapReader((Heap) heapEntry, entryInformation.offset, entryInformation.length);
                } else {
                    // @todo
                    throw new UnsupportedOperationException();
                }
            } else {
                result = null;
            }
        } else {
//            if (seekAfter) {
//                seekToEntry(numEntries);
//            }

            result = null;
        }

        return result;
    }


    private void seekToEntry(int entryNumber) throws IOException {
        getIn().seek(entriesOffset + getEntryLength() * entryNumber);
    }


    protected abstract boolean seekToHeap() throws IOException;


    protected abstract int readNumberOfHeapEntries() throws IOException;


    protected abstract int getEntryLength();


    protected abstract EntryInformation readEntryInformation() throws IOException;


    protected abstract HeapReader createHeapReader(final Heap heap, final long offset, final int length);


    protected final long heapOffset;


    protected final int heapLength;


    private int numEntries;


    private long entriesOffset;


    private int entryNum;


    private int entryTag;
}
