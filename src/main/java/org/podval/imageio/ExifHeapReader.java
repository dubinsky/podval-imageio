package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.Type;

import java.io.IOException;


public final class ExifHeapReader extends HeapReader {

    public ExifHeapReader(
        final ReaderNg reader,
        final Entry entry,
        final long heapOffset,
        final int heapLength)
    {
        super(reader, entry, heapOffset, heapLength);
    }


    @Override
    protected boolean seekToHeap() throws IOException {
        long offset = getIn().readUnsignedInt();
        boolean result = (offset != 0);
        if (result) {
            getIn().seek(getOffsetBase() + offset);
        }
        return result;
    }


    @Override
    protected int readNumberOfHeapEntries() throws IOException {
        return getIn().readUnsignedShort();
    }


    @Override
    protected int getEntryLength() {
        return 12;
    }


    @Override
    protected EntryInformation readEntryInformation() throws IOException {
        final int tag = getIn().readUnsignedShort();
        final Type type = decodeType(getIn().readUnsignedShort());
        final int count = reader.readUnsignedInt();
        final int length = count * type.getLength();

        final long offset = (length > 4) ?
            getOffsetBase() + getIn().readUnsignedInt() :
            getIn().getStreamPosition();

        return new EntryInformation(Entry.Kind.UNKNOWN, offset, length, tag, type);
    }


    private long getOffsetBase() {
        return ((ExifReaderNg) reader).getOffsetBase();
    }


    private static Type decodeType(int code) throws IOException {
        Type result;

        switch (code) {
        case 1: result = Type.U8; break; // "byte"
        case 2: result = Type.STRING; break; // "ASCII string"
        case 3: result = Type.U16; break; // "short"
        case 4: result = Type.U32; break; // "long"
        case 5: result = Type.RATIONAL; break; // "rational" (two longs)
        case 6: result = Type.S8; break;
        case 7: result = Type.X8; break; // "undefined"
        case 8: result = Type.S16; break;
        case 9: result = Type.S32; break; // "slong"
        case 10: result = Type.SRATIONAL; break; // "srational"
        case 11: result = Type.F32; break; // "single float"
        case 12: result = Type.F64; break; // "double float"

        default:
            throw new IOException("Unknown data type " + code);
        }

        return result;
    }


    @Override
    protected HeapReader createHeapReader(final Heap heap, final long offset, final int length) {
        return new ExifHeapReader(reader, heap, offset, length);
    }
}
