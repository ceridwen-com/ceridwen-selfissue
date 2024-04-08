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
package com.ceridwen.selfissue.client;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import com.ceridwen.selfissue.client.config.Configuration;

class SelfIssueClientThreadGroup extends ThreadGroup {
  private static boolean ooo = Configuration.getBoolProperty("Admin/WatchDog/ShowOutOfOrderScreenOnUnhandledException"); 
  private static Log log = LogFactory.getLog(SelfIssueClientThreadGroup.class);
  private static SelfIssueFrame frame = null;

  public SelfIssueClientThreadGroup(String name) {
    super(name);
  }

  public SelfIssueClientThreadGroup(ThreadGroup parent, String name) {
    super(parent, name);
  }
  
  public void setFrame(SelfIssueFrame f)
  {
	  frame = f;
  }

  public void uncaughtException(Thread t, Throwable e) {
	try {
	    log.fatal("Uncaught exception:", e);    
	    if (ooo && frame != null)
	    {
	    	frame.setOutOfOrderPanel();
	    }
	} catch (Exception ex) {		
	}
  }
}
