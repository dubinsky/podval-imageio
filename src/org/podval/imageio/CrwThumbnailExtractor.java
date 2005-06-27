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


  private static void extract(File original, int idCode, File file)
    throws IOException
  {
    new CrwThumbnailExtractor(idCode, file).extract(original);
  }


  private CrwThumbnailExtractor(int idCode, File file) {
    this.idCode = idCode;
    this.file = file;
  }


  private void extract(File original) throws IOException {
    ImageInputStream in = new FileImageInputStream(original);

    new CiffReader(in).read(new ReaderHandler() {

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


      public void readRecord(int tag, TypeNG type, int length, int count, Reader reader, RecordNG record) {
        if (tag == idCode) {
          try {
            reader.stream(length, new FileOutputStream(file));
          } catch (IOException e) {
            exception = e;
          }
        }
      }
    });

    in.close();

    if (exception != null) {
      throw exception;
    }
  }


  private final int idCode;


  private final File file;


  private IOException exception;
}
