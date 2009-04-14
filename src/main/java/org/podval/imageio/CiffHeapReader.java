package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.Heap;
import org.podval.imageio.metametadata.Type;

import java.io.IOException;


public final class CiffHeapReader extends HeapReader {

    public CiffHeapReader(
        final ReaderNg reader,
        final Entry entry,
        final long heapOffset,
        final int heapLength)
    {
        super(reader, entry, heapOffset, heapLength);
    }


    @Override
    protected boolean seekToHeap() throws IOException {
        return true;
    }


    @Override
    protected int readNumberOfHeapEntries() throws IOException {
        getIn().seek(heapOffset + heapLength - 4);

        int offsetTblOffset = reader.readUnsignedInt();
        getIn().seek(heapOffset + offsetTblOffset);

        return getIn().readUnsignedShort();
    }


    @Override
    protected int getEntryLength() {
        return 10;
    }


    @Override
    public EntryInformation readEntryInformation() throws IOException {
        int typeCode = getIn().readUnsignedShort();

        boolean empty =
            (typeCode == 0 /* Null entry. */) ||
            (typeCode == 1 /* Free entry. */);

        return empty ? null : readEntryInformation(typeCode);
    }


    private EntryInformation readEntryInformation(int typeCode) throws IOException {
        int storageMethod = (typeCode & 0xC000) >> 14;
        int dataType = (typeCode & 0x3800) >> 11;
        int idCode = typeCode & 0x07FF;

        if ((storageMethod & 0x02) != 0) {
            throw new IOException("Unknown storage method.");
        }

        boolean inHeapSpace = ((storageMethod & 0x01) == 0);

        long offset;
        int length;

        if (inHeapSpace) {
            length = reader.readUnsignedInt();
            offset = heapOffset + reader.readUnsignedInt();
        } else {
            length = 8;
            offset = getIn().getStreamPosition();
        }

        Type type = decodeType(dataType);

        boolean isHeap = ((type == Type.ONE) || (type == Type.TWO));

        Entry.Kind kind = (isHeap ? Entry.Kind.HEAP : Entry.Kind.RECORD);

        return new EntryInformation(kind, offset, length, idCode, type);
    }


    private static Type decodeType(int dataType) throws IOException {
        Type result;

        switch (dataType) {
        case 0: result = Type.U8; break;
        case 1: result = Type.STRING; break;
        case 2: result = Type.U16; break;
        case 3: result = Type.U32; break;
        case 4: result = Type.X8;  break;
        case 5: result = Type.ONE; break;
        case 6: result = Type.TWO; break;

        default:
            throw new IOException("Unknown data type.");
        }

        return result;
    }


    @Override
    protected HeapReader createHeapReader(final Heap heap, final long offset, final int length) {
        return new CiffHeapReader(reader, heap, offset, length);
    }
}
