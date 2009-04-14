package org.podval.imageio;

import org.podval.imageio.metametadata.MetaMetaData;

import org.podval.imageio.util.Util;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;

import java.nio.ByteOrder;

import java.util.Stack;


public abstract class ReaderNg {

    /**
     * Checks if the stream seems to be of the appropriate format.
     * Strem position is unchanged by this method, but other stream attributes
     * might change (e.g., byte order).
     * Attempts to read format-specific prolog and returns <code>true</code>
     * if succesfull and <code>false</code> if it fails.

     * @return boolean
     */
    public final boolean canRead(final ImageInputStream in) {
        if (in == null) {
            throw new NullPointerException("in");
        }

        this.in = in;

        boolean result = false;

        in.mark();
        try {
            readPrologue();
            result = true;
        } catch (IOException e) {
        } finally {
            try {
                in.reset();
            } catch (IOException e) {
            }
        }

        return result;
    }


    public final void start(ImageInputStream in) throws IOException {
        start(in, new MetaMetaData());
    }


    public final void start(ImageInputStream in, MetaMetaData metaMetaData) throws IOException {
        if (in == null) {
            throw new NullPointerException("in");
        }

        if (metaMetaData == null) {
            throw new NullPointerException("metaMetaData");
        }

        this.in = in;

        readPrologue();

        final EntryReader firstReader = start(metaMetaData);

        readers.push(firstReader);
    }


    public void next() {
        if (readers.isEmpty()) {
            throw new IllegalStateException(); // @todo
        }

        // @todo
    }


    public final ImageInputStream getInputStream() {
        return in;
    }


    /**
     * Reads format-specific prologue.
     *
     * @throws IOException
     */
    protected abstract void readPrologue() throws IOException;


    /**
     * Start reading format-specific content.
     *
     * @throws IOException
     */
    protected abstract EntryReader start(MetaMetaData metaMetaData) throws IOException;


    /**
     * Determines byte order used from a special marker, and arranges so that it
     * is used for all subsequent reads.
     *
     * @throws IOException
     */
    protected final void determineByteOrder() throws IOException {
        int byte1 = in.read();
        int byte2 = in.read();

        ByteOrder byteOrder;

        if ((byte1 == 'I') && (byte2 == 'I')) {
            byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else if ((byte1 == 'M') && (byte2 == 'M')) {
            byteOrder = ByteOrder.BIG_ENDIAN;

        } else {
            throw new IOException("Bad byte order marker.");
        }

        in.setByteOrder(byteOrder);
    }


    protected final void readSignature(final int[] signature, final String message) throws IOException {
        for (int i = 0; i < signature.length; i++) {
            if (in.read() != signature[i]) {
                throw new IOException(message);
            }
        }
    }


    // @todo push/pop EntryState


    protected int readUnsignedInt() throws IOException {
        return Util.readUnsignedInt(getInputStream());
    }


    protected ImageInputStream in;


    private Stack<EntryReader> readers = new Stack<EntryReader>();
}
