package org.podval.imageio;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadataNode;


public class Group {

  public void addBinding(Typed key, Object value) {
    if (key == null)
      throw new NullPointerException("Key is null!");

    if (value == null)
      throw new NullPointerException("Value is null!");

    entries.add(new Binding(key, value));
  }


  public Object find(String name) {
    Object result = null;
    for (Iterator i = entries.iterator(); i.hasNext();) {
      Binding binding = (Binding) i.next();
      Object value = binding.value;
      // Group and value may have the same name, so we look inside groups first.
      if (value instanceof Group) {
        result = ((Group) value).find(name);
        if (result != null)
          break;
      }
      if (binding.key.getName().equals(name)) {
        result = value;
        break;
      }
    }
    return result;
  }


  public /** @todo should be private? */ static IIOMetadataNode getNativeTree(Binding binding) {
    IIOMetadataNode result;
    String name = binding.key.getName();
    Object value = binding.value;
    if (value instanceof Group) {
      List entries = ((Group) value).entries;
      if (entries.size() == 1) {
        result = getNativeTree((Binding) entries.get(0));
      } else {
        result = new IIOMetadataNode(name);
        for (Iterator i = entries.iterator(); i.hasNext();) {
          result.appendChild(getNativeTree((Binding) i.next()));
        }
      }
    } else {
      result = new IIOMetadataNode(name);
      if (value instanceof ComplexValue) {
        ((ComplexValue) value).buildNativeTree(result);
      } else {
        result.setAttribute("value", value.toString());
      }
    }
    return result;
  }


  private final List entries = new ArrayList();



  /**
   *
   */
  public static interface ComplexValue {
    public void buildNativeTree(IIOMetadataNode result);
  }



  /**
   *
   */
  public /** @todo should be private? */ static class Binding {
    public Binding(Typed key, Object value) {
      this.key = key;
      this.value = value;
    }

    private Typed key;
    private Object value;
  }
}
