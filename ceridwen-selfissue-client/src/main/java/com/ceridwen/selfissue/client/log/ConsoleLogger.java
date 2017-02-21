package com.ceridwen.selfissue.client.log;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.OutOfOrderInterface;

public class ConsoleLogger extends OnlineLogLogger {
    
    public void initialise(Node config, OutOfOrderInterface ooo) {
      super.initialise(config, ooo);
    }

    public synchronized boolean sendConsoleMessage(String subject, String type, String msg) {
      try {
          System.out.println(subject + "(" + type +"): " +msg);
          return true;
      }
      catch (Exception ex) {
        return false;
      }
    }

    public boolean log(OnlineLogEvent event) {      
      return this.sendConsoleMessage(this.getSubject(event), this.getSubjectType(event), this.getMessage(event));
    }
}