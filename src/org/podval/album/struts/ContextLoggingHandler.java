package org.podval.album.struts;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.ErrorManager;

import javax.servlet.ServletContext;


public class ContextLoggingHandler extends Handler {

  public ContextLoggingHandler(ServletContext context) {
    this.context = context;
  }


  public synchronized void publish(LogRecord record) {
    if (!isLoggable(record)) {
        return;
    }

    String msg;
    try {
      msg = getFormatter().format(record);
    } catch (Exception ex) {
      // We don't want to throw an exception here, but we
      // report the exception to any registered ErrorManager.
      reportError(null, ex, ErrorManager.FORMAT_FAILURE);
      return;
    }

    try {
      context.log(msg);
    } catch (Exception ex) {
      // We don't want to throw an exception here, but we
      // report the exception to any registered ErrorManager.
      reportError(null, ex, ErrorManager.WRITE_FAILURE);
    }
  }


  public void flush() {
  }


  public void close() {
    // context = null;
  }


  private ServletContext context;
}
