package org.podval.album.struts;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

import org.podval.album.Album;


public class Context implements ServletContextListener {

  public void contextInitialized(ServletContextEvent e) {
    servletContext = e.getServletContext();

    Level level = Level.FINE;

    Logger logger = Logger.getLogger("org.podval.album.Picture");
    logger.setLevel(level);

    Handler handler = new ContextLoggingHandler(servletContext);
    handler.setFormatter(new SimpleFormatter());
    handler.setLevel(level);
    logger.addHandler(handler);

    Album.setRoot(servletContext.getInitParameter("AlbumRoot"));
  }


  public void contextDestroyed(ServletContextEvent e) {
  }


  private static ServletContext servletContext;
}
