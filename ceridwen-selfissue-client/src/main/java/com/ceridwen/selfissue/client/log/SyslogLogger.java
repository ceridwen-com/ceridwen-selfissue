package com.ceridwen.selfissue.client.log;


import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import com.ceridwen.util.net.Syslog;

public class SyslogLogger extends OnlineLogLogger {
    String host;
    int port;
    
    public void initialise(Node config, OutOfOrderInterface ooo) {
      super.initialise(config, ooo);
      host = Configuration.getSubProperty(config, "Host");
      port = Configuration.getIntSubProperty(config, "Port");      
    }

    public synchronized boolean sendSyslogMessage(String facility, int level, String msg) {
      try {
          Syslog.sendSyslog(host, port, facility, level , msg);
          return true;
      }
      catch (Exception ex) {
        return false;
      }
    }

    public boolean log(OnlineLogEvent event) {      
      return this.sendSyslogMessage("SelfIssue", event.isActionRequired()?Syslog.LOG_ALERT:Syslog.LOG_INFO, this.getMessage(event));
    }
}
