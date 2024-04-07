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
package com.ceridwen.selfissue.client.log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.collections.Spooler;
import com.ceridwen.util.collections.Queue;
import java.net.UnknownHostException;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class OnlineLogDevice implements OnlineLog {
  private final Spooler<OnlineLogEvent> spool;
  private final OnlineLogLogger processor;
  private static final long delay = 10000;

  public OnlineLogDevice(String file, OnlineLogLogger processor, int period) throws IOException, ClassNotFoundException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    this.processor = processor;
    Queue<OnlineLogEvent> persistentQueue =  Configuration.getPersistentQueue(file);
    spool = new Spooler<>(persistentQueue, this.processor, delay, period);
  }

  @Override
  public void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request, Message response) throws IOException {
    OnlineLogEvent ev = new OnlineLogEvent();
    ev.setLevel(level);
    ev.setOriginalTransactionTime(originalTransactionTime);
    ev.setLibrary(library);
    ev.setRequest(request);
    ev.setResponse(response);
    ev.setAddInfo(addInfo);
    try {
      ev.setSource(java.net.InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException ex) {

    }
    ev.setTimeStamp(new java.util.Date());
    if (!this.processor.process(ev)) {
      spool.add(ev);
    }
  }
  
  @Override
  public void close() {
    spool.cancelScheduler();
  }
}
