package com.ceridwen.selfissue.client.panels;

import java.util.EventObject;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import com.ceridwen.circulation.SIP.messages.Message;

public class SelfIssuePanelEvent extends EventObject {
  public Message request;
  public Message response;
  public Class nextPanel;

  public SelfIssuePanelEvent(Object source, Class nextPanel) {
    super(source);
    this.nextPanel = nextPanel;
  }
}
