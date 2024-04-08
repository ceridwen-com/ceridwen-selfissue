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

import java.io.IOException;
import java.net.ServerSocket;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.dialogs.ErrorDialog;
import com.ceridwen.selfissue.client.logging.LoggingHandlerWrapper;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class SelfIssueClient {
  private static final Log log = LogFactory.getLog(SelfIssueClient.class);
  
	private static void checkExistingInstance() {
		final int SOCKET_PORT = 61432;
	try {
        new ServerSocket(SOCKET_PORT);
      }
      catch (IOException ex) {
        ErrorDialog err = new ErrorDialog("SelfIssue Client is already running");
        err.setVisible(true);
        Runtime.getRuntime().halt(200);
      }
	}
      
	private static void initiateLogging() {
    NodeList loggingHandlers = Configuration.getPropertyList("Admin/LoggingHandlers/LoggingHandler");
    Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
    for (int i = 0; i < loggingHandlers.getLength(); i++) {
      LoggingHandlerWrapper loggingHandlerWrapper;
      try {
        loggingHandlerWrapper = (LoggingHandlerWrapper) Class.forName(
          Configuration.getSubProperty(loggingHandlers.item(i), "@class")).
          newInstance();
        Handler handler = loggingHandlerWrapper.getLoggingHandler(loggingHandlers.item(i));
        rootLogger.addHandler(handler);
        if (rootLogger.getLevel().intValue() > handler.getLevel().intValue()) {
          rootLogger.setLevel(handler.getLevel());
        }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SecurityException ex) {
        log.fatal("Could not register logging handler", ex);
      }
    }
	}

	private static void initiateShutdownHooks() {
    Runtime.getRuntime().addShutdownHook(new ShutdownThread());
	}

	private static void startThreads() {
		SelfIssueClientThreadGroup tg;
		SelfIssueThread th;

	    tg = new SelfIssueClientThreadGroup("SelfIssueClientThreadGroup");
	    Thread.setDefaultUncaughtExceptionHandler(tg);
			th = new SelfIssueThread(tg, "SelfIssueClientThread");
	    th.setUncaughtExceptionHandler(tg);
	    th.start();
	  }

	public static void main(String[] args) {
		checkExistingInstance();      
		initiateLogging();
		initiateShutdownHooks();
		startThreads();
	}
}
