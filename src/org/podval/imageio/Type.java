package org.podval.imageio;

import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;


public class Type {

  private static Type type(String name, int length) {
    return new Type(name, length);
  }


  private static Type selfType(String name, int length) {
    Type result = type(name, length);
    result.setAllowedFieldTypes(new Type[] {});
    return result;
  }


  private static void register(Type type) {
    types.put(type.name, type);
  }


  private static final Map types = new HashMap();


  private Type(String name, int length) {
    this.name = name;
    this.length = length;

    register(this);
  }


  private final String name;
  private final int length;
  private boolean isVariableLength;
  private boolean isDirectoryAllowed = false;
  private boolean isVectorAllowed = false;
  private boolean isEnumerationAllowed = false;
  private Type[] allowedFieldTypes;
  private Type[] allowedSubfieldTypes = null;
  private Collection actualTypes = null;


  public String toString() {
    return name;
  }


  public String getName() {
    return name;
  }


  public int getLength() {
    assert (length > 0) : "Undefined length for type " + name;
    return length;
  }


  private void setVariableLength() {
    this.isVariableLength = true;
  }


  public boolean isVariableLength() {
    return isVariableLength;
  }


  private void setDirectoryAllowed() {
    this.isDirectoryAllowed = true;
  }


  public boolean isDirectoryAllowed() {
    return isDirectoryAllowed;
  }


  private void setAllowedFieldTypes(Type[] allowedFieldTypes) {
    this.allowedFieldTypes = allowedFieldTypes;
  }


  public boolean isRecordAllowed() {
    return (allowedFieldTypes != null);
  }


  private void setVectorAllowed() {
    this.isVectorAllowed = true;
  }


  public boolean isVectorAllowed() {
    return isVectorAllowed;
  }


  public boolean isFieldAllowed(Type type) {
    return (allowedFieldTypes != null) &&
      (((allowedFieldTypes.length == 0) && (type == this)) ||
      find(type, allowedFieldTypes));
  }


  public Type getDefaultFieldType() {
    assert (allowedFieldTypes != null) : "No default field type for non-record types.";
    return (allowedFieldTypes.length == 0) ? this : allowedFieldTypes[0];
  }


  private void setAllowedSubfieldTypes(Type[] allowedSubfieldTypes) {
    assert (allowedSubfieldTypes == null) || (allowedSubfieldTypes.length != 0)
      : "List of allowed subfield types can not be empty!";

    this.allowedSubfieldTypes = allowedSubfieldTypes;
  }


  public boolean isSubfieldAllowed(Type type) {
    return find(type, allowedSubfieldTypes);
  }


  private void setEnumerationAllowed() {
    this.isEnumerationAllowed = true;
  }


  public boolean isEnumerationAllowed() {
    return isEnumerationAllowed;
  }


  private void setActualTypes(Type[] actualTypes) {
    this.actualTypes = toCollection(actualTypes);
  }


  public Collection getActualTypes() {
    if (actualTypes==null) {
      actualTypes = toCollection(new Type[] { this });
    }

    return actualTypes;
  }


  private Collection toCollection(Type[] types) {
    return Collections.unmodifiableCollection(Arrays.asList(types));
  }


  private boolean find(Type type, Type[] list) {
    boolean result = false;
    if (list != null) {
      for (int i = 0; i<list.length; i++) {
        if (list[i]==type) {
          result = true;
          break;
        }
      }
    }
    return result;
  }


  public static final Type U32 = selfType("U32", 4);
  public static final Type S32 = type("S32", 4);
  public static final Type F32 = type("F32", 4);
  public static final Type U16 = type("U16", 2);
  public static final Type S16 = type("S16", 2);
  public static final Type U8 = selfType("U8", 1);
  public static final Type X8 = selfType("X8", 1);
  public static final Type X8_STRING = selfType("X8-string", 1);
  public static final Type STRING = selfType("string", 1);
  public static final Type RATIONAL = selfType("rational", 8);
  public static final Type SRATIONAL = selfType("signedRational", 8);
  public static final Type U16_OR_U32 = selfType("U16orU32", 0);
  public static final Type ONE = type("one", 0);
  public static final Type TWO = type("two", 0);


  static {
    U8.setVariableLength();
    X8.setVariableLength();
    X8_STRING.setVariableLength();
    STRING.setVariableLength();

    U32.setDirectoryAllowed();
    ONE.setDirectoryAllowed();
    TWO.setDirectoryAllowed();

    X8_STRING.setActualTypes(new Type[] { X8 });
    U16_OR_U32.setActualTypes(new Type[] { U16, U32 });

    U32.setAllowedFieldTypes(new Type[] { U32, S32, F32 });
    U16.setAllowedFieldTypes(new Type[] { U16, S16 });

    U32.setAllowedSubfieldTypes(new Type[] { U16, U8 });

    U16.setVectorAllowed();

    U16.setEnumerationAllowed();
    U8.setEnumerationAllowed();
    X8.setEnumerationAllowed();
  }


  public static Type parse(String name) {
    Type result = (Type) types.get(name);
    assert (result != null) : "Unrecognized type " + name;
    return result;
  }
}
