package com.ceridwen.selfissue.client.log;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.util.*;

public interface OnlineLog {
  void recordEvent(int level, String library, String addInfo, Message request, Message response);
}
