package com.ceridwen.selfissue.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.ACSStatus;
import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.messages.SCStatus;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.security.SecurityDevice;
import com.ceridwen.selfissue.client.config.Configuration;

public class ShutdownThread extends Thread {
  private static SecurityDevice device = null;

  private static Log log = LogFactory.getLog(ShutdownThread.class);
  private Connection conn;

  public ShutdownThread() {
    super();
  }

  public static void registerSecurityDeviceShutdown(SecurityDevice d) {
    device = d;
  }

  public static void shutdownSecurityDevice() {
    synchronized (device) {
      device.stop();
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
    if (!Configuration.getBoolProperty("Modes/SendShutdownStatus")) {
      return null;
    }

    if (!this.connect()) {
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
    log.error("Shutting Down Self Issue Terminal");
    SelfIssueClient.deregister();
    shutdownSecurityDevice();
    System.out.println("Shutting Down Self Issue Terminal...");
    this.sendShutdownStatus();
  }
}
