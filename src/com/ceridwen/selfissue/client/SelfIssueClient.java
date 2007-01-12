package com.ceridwen.selfissue.client;

import java.io.IOException;
import java.net.ServerSocket;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.transport.TelnetConnection;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.dialogs.ErrorDialog;
import java.net.URLConnection;
import java.net.InetAddress;
import java.net.*;

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

  private SelfIssueClient() {
  }

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

  private static ServerSocket SingleInstance = null;
  private static final int SOCKET_PORT = 61432;

  public void run() {
    int WatchDogThreshold = Configuration.getIntProperty("UI/WatchDog/CriticalSectionThreshold");
    int WatchDogTimer = Configuration.getIntProperty("UI/WatchDog/Timer") * 1000;
    int WatchDogMinimumMemory = Configuration.getIntProperty("UI/WatchDog/MinimumMemory");
    final int WatchDogMaximumErrors = 5;

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//      UIManager.setLookAndFeel(new napkin.NapkinLookAndFeel());
      /**@todo: reinstate this optionally?
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

  public static void register() {
    String name="";
    String jmx="";
    String address = "";
    String hostname = "unknown";

    try {
      hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
    } catch (Exception ex) {
    }

    InetAddress addr = null;
    try {
      addr = InetAddress.getLocalHost();
      byte[] ip = addr.getAddress();
      for (int i = 0; i < ip.length; i++) {
        address += Integer.toString((int)(ip[i] & 0x00ff));
        if (i < (ip.length-1)) {
          address += ".";
        }
      }
    } catch (UnknownHostException ex1) {
    }
    String port = System.getProperty("com.sun.management.jmxremote.port");
    jmx = "service:jmx:rmi:///jndi/rmi://" + address + ":" + port + "/jmxrmi";
    name = Configuration.getProperty("UI/SelfIssue/LibraryText_Text") + " (" + hostname + ")";

    try {
      jmx = URLEncoder.encode(jmx, "UTF-8");
      name = URLEncoder.encode(name, "UTF-8");
      URLConnection c = new java.net.URL("http://127.0.0.1:8080/mobimon/MMServlet?register=" + name + "&jmx=" + jmx).openConnection();
        c.connect();
        c.getContent();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (IllegalArgumentException ille) {
      ille.printStackTrace();
    } finally {
    }
}

  public static void deregister() {
    String name="";
    String hostname = "unknown";

    try {
      hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
    } catch (Exception ex) {
    }

    name = Configuration.getProperty("UI/SelfIssue/LibraryText_Text") + " (" + hostname + ")";

    try {
      name = URLEncoder.encode(name, "UTF-8");
      URLConnection c = new java.net.URL("http://127.0.0.1:8080/mobimon/MMServlet?deregister=" + name).openConnection();
        c.connect();
        c.getContent();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (IllegalArgumentException ille) {
      ille.printStackTrace();
    } finally {
    }
}


  public static void main(String[] args) {
      try {
        SingleInstance = new ServerSocket(SOCKET_PORT);
      }
      catch (IOException ex) {
        ErrorDialog err = new ErrorDialog("SelfIssue Client is already running");
        err.setVisible(true);
        Runtime.getRuntime().halt(200);
      }

    register();

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
    } else if (Configuration.getProperty("Logging/SMTPHandler/level").equalsIgnoreCase("ALL")) {
      handler.setLevel(java.util.logging.Level.ALL);
    } else {
      handler.setLevel(java.util.logging.Level.OFF);
    }
    java.util.logging.LogManager.getLogManager().getLogger("").addHandler(handler);

    try {
      java.util.logging.LogManager.getLogManager().getLogger("").addHandler((java.util.logging.Handler)Class.forName("com.ceridwen.util.logging.JMXLogHandler").newInstance());
    } catch (Exception ex) {
      log.warn("Could not initialise logging management", ex);
    } catch (java.lang.NoClassDefFoundError ex) {
      log.warn("Could not initialise logging management", ex);
    }

    Runtime.getRuntime().addShutdownHook(new ShutdownThread());

    SelfIssueClientThreadGroup tg = new SelfIssueClientThreadGroup("SelfIssueClientThreadGroup");
    SelfIssueClient th = new SelfIssueClient(tg, "SelfIssueClientThread");
    th.start();
  }

  static {
    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(SelfIssueClient.class);
  }
}
