package com.ceridwen.selfissue.client.log;

import com.ceridwen.circulation.SIP.messages.Message;
import java.util.*;

public class OnlineLogManager implements OnlineLog {
  Vector loggers = new Vector();


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
