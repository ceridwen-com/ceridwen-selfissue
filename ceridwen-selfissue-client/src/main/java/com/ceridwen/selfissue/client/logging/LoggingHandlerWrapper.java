/* 
 * Copyright (C) 2019 Ceridwen Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;
import java.util.logging.Level;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

public abstract class LoggingHandlerWrapper {
    public abstract Handler getLoggingHandler(Node config);
    
    protected Level getLevel(Node config) {
        if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("SEVERE")) {
            return java.util.logging.Level.SEVERE;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("WARNING")) {
            return java.util.logging.Level.WARNING;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("INFO")) {
            return java.util.logging.Level.INFO;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("CONFIG")) {
            return java.util.logging.Level.CONFIG;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("FINE")) {
            return java.util.logging.Level.FINE;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("FINER")) {
            return java.util.logging.Level.FINER;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("FINEST")) {
            return java.util.logging.Level.FINEST;
          } else if (Configuration.getSubProperty(config, "level").equalsIgnoreCase("ALL")) {
            return java.util.logging.Level.ALL;
          } else {
            return java.util.logging.Level.OFF;
          }
    }
    
    protected String getHost(Node config) {
        return Configuration.getSubProperty(config, "Host");
    }
    
    protected int getPort(Node config) {
        return Configuration.getIntSubProperty(config, "Port");
    }
    
    protected boolean getSSL(Node config) {
        return Configuration.getBoolSubProperty(config, "SSL");
    }
    
    protected String getTarget(Node config) {
        return Configuration.getSubProperty(config, "Target");
    }
    
    protected String getSource(Node config) {
        return Configuration.getSubProperty(config, "Source");
    }
    
    protected int getConnectionTimeout(Node config) {
        return Configuration.getIntSubProperty(config, "ConnectionTimeout");
    }
    
    protected int getIdleTimeout(Node config) {
        return Configuration.getIntSubProperty(config, "IdleTimeout");  
    }

}
