package org.podval.imageio;

import java.util.Stack;

import javax.imageio.metadata.IIOMetadataNode;


public class DefaultMetadataHandler implements MetadataHandler {

  public DefaultMetadataHandler(String name) {
    result = new Metadata(name);
    tip.push(result.getRoot());
  }


  public Directory getInitDirectory() {
    return result.getInitDirectory();
  }


  public void startGroup(Typed group) {
    Group node = new Group();
    add(group, node);
    tip.push(node);
  }


  public void endGroup() {
    tip.pop();
  }


  private void add(Typed typed, Object value) {
    ((Group) tip.peek()).addBinding(typed, value);
  }


  public void integerValue(Field field, long value) {
    add(field, new Long(value));
  }


  public void stringValue(Field field, String value) {
    add(field, value);
  }


  public void floatValue(Field field, float value) {
    add(field, new Float(value));
  }


  public void binaryValue(Field field, byte[] value) {
    add(field, new HexValue(value));
  }


  public void pointerValue(Field field, long offset, long length) {
    add(field, new PointerValue(offset, length));
  }


  public MakerNote getMakerNote() {
    return MakerNote.get(result.getStringValue("make"));
  }


  public Metadata getResult() {
    return result;
  }


  private final Metadata result;


  private Stack tip = new Stack();



  /**
   *
   */
  private static final class PointerValue implements Group.ComplexValue {
    public PointerValue(long offset, long length) {
      this.offset = offset;
      this.length = length;
    }

    public void buildNativeTree(IIOMetadataNode result) {
      result.setAttribute("offset", Long.toString(offset));
      result.setAttribute("length", Long.toString(length));
    }

    public long getOffset() { return offset; }
    public long getLength() { return length; }
    private final long offset;
    private final long length;
  }



  /**
   *
   */
  private static final class HexValue {
    public HexValue(byte[] value) { this.value = value; }

    private static final char[] HEX_DIGIT = new char[]
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static char toHexDigit(int d) { return HEX_DIGIT[d]; }

    public String toString() {
      StringBuffer result = new StringBuffer(2*value.length);
      for (int i=0; i<value.length; i++) {
        byte b = value[i];
        result.append(toHexDigit( b       & 0x0F));
        result.append(toHexDigit((b >> 4) & 0x0F));
      }
      return new String(result);
    }

    private final byte[] value;
  }
}
