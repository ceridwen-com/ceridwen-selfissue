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
package com.ceridwen.selfissue.client.log;


import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import com.ceridwen.util.net.Syslog;
import java.io.IOException;

public class SyslogLogger extends OnlineLogLogger {
    
    @Override
    public void initialise(Node config, OutOfOrderInterface ooo) {
      super.initialise(config, ooo);    
    }

    public synchronized boolean sendSyslogMessage(String facility, int level, String msg) {
      try {
          Syslog.sendSyslog(host, port, facility, level , msg);
          return true;
      }
      catch (IOException ex) {
        return false;
      }
    }

    @Override
    public boolean log(OnlineLogEvent event) {      
      return this.sendSyslogMessage(source.isBlank()?"SelfIssue":source, event.isActionRequired()?Syslog.LOG_ALERT:Syslog.LOG_INFO, this.getMessage(event));
    }
}
