package org.podval.imageio;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.metadata.IIOMetadataNode;


public class Group extends Entry {

  public void addEntry(Entry entry) {
    if (entry == null)
      throw new NullPointerException("Entry is null!");

    entries.add(entry);
  }


  public void setParent(Group parent) {
    this.parent = parent;
  }


  public Group getParent() {
    return parent;
  }


  public void flatten(Group child) {
    int index = entries.indexOf(child);
    assert (index != -1) : "Alleged child is not!";
    Entry flat = child.flatten();
    if (flat != child)
      entries.set(index, flat);
  }


  public Entry flatten() {
    return (entries.size() == 1) ? (Entry) entries.get(0) : this;
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


  protected void buildNativeTree(IIOMetadataNode result) {
    for (Iterator i = entries.iterator(); i.hasNext();) {
      result.appendChild(((Entry) i.next()).getNativeTree());
    }
  }


  private final List entries = new ArrayList();


  private Group parent;
}
