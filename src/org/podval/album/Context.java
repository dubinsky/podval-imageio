package org.podval.album;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import java.io.File;


public class Context implements ServletContextListener {

  public void contextInitialized(ServletContextEvent e) {
    servletContext = e.getServletContext();
    String originalsDirectory = servletContext.getInitParameter("originalsDirectory");
    String generatedDirectory = servletContext.getInitParameter("generatedDirectory");
    /** @todo check that directories exist (and have proper permissions) - or else what? */
    root = new PictureDirectory("", new File(originalsDirectory), new File(generatedDirectory));


    /** @todo should register using jar manifest! */
    javax.imageio.spi.IIORegistry.getDefaultInstance().registerServiceProvider(new
      org.podval.imageio.CiffImageReaderSpi());

    /** @todo where is the right place for this? */
    org.podval.imageio.MetaMetadata.load();
  }


  public void contextDestroyed(ServletContextEvent e) {
  }


  public static void log(String what) {
    servletContext.log(what);
  }


  public static PictureDirectory getRoot() {
    return root;
  }


  private static ServletContext servletContext;


  private static PictureDirectory root;
}
