package com.ceridwen.selfissue.client.log;

import com.ceridwen.util.*;
import com.ceridwen.circulation.SIP.messages.*;
import java.io.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class OnlineLogDevice implements OnlineLog {
  private Spooler spool;
  private OnlineLogLogger processor;

  public OnlineLogDevice(File file, OnlineLogLogger processor, int period) {
    this.processor = processor;
    spool = new Spooler(new PersistentQueue(file), this.processor, period);
  }

  public void recordEvent(int level, String library, String addInfo, Message request, Message response) {
    OnlineLogEvent ev = new OnlineLogEvent();
    ev.setLevel(level);
    ev.setLibrary(library);
    ev.setRequest(request);
    ev.setResponse(response);
    ev.setAddInfo(addInfo);
    try {
      ev.setSource(java.net.InetAddress.getLocalHost().getHostName());
    } catch (Exception ex) {

    }
    ev.setTimeStamp(new java.util.Date().toString());
    if (!this.processor.process(ev)) {
      spool.add(ev);
    }
  }
  protected void finalize() throws java.lang.Throwable {
    spool.cancelScheduler();
  }
}
