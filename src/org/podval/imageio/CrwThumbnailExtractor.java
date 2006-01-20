/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;


public class CrwThumbnailExtractor {

  public static void extractSmall(File original, File file)
    throws IOException
  {
    extract(original, 8, file);
  }


  public static void extractBig(File original, File file)
    throws IOException
  {
    extract(original, 7, file);
  }


  private static void extract(File original, final int idCode, final File file) throws IOException {
    ImageInputStream in = new FileImageInputStream(original);

    new CiffReader().read(in, new ReaderHandler() {

      public boolean startHeap(int tag, String name) {
        return (tag == 0);
      }


      public void endHeap() {
      }


      public boolean startRecord(int tag, String name) {
        return false;
      }


      public void endRecord() {
      }


      public ValueDisposition atValue(int tag, String name, TypeNG type, int count)
        throws FileNotFoundException
      {
        return (tag == idCode) ?
          ValueDisposition.stream(new FileOutputStream(file)) :
          ValueDisposition.SKIP;
      }


      public void handleValue(int tag, String name, TypeNG type, int count, Object value) {
      }


      public void handleRawValue(int tag, String name, TypeNG type, int count, ImageInputStream is) {
      }
    });

    in.close();
  }
}
