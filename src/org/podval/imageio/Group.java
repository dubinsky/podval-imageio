package org.podval.imageio;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOMetadataNode;


public class Group extends Entry {

  public void addEntry(Entry entry) {
    if (entry == null)
      throw new NullPointerException("Entry is null!");

    entries.add(entry);
  }


  public Entry getEntry(String name) {
    Entry result = null;
    for (Iterator i = entries.iterator(); i.hasNext();) {
      result = ((Entry) i.next()).getEntry(name);
      if (result != null)
        break;
    }
    return result;
  }


  public final Node getNativeTree() {
    Node result;
    if (entries.size() == 1) {
      result = ((Entry) entries.get(0)).getNativeTree();
    } else {
      IIOMetadataNode node = new IIOMetadataNode(getName());
      for (Iterator i = entries.iterator(); i.hasNext();) {
        node.appendChild(((Entry) i.next()).getNativeTree());
      }
      result = node;
    }

    return result;
  }


  private final List entries = new ArrayList();
}
