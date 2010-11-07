/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client;

import java.io.IOException;
import java.net.ServerSocket;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;

import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.transport.TelnetConnection;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.dialogs.ErrorDialog;
import com.ceridwen.selfissue.client.logging.LoggingHandlerWrapper;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class SelfIssueClient extends Thread {
  private static Log log = LogFactory.getLog(SelfIssueClient.class);
  private final boolean packFrame = false;
  private SelfIssueFrame frame;
  private static SelfIssueClientThreadGroup tg;
  private static SelfIssueClient th;
  

//  private SelfIssueClient() {
//  }

  private static int criticalSectionCounter = 0;

  public static void EnterCriticalSection() {
    criticalSectionCounter++;
  }

  public static void LeaveCriticalSection() {
    criticalSectionCounter--;
    if (criticalSectionCounter < 1) {
      criticalSectionCounter = 0;
    }
  }

  public static boolean isInCriticalSection() {
    return (criticalSectionCounter > 0);
  }

  public static Connection ConfigureConnection() {
    Connection conn;

    if (Configuration.getProperty("Systems/SIP/@mode").equalsIgnoreCase("Socket")) {
      conn = new SocketConnection();
    } else {
      conn = new TelnetConnection();
      ((TelnetConnection)conn).setUsername(Configuration.getProperty("Systems/SIP/TelnetUsername"));
      ((TelnetConnection)conn).setPassword(Configuration.Decrypt(Configuration.getProperty("Systems/SIP/TelnetPassword")));
      ((TelnetConnection)conn).setLoggedOnText(Configuration.getProperty("Systems/SIP/LoggedOnText"));
    }

    conn.setHost(Configuration.getProperty("Systems/SIP/Host"));
    conn.setPort(Configuration.getIntProperty("Systems/SIP/Port"));
    conn.setConnectionTimeout(Configuration.getIntProperty("Systems/SIP/ConnectionTimeout") * 1000);
    conn.setIdleTimeout(Configuration.getIntProperty("Systems/SIP/IdleTimeout") * 1000);
    conn.setRetryAttempts(Configuration.getIntProperty("Systems/SIP/RetryAttempts"));
    conn.setRetryWait(Configuration.getIntProperty("Systems/SIP/RetryWait"));
    conn.setAddSequenceAndChecksum(Configuration.getBoolProperty("Systems/SIP/AddSequenceAndChecksum"));
    conn.setStrictChecksumChecking(Configuration.getBoolProperty("Systems/SIP/StrictChecksumChecking"));
    conn.setStrictSequenceChecking(Configuration.getBoolProperty("Systems/SIP/StrictSequenceChecking"));
    return conn;
  }




  //Construct the application
  public SelfIssueClient(ThreadGroup parent, String name) {
    super(parent, name);
  }
  
  private void StartUp() {
	frame = new SelfIssueFrame();

    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
        
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
        
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    frame.setVisible(true);
    tg.setFrame(frame);
  }
  //Main method

  private static final int SOCKET_PORT = 61432;

  public void run() {
    int WatchDogThreshold = Configuration.getIntProperty("UI/WatchDog/CriticalSectionThreshold");
    int WatchDogTimer = Configuration.getIntProperty("UI/WatchDog/Timer") * 1000;
    int WatchDogMinimumMemory = Configuration.getIntProperty("UI/WatchDog/MinimumMemory");
    boolean ooo = Configuration.getBoolProperty("UI/WatchDog/ShowOutOfOrderScreenOnWatchDogProblem");
    final int WatchDogMaximumErrors = 5;

    try {
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	} catch (Exception ex) {    		
    	}
    	try {
    	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    	        if ("Nimbus".equals(info.getName())) {
    	            UIManager.setLookAndFeel(info.getClassName());
    	            break;
    	        }
    	    }
    	} catch (UnsupportedLookAndFeelException e) {
    	} catch (ClassNotFoundException e) {
    	} catch (InstantiationException e) {
    	} catch (IllegalAccessException e) {
    	}    	   	
      /**TODO: reinstate this optionally?
       *       com.ceridwen.util.synchronicity.TimeCheck check = new com.ceridwen.util.synchronicity.TimeCheck(24, 120, "ntp0.oucs.ox.ac.uk");
       */

      this.StartUp();
      int watchdogErrors = 0;
      int watchdog = WatchDogThreshold;

      while (true) {
        if (SelfIssueClient.isInCriticalSection()) {
          watchdog--;
          if (watchdog < 1) {
            if (watchdogErrors < WatchDogMaximumErrors) {
              log.error("Watchdog timeout in critical section");
              watchdogErrors++;
            } else {
            	if (ooo) {
            		frame.setOutOfOrderPanel();
            	}
            }
            watchdog = 0;
          }
        } else {
          watchdog = WatchDogThreshold;
          watchdogErrors = 0;
        }

        Runtime runtime = Runtime.getRuntime();
        long availableMemory = (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()))/(1024*1024);
        if (availableMemory < WatchDogMinimumMemory) {
          log.fatal("Low Memory: " + availableMemory);
      	  if (ooo) {
    		frame.setOutOfOrderPanel();
    	  }
        }

        try {
          sleep(WatchDogTimer);
        } catch (Exception ex) {
          log.debug("Sleep failure: ", ex);
        }
      }
    }
    catch(Exception e) {
      log.fatal("Main thread failure: ", e);
    }
  }

  public static void main(String[] args) {
      try {
        new ServerSocket(SOCKET_PORT);
      }
      catch (IOException ex) {
        ErrorDialog err = new ErrorDialog("SelfIssue Client is already running");
        err.setVisible(true);
        Runtime.getRuntime().halt(200);
      }
      
    NodeList loggingHandlers = Configuration.getPropertyList("Logging/LoggingHandler");
    for (int i = 0; i < loggingHandlers.getLength(); i++) {
          LoggingHandlerWrapper loggingHandlerWrapper;
          try {
              loggingHandlerWrapper = (LoggingHandlerWrapper) Class.forName(
                Configuration.getSubProperty(loggingHandlers.item(i), "@class")).
                newInstance();
              java.util.logging.LogManager.getLogManager().getLogger("").addHandler(loggingHandlerWrapper.getLoggingHandler(loggingHandlers.item(i)));
          } catch (Exception ex) {
              log.fatal("Could not register logging handler", ex);
          }
    }

    Runtime.getRuntime().addShutdownHook(new ShutdownThread());

    tg = new SelfIssueClientThreadGroup("SelfIssueClientThreadGroup");
    Thread.setDefaultUncaughtExceptionHandler(tg);
    th = new SelfIssueClient(tg, "SelfIssueClientThread");
    th.setUncaughtExceptionHandler(tg);
    th.start();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SelfIssueClient.class);
  }
}
