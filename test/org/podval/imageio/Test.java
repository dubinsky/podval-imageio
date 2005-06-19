/* $Id$ */

package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.FileImageInputStream;

import java.io.File;
import java.io.IOException;


public class Test {

  private static class DumpingHandler implements CiffHandler {

    public boolean startHeap(int idCode) {
      indent();
      System.out.println("Heap: " + idCode);
      level++;
      return true;
    }


    public void endHeap() {
      level--;
    }


    public void readRecord(ImageInputStream in, long offset, long length, CiffType type, int idCode) {
      indent();
      System.out.println("Record: " + idCode + " :: "+ type + "[" + length + "]");
    }


    private void indent() {
      for (int i = 0; i<level; i++) {
        System.out.print("  ");
      }
    }


    private int level = 0;
  }



  public static void main(String[] args) throws IOException {
    File file = new File(args[0]);

    CiffReader.read(new FileImageInputStream(file), new DumpingHandler());

    CrwThumbnailExtractor.extractBig(file, new File("/tmp/" + file.getName() + ".big.jpeg"));

    CrwThumbnailExtractor.extractSmall(file, new File("/tmp/" + file.getName() + ".small.jpeg"));
  }
}
