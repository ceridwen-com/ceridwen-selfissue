package com.ceridwen.selfissue.client.panels;

import java.util.EventListener;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public interface SelfIssuePanelListener extends EventListener {
  void PanelChange(SelfIssuePanelEvent e);
}
