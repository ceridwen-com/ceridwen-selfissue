package com.ceridwen.selfissue.client;

import org.apache.commons.logging.*;
import java.applet.Applet;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import java.util.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class SelfIssuePanel extends JPanel {
  private static Log log = LogFactory.getLog(BookPanel.class);

  static final boolean trustMode = Configuration.getBoolProperty("Modes/TrustMode");
  static final boolean allowOffline = Configuration.getBoolProperty("Modes/AllowOffline");
  static final boolean retryPatronWhenError = Configuration.getBoolProperty("Modes/RetryPatronWhenError");
  static final boolean retryItemWhenError = Configuration.getBoolProperty("Modes/RetryItemWhenError");
  static final boolean allowRenews = Configuration.getBoolProperty("Modes/AllowRenews");
  static final boolean useNoBlock = Configuration.getBoolProperty("Modes/UseNoBlock");

  static final int MAX_LEN = 256;

  boolean validateBarcode(String barcode, String pattern) {
    if (pattern == null)
      return barcode.length() < MAX_LEN;
    if (pattern.length() == 0)
      return barcode.length() < MAX_LEN;
    return Pattern.matches(pattern, barcode);
  }

  void PlaySound(String sound) {
    try {
      String snd = Configuration.getProperty("UI/Audio/" + sound);
      if (snd == null)
        return;
      if (snd.length() == 0)
        return;
      Applet.newAudioClip(Configuration.LoadResource(snd)).play();
    } catch (Exception ex) {
      log.debug("Sound object not found");
    }
  }

  private transient Vector selfIssuePanelListeners;
  public synchronized void addSelfIssuePanelListener(SelfIssuePanelListener l) {
    Vector v = selfIssuePanelListeners == null ? new Vector(2) : (Vector) selfIssuePanelListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      selfIssuePanelListeners = v;
    }
  }
  public synchronized void removeSelfIssuePanelListener(SelfIssuePanelListener l) {
    if (selfIssuePanelListeners != null && selfIssuePanelListeners.contains(l)) {
      Vector v = (Vector) selfIssuePanelListeners.clone();
      v.removeElement(l);
      selfIssuePanelListeners = v;
    }
  }
  protected void firePanelChange(SelfIssuePanelEvent e) {
    if (selfIssuePanelListeners != null) {
      Vector listeners = selfIssuePanelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((SelfIssuePanelListener) listeners.elementAt(i)).PanelChange(e);
      }
    }
  }
}
