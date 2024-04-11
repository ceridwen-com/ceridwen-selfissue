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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Formatter;

public abstract class LoggingHandlerWrapper {
    
    public Handler getLoggingHandler(Node config) throws IOException{
        Handler handler = this.getLoggingHandlerInstance(config);
        this.ConfigureHandler(config, handler);
        return handler;
    }
    
    protected abstract Handler getLoggingHandlerInstance(Node config) throws IOException;
    
    private Level getLevel(Node config) {
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
    
    protected int getPort(Node config, int d) {
        return Configuration.getIntSubProperty(config, "Port", d);
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
    
    protected String getUsername(Node config) {
        return Configuration.getSubProperty(config, "Username");
    }

    protected String getPassword(Node config) {
        return Configuration.Decrypt(Configuration.getSubProperty(config, "Password"));
    } 
    
    protected int getConnectionTimeout(Node config) {
        return 1000*Configuration.getIntSubProperty(config, "ConnectionTimeout", 1);
    }
    
    protected int getIdleTimeout(Node config) {
        return 1000*Configuration.getIntSubProperty(config, "IdleTimeout", 5);  
    }

    
    private void setFormatter(Node config, Handler handler) {
        String clazzName = Configuration.getSubProperty(config, "Formatter");
        try {
            if (clazzName != null && !clazzName.isBlank()) {
                Formatter f = (Formatter) Class.forName(clazzName).
                getDeclaredConstructor().newInstance();
                handler.setFormatter(f);
            }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SecurityException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException ex) {
      }        
    }
    
    private void setEncoding(Node config, Handler handler) {
        String en = Configuration.getSubProperty(config, "Encoding"); 
        try {
            if (en != null && !en.isBlank()) {
                 handler.setEncoding(en);
            }
        } catch (SecurityException | UnsupportedEncodingException ex) {            
        } 
    }
    
    private void ConfigureHandler(Node config, Handler handler) {
        handler.setLevel(this.getLevel(config));
        this.setEncoding(config, handler);
        this.setFormatter(config, handler);
    }
}
