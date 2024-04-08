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

public class ConsoleLogger extends OnlineLogLogger {
    
    @Override
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

    @Override
    public boolean log(OnlineLogEvent event) {      
      return this.sendConsoleMessage(this.getSubject(event), this.getSubjectType(event), this.getMessage(event));
    }
}