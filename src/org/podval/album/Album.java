package org.podval.album;

import java.util.Collection;

import java.util.StringTokenizer;

import java.io.File;
import java.io.IOException;

import org.podval.imageio.MetaMetadata;


/**
 */

public abstract class Album {

  public static void setRoot(String albumRoot) {
    /** @todo check that directories exist (and have proper permissions) - or else what? */
    root = new AlbumLocal(new File(albumRoot));

    MetaMetadata.init();
  }


  public static Album getByPath(String path) {
    if (path == null)
      throw new NullPointerException("Path is null!");

    if (!path.startsWith("/"))
      throw new IllegalArgumentException("Path does not start with '/': " + path);

    Album result = getRoot();

    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    while (tokenizer.hasMoreTokens() && (result != null))
      result = ((Album) result).getSubalbum(tokenizer.nextToken());

    return result;
  }


  private static Album getRoot() {
    if (root == null)
      throw new NullPointerException("Root is not set!");

    return root;
  }


  private static Album root;


  protected Album() {
    this.parent = null;
    this.name = "";
  }


  protected Album(AlbumLocal parent, String name) {
    if (parent == null)
      throw new NullPointerException("Parent is null.");

    if (name == null)
      throw new NullPointerException("Name is null.");

    if (name.equals(""))
      throw new IllegalArgumentException("Name is empty.");

    this.parent = parent;
    this.name = name;
  }


  public final AlbumLocal getParent() {
    return parent;
  }


  public final String getName() {
    return name;
  }


  public final void setTitle(String value) {
    loadMetadata();
    if (((title == null) && (value != null)) || !title.equals(value)) {
      title = value;
      metadataChanged();
    }
  }


  public final String getTitle() {
    loadMetadata();
    return (title != null) ? title : getDefaultTitle();
  }


  public abstract String getPath();


  protected abstract String getDefaultTitle();


  public abstract int getNumSubalbums();


  public abstract Album getSubalbum(String name);


  public abstract Collection getSubalbums();


  public abstract int getNumPictures();


  public abstract Picture getPicture(String name);


  public abstract Collection getPictures();


  protected abstract void loadMetadata();


  protected abstract void metadataChanged();


  private final AlbumLocal parent;


  private final String name;


  protected String title;
}
