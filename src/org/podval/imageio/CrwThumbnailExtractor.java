/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.FileOutputStream;
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

    final IOException[] exception = new IOException[1];

    new CiffReader().read(in, new ReaderHandler() {

      public boolean startHeap(int tag, Heap heap) {
        return (tag == 0);
      }


      public void endHeap() {
      }


      public boolean startRecord(int tag, RecordNG record) {
        return false;
      }


      public void endRecord() {
      }


      public void handleValue(int tag, Object value, TypeNG type, RecordNG record) {
      }


      public void handleLongValue(int tag, int count, TypeNG type, RecordNG record, Reader reader) {
        if (tag == idCode) {
          try {
            reader.stream(new FileOutputStream(file));
          } catch (IOException e) {
            exception[0] = e;
          }
        }
      }
    });

    in.close();

    if (exception[0] != null) {
      throw exception[0];
    }
  }
}
