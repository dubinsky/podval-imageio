package org.podval.album;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import java.io.File;


public class Context implements ServletContextListener {

  public void contextInitialized(ServletContextEvent e) {
    servletContext = e.getServletContext();

    String originalsRoot = servletContext.getInitParameter("originalsRoot");
    String metadataRoot = servletContext.getInitParameter("metadataRoot");
    String generatedRoot = servletContext.getInitParameter("generatedRoot");

    Album.setRoot(originalsRoot, metadataRoot, generatedRoot);
  }


  public void contextDestroyed(ServletContextEvent e) {
  }


  private static ServletContext servletContext;
}
