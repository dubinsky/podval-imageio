/* $Id$ */

package org.podval.imageio.metametadata;


public final class Key implements Comparable<Key> {

  public Key(int tag, Type type) {
    this.tag = tag;
    this.type = type;
  }


  public int compareTo(Key other) {
    int result;

    if (tag < other.tag) {
      result = -1;
    } else
    if (tag > other.tag) {
      result = +1;
    } else {
      result = type.compareTo(other.type);
    }

    return result;
  }


  public boolean equals(Object o) {
    Key other = (Key) o;
    return (this.tag == other.tag) && (this.type == other.type);
  }


  public int hashCode() {
    int result = tag*317;
    if (type != null) {
      result += type.hashCode();
    }
    return result;
  }


  public String toString() {
    return tag + "-" + type;
  }


  public final int tag;


  public final Type type;
}
