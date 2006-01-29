package org.podval.imageio;

import java.util.Locale;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import javax.imageio.ImageReader;


public class CiffImageReaderSpi extends ImageReaderSpi {

//  public static void init() {
//    /** @todo should register using jar manifest (also). */
//    IIORegistry.getDefaultInstance().registerServiceProvider(new
//      CiffImageReaderSpi());
//  }


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
      null, // CiffImageReader.NATIVE_FORMAT_NAME,
      null,
      null,
      null,
      false,
      CiffImageReader.NATIVE_FORMAT_NAME,
      null,
      null,
      null
    );
  }


  public String getDescription(Locale locale) {
    return "podval.org CIFF/Canon RAW image reader";
  }


  public boolean canDecodeInput(Object source) throws IOException {
    return ((source instanceof ImageInputStream) &&
      CiffImageReader.canDecodeInput((ImageInputStream) source));
  }


  public ImageReader createReaderInstance(Object extension) {
    return new CiffImageReader(this);
  }
}
