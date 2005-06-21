/* $Id$ */

package org.podval.imageio;

import java.io.File;
import java.io.OutputStream;
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

      public boolean startHeap(int idCode) {
        return (idCode == 0);
      }


      public void endHeap() {
      }


      public void readRecord(Reader reader) {
        read(reader);
      }
    });

    in.close();

    if (exception != null) {
      throw exception;
    }
  }


  private void read(Reader reader) {
    if (reader.getDataTag() == this.idCode) {
      try {
        copy(reader.getInputStream(), reader.getDataLength());
      } catch (IOException e) {
        exception = e;
      }
    }
  }



  private void copy(ImageInputStream in, long length)
    throws IOException
  {
    OutputStream os = new FileOutputStream(file);
    for (long i = 0; i < length; i++) {
      int b = in.read();
      os.write(b);
    }
    os.close();
  }


  private final int idCode;


  private final File file;


  private IOException exception;
}
