package com.ceridwen.selfissue.client;

import java.util.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public interface SelfIssuePanelListener extends EventListener {
  public void PanelChange(SelfIssuePanelEvent e);
}