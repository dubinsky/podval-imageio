/* $Id$ */

package org.podval.imageio;


public class DumpingHandler implements ReaderHandler {

  public boolean startHeap(int idCode) {
    indent();
    System.out.println("Heap: " + idCode);
    level++;
    return true;
  }


  public void endHeap() {
    level--;
  }


  public void readRecord(Reader reader) {
    indent();
    System.out.println("Record: " + reader.getDataTag() + " :: " +
      reader.getDataType() + "[" + reader.getDataCount() + "] (=" +
      reader.getDataLength() + ")");
  }


  private void indent() {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }


  private int level = 0;
}
