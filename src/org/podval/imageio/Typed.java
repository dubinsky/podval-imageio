package org.podval.imageio;


public abstract class Typed {

  protected Typed(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }


  public String toString() {
    return getKind() + " " + name + "::" + type;
  }


  protected void add(org.podval.imageio.jaxb.Typed xml) {
    if (xml.getType() != null)
      setType(Type.parse(xml.getType()));
  }


  protected void setType(Type type) {
    if (this.type == null) {
      this.type = type;
      if (!checkType())
        throw new IllegalArgumentException("Wrong type: " + this);
    } else {
      if (this.type != type)
        throw new IllegalArgumentException(
          "Attempt to change type of " + this + " to " + type
        );
    }
  }


  public Type getType() {
    if (type == null)
      return Type.U16;
//      throw new IllegalArgumentException("Type not set: " + this);
    return type;
  }


  protected abstract String getKind();


  protected abstract boolean checkType();


  private final String name;


  private Type type;
}
