package org.podval.imageio;

import javax.imageio.metadata.IIOMetadataNode;


public abstract class Value extends Entry {

  public final Entry getEntry(String name) {
    return (getName().equals(name)) ? this : null;
  }



  /**
   *
   */
  public static final class PointerValue extends Value {
    public PointerValue(long offset, long length) {
      this.offset = offset;
      this.length = length;
    }

    protected void buildNativeTree(IIOMetadataNode result) {
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
  private static abstract class SimpleValue extends Value {
    protected final void buildNativeTree(IIOMetadataNode result) {
      result.setAttribute("value", getValueAsString());
    }

    protected abstract String getValueAsString();
  }


  /**
   *
   */
  public static final class StringValue extends SimpleValue {
    public StringValue(String value) { this.value = value; }
    protected String getValueAsString() { return value; }
    public String getValue() { return value; }
    private final String value;
  }


  /**
   *
   */
  public static final class IntegerValue extends SimpleValue {
    public IntegerValue(long value) { this.value = value; }
    public long getValue() { return value; }
    protected String getValueAsString() { return Long.toString(value); }
    private final long value;
  }


  /**
   *
   */
  public static final class FloatValue extends SimpleValue {
    public FloatValue(float value) { this.value = value; }
    protected String getValueAsString() { return Float.toString(value); }
    private final float value;
  }


  /**
   *
   */
  public static final class HexValue extends SimpleValue {
    public HexValue(byte[] value) { this.value = value; }


    private static final char[] HEX_DIGIT = new char[]
      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static char toHexDigit(int d) { return HEX_DIGIT[d]; }

    protected String getValueAsString() {
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
