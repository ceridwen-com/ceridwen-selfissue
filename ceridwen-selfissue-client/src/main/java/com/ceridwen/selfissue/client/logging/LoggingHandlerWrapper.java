package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;
import java.util.logging.Level;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

public abstract class LoggingHandlerWrapper {
    public abstract Handler getLoggingHandler(Node item);
    
    protected Level getLevel(Node item) {
        if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("SEVERE")) {
            return java.util.logging.Level.SEVERE;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("WARNING")) {
            return java.util.logging.Level.WARNING;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("INFO")) {
            return java.util.logging.Level.INFO;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("CONFIG")) {
            return java.util.logging.Level.CONFIG;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("FINE")) {
            return java.util.logging.Level.FINE;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("FINER")) {
            return java.util.logging.Level.FINER;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("FINEST")) {
            return java.util.logging.Level.FINEST;
          } else if (Configuration.getSubProperty(item, "level").equalsIgnoreCase("ALL")) {
            return java.util.logging.Level.ALL;
          } else {
            return java.util.logging.Level.OFF;
          }
    }

}
