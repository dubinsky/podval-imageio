package org.podval.imageio.metametadata.loader;

import org.podval.imageio.metametadata.MetaMetaData;

import org.podval.imageio.metametadata.MetaMetaDataException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;


public final class MetaMetaDataLoader {

    public MetaMetaDataLoader(final MetaMetaData metaMetaData) {
        this.metaMetaData = metaMetaData;
    }


    public void load(final String resourceName) throws XMLStreamException, IOException, MetaMetaDataException {
//        final InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
        final InputStream is = new FileInputStream(resourceName);
        if (is == null) {
            throw new IOException("Can't find resource " + resourceName);
        }

        if (resourceName.endsWith(".list")) {
            loadList(is);

        } else {
            loadXml(is);
        }

        is.close();
    }


    private void loadList(final InputStream is) throws XMLStreamException, IOException, MetaMetaDataException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if ((line.length() > 0) && !line.startsWith("#")) {
                load(line);
            }
        }
    }


    private void loadXml(final InputStream is) throws XMLStreamException, MetaMetaDataException {
        new Loader(is, metaMetaData).load();
    }


    private final MetaMetaData metaMetaData;
}
