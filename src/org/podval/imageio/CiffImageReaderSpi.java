package org.podval.imageio;

import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import javax.imageio.ImageReader;


public class CiffImageReaderSpi extends ImageReaderSpi {

  public CiffImageReaderSpi() {
    super(
      "Podval Group",
      "0.5",
      new String[] { "CIFF", "CRW", "ciff", "crw" },
      new String[] { "crw" },
      null,
      "org.podval.imageio.CiffImageReader",
      STANDARD_INPUT_TYPE,
      null,
      false,
      null, // CiffMetadata.NATIVE_FORMAT_NAME,
      null,
      null,
      null,
      false,
      CiffMetadataReader.NATIVE_FORMAT_NAME,
      null,
      null,
      null
    );
  }


  public String getDescription(Locale locale) {
    return "podval.org CIFF/Canon RAW image reader";
  }


  public boolean canDecodeInput(Object source) throws IOException {
    if (!(source instanceof ImageInputStream))
      return false;

    ImageInputStream in = (ImageInputStream) source;
    in.mark();

    // Skip byteorder indicator and length
    in.read();
    in.read();
    in.readUnsignedInt();

    boolean result = CiffMetadataReader.readSignature(in);
    in.reset();

    return result;
  }


  public ImageReader createReaderInstance(Object extension) {
    return new CiffImageReader(this);
  }
}
