package com.ceridwen.selfissue.client.log;

import java.util.Enumeration;
import java.util.Vector;

import com.ceridwen.circulation.SIP.messages.Message;

public class OnlineLogManager implements OnlineLog {
  private Vector loggers = new Vector();


  public void addOnlineLogger(OnlineLog logger) {
    loggers.add(logger);
  }

  public void removeOnlineLogger(OnlineLog logger) {
    loggers.remove(logger);
  }

  public void recordEvent(int level, String library, String addInfo, Message request,
                          Message response) {
    Enumeration enumerate = loggers.elements();
    while (enumerate.hasMoreElements()) {
      ((OnlineLog)enumerate.nextElement()).recordEvent(level, library, addInfo, request, response);
    }
  }
}
