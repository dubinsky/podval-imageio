package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;

import java.io.IOException;


public final class ExifReaderNg extends ReaderNg {

    private static int[] EXIF_SIGNATURE = {'E', 'x', 'i', 'f', 0, 0};


    @Override
    protected void readPrologue() throws IOException {
        readSignature(EXIF_SIGNATURE, "Bad EXIF signature.");

        offsetBase = in.getStreamPosition();

        determineByteOrder();

        if (in.readUnsignedShort() != 0x2A) {
            throw new IOException("Bad TIFF magic.");
        }
    }


    public long getOffsetBase() {
        return offsetBase;
    }


    @Override
    protected EntryReader start(MetaMetaData metaMetaData) throws IOException {
        // @todo
//        // Since virtually all the tags (except 513 and 514) seem to be allowed
//        // both in IFD0 and IFD1 (including EXIF and GPS IFDs),
//        // the same directory descriptor can be used for IFD1 too.
//        Heap rootHeap = metaMetaData.getHeap("exif-root");
//        if (rootHeap.read(this, 0, 0, 0, true)) {
//            rootHeap.read(this, 0, 0, 1, false);
//        }
        return null;
    }


//    public final void handleValue(int tag, String name, int count, Object value) {
//        if ("make".equals(name)) {
//            make = (String) value;
//        }
//
//        super.handleValue(tag, name, count, value);
//    }


    public final String getMake() {
        return make;
    }


    private String make;


    private long offsetBase;
}
