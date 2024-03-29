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
package com.ceridwen.selfissue.client.panels;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.selfissue.client.config.Configuration;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class SelfIssuePanel extends JPanel {
  /**
	 * 
	 */
	

/**
	 * 
	 */
	private static final long serialVersionUID = -9031892895576984908L;

/**
	 * 
	 */
	

private static final Log log = LogFactory.getLog(SelfIssuePanel.class);

  public static final boolean trustMode = Configuration.getBoolProperty("Modes/TrustMode");
  public static final boolean allowOffline = Configuration.getBoolProperty("Modes/AllowOffline");
  public static final boolean retryPatronWhenError = Configuration.getBoolProperty("Modes/RetryPatronWhenError");
  public static final boolean retryItemWhenError = Configuration.getBoolProperty("Modes/RetryItemWhenError");
  public static final boolean allowRenews = Configuration.getBoolProperty("Modes/AllowRenews");
  public static final boolean useNoBlock = Configuration.getBoolProperty("Modes/UseNoBlock");
  public static final boolean suppressSecurityFailureMessages = Configuration.getBoolProperty("Modes/SuppressSecurityFailureMessages");
  /**@todo: find better name and functionality
   *
   */



  private static final int MAX_LEN = 256;

  boolean validateBarcode(String barcode, String pattern) {
    if (StringUtils.isEmpty(pattern)) {
      return barcode.length() < MAX_LEN;
    }
    return Pattern.matches(pattern, barcode);
  }

  void PlaySound(String sound) {
    try {
      String snd = Configuration.getProperty("UI/Audio/" + sound);
      if (StringUtils.isEmpty(snd)) {
        return;
      }
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Configuration.LoadResource(snd));
      Clip clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      clip.start();
    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
      log.debug("Sound object not found");
    }
  }

  private transient ArrayList<SelfIssuePanelListener> selfIssuePanelListeners;
  
  public synchronized void addSelfIssuePanelListener(SelfIssuePanelListener l) {
    @SuppressWarnings("unchecked")
    ArrayList<SelfIssuePanelListener> v;
    v = ((selfIssuePanelListeners == null)?new ArrayList<>(2):(ArrayList<SelfIssuePanelListener>)selfIssuePanelListeners.clone());
    if (!v.contains(l)) {
      v.add(l);
      selfIssuePanelListeners = v;
    }
  }
  
  public synchronized void removeSelfIssuePanelListener(SelfIssuePanelListener l) {
    if (selfIssuePanelListeners != null && selfIssuePanelListeners.contains(l)) {
      @SuppressWarnings("unchecked")
      ArrayList<SelfIssuePanelListener> v;
      v = (ArrayList<SelfIssuePanelListener>) selfIssuePanelListeners.clone();
      v.remove(l);
      selfIssuePanelListeners = v;
    }
  }
  
  public synchronized void clearSelfIssuePanelListeners() {
    selfIssuePanelListeners = null;
  }
  
  protected void firePanelChange(SelfIssuePanelEvent e) {
    if (selfIssuePanelListeners != null) {
      ArrayList<SelfIssuePanelListener> listeners;
      listeners = selfIssuePanelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((SelfIssuePanelListener) listeners.get(i)).PanelChange(e);
      }
    }
  }

  public static final String escapeHTML(String s) {
	    if (StringUtils.isEmpty(s)) {
	    	return "";
  		}
	    StringBuilder sb = new StringBuilder();
	    int n = s.length();
	    for (int i = 0; i < n ; i++) {
	      char c = s.charAt(i);
	      switch (c) {
	        case '<':
	          sb.append("&lt;");
	          break;
	        case '>':
	          sb.append("&gt;");
	          break;
	        case '&':
	          sb.append("&amp;");
	          break;
	        case '"':
	          sb.append("&quot;");
	          break;
	        case 'à':
	          sb.append("&agrave;");
	          break;
	        case 'À':
	          sb.append("&Agrave;");
	          break;
	        case 'â':
	          sb.append("&acirc;");
	          break;
	        case 'Â':
	          sb.append("&Acirc;");
	          break;
	        case 'ä':
	          sb.append("&auml;");
	          break;
	        case 'Ä':
	          sb.append("&Auml;");
	          break;
	        case 'å':
	          sb.append("&aring;");
	          break;
	        case 'Å':
	          sb.append("&Aring;");
	          break;
	        case 'æ':
	          sb.append("&aelig;");
	          break;
	        case 'Æ':
	          sb.append("&AElig;");
	          break;
	        case 'ç':
	          sb.append("&ccedil;");
	          break;
	        case 'Ç':
	          sb.append("&Ccedil;");
	          break;
	        case 'é':
	          sb.append("&eacute;");
	          break;
	        case 'É':
	          sb.append("&Eacute;");
	          break;
	        case 'è':
	          sb.append("&egrave;");
	          break;
	        case 'È':
	          sb.append("&Egrave;");
	          break;
	        case 'ê':
	          sb.append("&ecirc;");
	          break;
	        case 'Ê':
	          sb.append("&Ecirc;");
	          break;
	        case 'ë':
	          sb.append("&euml;");
	          break;
	        case 'Ë':
	          sb.append("&Euml;");
	          break;
	        case 'ï':
	          sb.append("&iuml;");
	          break;
	        case 'Ï':
	          sb.append("&Iuml;");
	          break;
	        case 'ô':
	          sb.append("&ocirc;");
	          break;
	        case 'Ô':
	          sb.append("&Ocirc;");
	          break;
	        case 'ö':
	          sb.append("&ouml;");
	          break;
	        case 'Ö':
	          sb.append("&Ouml;");
	          break;
	        case 'ø':
	          sb.append("&oslash;");
	          break;
	        case 'Ø':
	          sb.append("&Oslash;");
	          break;
	        case 'ß':
	          sb.append("&szlig;");
	          break;
	        case 'ù':
	          sb.append("&ugrave;");
	          break;
	        case 'Ù':
	          sb.append("&Ugrave;");
	          break;
	        case 'û':
	          sb.append("&ucirc;");
	          break;
	        case 'Û':
	          sb.append("&Ucirc;");
	          break;
	        case 'ü':
	          sb.append("&uuml;");
	          break;
	        case 'Ü':
	          sb.append("&Uuml;");
	          break;
	        case '®':
	          sb.append("&reg;");
	          break;
	        case '©':
	          sb.append("&copy;");
	          break;
	        case '€':
	          sb.append("&euro;");
	          break;
	          // be carefull with this one (non-breaking white space)
	          //           case ' ': sb.append("&nbsp;");break;

	        default:
	          sb.append(c);
	          break;
	      }
	    }
	    return sb.toString();
	  }

  protected String demangleDate(String date) {
    if (date == null) {
      return "";
    }
    return date;
  }

  protected String stripHTML(String in) {
    String process;
    process = in.replaceAll("<html>", "");
    process = process.replaceAll("<head>", "");
    process = process.replaceAll("<body>", "");
    process = process.replaceAll("<p>", "");
    process = process.replaceAll("<b>", "");
    process = process.replaceAll("</b>", "");
    process = process.replaceAll("</p>", "");
    process = process.replaceAll("</body>", "");
    process = process.replaceAll("</head>", "");
    process = process.replaceAll("</html>", "");
    process = process.replaceAll("  ", " ");
    return process.trim();
  }
}
