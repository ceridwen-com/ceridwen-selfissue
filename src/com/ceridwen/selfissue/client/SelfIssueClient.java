package com.ceridwen.selfissue.client;

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.awt.*;
import org.apache.commons.logging.*;
import com.ceridwen.circulation.SIP.transport.*;
import com.ceridwen.circulation.SIP.messages.*;
import java.util.Vector;
import java.io.*;
import java.util.*;
import com.ceridwen.circulation.security.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

class SelfIssueClientThreadGroup extends ThreadGroup {
  private static Log log = LogFactory.getLog(SelfIssueClientThreadGroup.class);

  public SelfIssueClientThreadGroup(String name) {
    super(name);
  }

  public SelfIssueClientThreadGroup(ThreadGroup parent, String name) {
    super(parent, name);
  }

  public void uncaughtException(Thread t, Throwable e) {
    log.fatal("Uncaught exception:", e);
  }
}

class ShutdownThread extends Thread {
  private static SecurityDevice device = null;

  private static Log log = LogFactory.getLog(ShutdownThread.class);
  Connection conn;

  public ShutdownThread() {
    super();
  }

  public static void registerSecurityDeviceShutdown(SecurityDevice d) {
    device = d;
  }

  public static void shutdownSecurityDevice() {
    synchronized (device) {
      device.deinit();
      device = null;
    }
  }

  private boolean connect() {
    conn = SelfIssueClient.ConfigureConnection();
    return conn.connect();
  }

  private void disconnect() {
    conn.disconnect();
  }

  private Message sendShutdownStatus() {
    if (!this.connect()) {
      return null;
    }

    if (!Configuration.getBoolProperty("Modes/SendShutdownStatus")) {
      return null;
    }

    SCStatus scstatus = new SCStatus();
    scstatus.setProtocolVersion("2.00");
    scstatus.setStatusCode("2");
    try {
      ACSStatus ascstatus = (ACSStatus) conn.send(scstatus);
      this.disconnect();
      return ascstatus;
    } catch (Exception ex) {
      this.disconnect();
      return null;
    }
  }


  public void run() {
    shutdownSecurityDevice();
    System.out.println("Shutting Down Self Issue Terminal...");
    this.sendShutdownStatus();
    log.info("Shutting Down Self Issue Terminal");
  }
}

public class SelfIssueClient extends Thread {
  private static Log log = LogFactory.getLog(SelfIssueClient.class);
  boolean packFrame = false;

  private SelfIssueClient() {
  }

  private static int criticalSectionCounter = 0;

  public static void EnterCriticalSection() {
    criticalSectionCounter++;
  }

  public static void LeaveCriticalSection() {
    criticalSectionCounter--;
    if (criticalSectionCounter < 1)
      criticalSectionCounter = 0;
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
      ((TelnetConnection)conn).setUsername(Configuration.getProperty("Systems/SIP/Username"));
      ((TelnetConnection)conn).setPassword(Configuration.Decrypt(Configuration.getProperty("Systems/SIP/Password")));
      ((TelnetConnection)conn).setLoggedOnText(Configuration.getProperty("Systems/SIP/LoggedOnText"));
    }

    conn.setHost(Configuration.getProperty("Systems/SIP/Host"));
    conn.setPort(Configuration.getIntProperty("Systems/SIP/Port"));
    conn.setConnectionTimeout(Configuration.getIntProperty("Systems/SIP/ConnectionTimeout") * 1000);
    conn.setIdleTimeout(Configuration.getIntProperty("Systems/SIP/IdleTimeout") * 1000);
    conn.setRetryAttempts(Configuration.getIntProperty("Systems/SIP/RetryAttempts"));
    conn.setRetryWait(Configuration.getIntProperty("Systems/SIP/RetryWait"));
    return conn;
  }




  //Construct the application
  public SelfIssueClient(ThreadGroup parent, String name) {
    super(parent, name);
  }

  private void StartUp() {
    SelfIssueFrame frame = new SelfIssueFrame();

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
  }
  //Main method
  public static void main(String[] args) {
    /**@todo: Read configuration
     *
     */

    java.util.logging.Handler handler = new com.ceridwen.util.logging.SMTPLogHandler(
      Configuration.getProperty("Logging/SMTPHandler/smtpServer"),
      Configuration.getProperty("Logging/SMTPHandler/recipients"));
    if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("SEVERE")) {
      handler.setLevel(java.util.logging.Level.SEVERE);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("WARNING")) {
      handler.setLevel(java.util.logging.Level.WARNING);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("INFO")) {
      handler.setLevel(java.util.logging.Level.INFO);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("CONFIG")) {
      handler.setLevel(java.util.logging.Level.CONFIG);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("FINE")) {
      handler.setLevel(java.util.logging.Level.FINE);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("FINER")) {
      handler.setLevel(java.util.logging.Level.FINER);
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("FINEST")) {
      handler.setLevel(java.util.logging.Level.FINEST);
    } else {
      handler.setLevel(java.util.logging.Level.OFF);
    }
    java.util.logging.LogManager.getLogManager().getLogger("").addHandler(handler);

    Runtime.getRuntime().addShutdownHook(new ShutdownThread());

    SelfIssueClientThreadGroup tg = new SelfIssueClientThreadGroup("SelfIssueClientThreadGroup");
    SelfIssueClient th = new SelfIssueClient(tg, "SelfIssueClientThread");
    th.start();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SelfIssueClient.class);
  }

  public void run() {
    int WatchDogThreshold = Configuration.getIntProperty("UI/WatchDog/CriticalSectionThreshold");
    int WatchDogTimer = Configuration.getIntProperty("UI/WatchDog/Timer") * 1000;
    int WatchDogMinimumMemory = Configuration.getIntProperty("UI/WatchDog/MinimumMemory");

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//      UIManager.setLookAndFeel(new napkin.NapkinLookAndFeel());
      /**@todo: reinstate this optionally?
       *       com.ceridwen.util.synchronicity.TimeCheck check = new com.ceridwen.util.synchronicity.TimeCheck(24, 120, "ntp0.oucs.ox.ac.uk");
       */
      this.StartUp();
      int watchdog = WatchDogThreshold;

      while (true) {
        if (SelfIssueClient.isInCriticalSection()) {
          watchdog--;
          if (watchdog < 1) {
            log.error("Watchdog timeout in critical section");
            watchdog = 0;
          }
        } else {
          watchdog = WatchDogThreshold;
        }

        Runtime runtime = Runtime.getRuntime();
        long availableMemory = (runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory()))/(1024*1024);
        if (availableMemory < WatchDogMinimumMemory) {
          log.fatal("Low Memory: " + availableMemory);
        }

        try {
          this.sleep(WatchDogTimer);
        } catch (Exception ex) {
          log.debug("Sleep failure: ", ex);
        }
      }
    }
    catch(Exception e) {
      log.fatal("Main thread failure: ", e);
    }
  }
}
