/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;


public class CrwThumbnailExtractor {

  private static final int THUMB_TAG = 8;


  private static final int PROOF_TAG = 7;


  public static void extract(File original, final File thumb, final File proof)
    throws IOException
  {
    ImageInputStream in = new FileImageInputStream(original);

    new CiffReader().read(in, new ReaderHandler() {

      public boolean startFolder(int tag, String name) {
        return (tag == 0);
      }


      public void endFolder() {
      }


      public ValueAction atValue(int tag, String name, int count) {
        boolean stream =
          ((thumb != null) && (tag == THUMB_TAG)) ||
          ((proof != null) && (tag == PROOF_TAG));

        return (stream) ? ValueAction.RAW : ValueAction.SKIP;
      }


      public void handleValue(int tag, String name, int count, Object value) {
      }


      public void handleRawValue(int tag, String name, int count, ImageInputStream is)
        throws IOException
      {
        File file = (tag == THUMB_TAG) ? thumb : proof;
        stream(is, new FileOutputStream(file), count);
      }


      public String getMake() {
        return null;
      }
    });

    in.close();
  }


  private static void stream(ImageInputStream is, OutputStream os, int length) throws IOException {
    for (long i = 0; i < length; i++) {
      int b = is.read();
      os.write(b);
    }

    os.close();
  }
}
