package org.podval.imageio;

import org.podval.imageio.metametadata.Entry;
import org.podval.imageio.metametadata.MetaMetaData;
import org.podval.imageio.metametadata.Type;

import org.podval.imageio.util.Util;

import java.io.IOException;


public final class CiffReaderNg extends ReaderNg {

    private static final int[] CIFF_SIGNATURE = {'H', 'E', 'A', 'P', 'C', 'C', 'D', 'R'};


    @Override
    protected void readPrologue() throws IOException {
        determineByteOrder();

        headerLength = readUnsignedInt();

        readSignature(CIFF_SIGNATURE, "Bad CIFF signature.");

        if (readUnsignedInt() != 0x00010002) {
            throw new IOException("Bad CIFF version.");
        }
    }


    @Override
    protected EntryReader start(MetaMetaData metaMetaData) throws IOException {
        int heapLength = Util.toInt(in.length() - headerLength);
        return new CiffHeapReader(this, metaMetaData.getHeap("ciff-root"), headerLength, heapLength);
    }


    private int headerLength;
}
