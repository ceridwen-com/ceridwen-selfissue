package com.ceridwen.selfissue.client.spooler;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import com.ceridwen.util.SpoolerProcessor;
import com.ceridwen.circulation.SIP.messages.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public interface OfflineSpooler {
  void add(OfflineSpoolObject m);
  int size();
}
