package org.podval.imageio;

import java.util.Stack;

import javax.imageio.metadata.IIOMetadataNode;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class DefaultMetadataHandler implements MetadataHandler {

  public DefaultMetadataHandler(String name) {
    result = new Metadata(name);
    tip.push(result.getRoot());
  }


  public void startGroup(Typed typed) {
    Group node = new Group();
    add(typed, node);
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
    /** @todo this should be in the Field! */
    Object result = value;

    Method conversion =  field.getConversion();
    if (conversion != null) {
      try {
        /** @todo when the method is not static, error message is very confusing... */
        result = conversion.invoke(null, new Object[] {value});
      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      }
    }
    add(field, result);
  }


  public void floatValue(Field field, float value) {
    add(field, new Float(value));
  }


  public void binaryValue(Field field, byte[] value) {
    add(field, new BinaryValue(value));
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
  private static final class BinaryValue {
    public BinaryValue(byte[] value) { this.value = value; }

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
