package com.ceridwen.selfissue.client.log;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import com.ceridwen.circulation.SIP.messages.Message;

public class OnlineLogManager implements OnlineLog {
  private Vector<OnlineLog> loggers = new Vector<OnlineLog>();


  public void addOnlineLogger(OnlineLog logger) {
    loggers.add(logger);
  }

  public void removeOnlineLogger(OnlineLog logger) {
    loggers.remove(logger);
  }

  public void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request,
                          Message response) {
    Enumeration<OnlineLog> enumerate = loggers.elements();
    while (enumerate.hasMoreElements()) {
      ((OnlineLog)enumerate.nextElement()).recordEvent(level, library, addInfo, originalTransactionTime, request, response);
    }
  }
}
