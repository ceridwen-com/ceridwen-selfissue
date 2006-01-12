package com.ceridwen.selfissue.client.nulldevices;

import com.ceridwen.selfissue.client.log.OnlineLogEvent;


/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class OnlineLogLogger extends com.ceridwen.selfissue.client.log.OnlineLogLogger {
  public boolean log(OnlineLogEvent event) {
    return true;
  }
}
