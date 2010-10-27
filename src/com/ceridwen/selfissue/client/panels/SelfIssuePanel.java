/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client.panels;

import java.applet.Applet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.selfissue.client.config.Configuration;

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
	

private static Log log = LogFactory.getLog(SelfIssuePanel.class);

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
    if (pattern == null) {
      return barcode.length() < MAX_LEN;
    }
    if (pattern.length() == 0) {
      return barcode.length() < MAX_LEN;
    }
    return Pattern.matches(pattern, barcode);
  }

  void PlaySound(String sound) {
    try {
      String snd = Configuration.getProperty("UI/Audio/" + sound);
      if (snd == null) {
        return;
      }
      if (snd.length() == 0) {
        return;
      }
      Applet.newAudioClip(Configuration.LoadResource(snd)).play();
    } catch (Exception ex) {
      log.debug("Sound object not found");
    }
  }

  private transient Vector<SelfIssuePanelListener> selfIssuePanelListeners;
  public synchronized void addSelfIssuePanelListener(SelfIssuePanelListener l) {
    @SuppressWarnings("unchecked")
	Vector<SelfIssuePanelListener> v = ((selfIssuePanelListeners == null)?new Vector<SelfIssuePanelListener>(2):(Vector<SelfIssuePanelListener>)selfIssuePanelListeners.clone());
    if (!v.contains(l)) {
      v.addElement(l);
      selfIssuePanelListeners = v;
    }
  }
  public synchronized void removeSelfIssuePanelListener(SelfIssuePanelListener l) {
    if (selfIssuePanelListeners != null && selfIssuePanelListeners.contains(l)) {
      @SuppressWarnings("unchecked")
	Vector<SelfIssuePanelListener> v = (Vector<SelfIssuePanelListener>) selfIssuePanelListeners.clone();
      v.removeElement(l);
      selfIssuePanelListeners = v;
    }
  }
  protected void firePanelChange(SelfIssuePanelEvent e) {
    if (selfIssuePanelListeners != null) {
      Vector<SelfIssuePanelListener> listeners = selfIssuePanelListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((SelfIssuePanelListener) listeners.elementAt(i)).PanelChange(e);
      }
    }
  }

  public static final String escapeHTML(String s) {
    StringBuffer sb = new StringBuffer();
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
        case '�':
          sb.append("&agrave;");
          break;
        case '�':
          sb.append("&Agrave;");
          break;
        case '�':
          sb.append("&acirc;");
          break;
        case '�':
          sb.append("&Acirc;");
          break;
        case '�':
          sb.append("&auml;");
          break;
        case '�':
          sb.append("&Auml;");
          break;
        case '�':
          sb.append("&aring;");
          break;
        case '�':
          sb.append("&Aring;");
          break;
        case '�':
          sb.append("&aelig;");
          break;
        case '�':
          sb.append("&AElig;");
          break;
        case '�':
          sb.append("&ccedil;");
          break;
        case '�':
          sb.append("&Ccedil;");
          break;
        case '�':
          sb.append("&eacute;");
          break;
        case '�':
          sb.append("&Eacute;");
          break;
        case '�':
          sb.append("&egrave;");
          break;
        case '�':
          sb.append("&Egrave;");
          break;
        case '�':
          sb.append("&ecirc;");
          break;
        case '�':
          sb.append("&Ecirc;");
          break;
        case '�':
          sb.append("&euml;");
          break;
        case '�':
          sb.append("&Euml;");
          break;
        case '�':
          sb.append("&iuml;");
          break;
        case '�':
          sb.append("&Iuml;");
          break;
        case '�':
          sb.append("&ocirc;");
          break;
        case '�':
          sb.append("&Ocirc;");
          break;
        case '�':
          sb.append("&ouml;");
          break;
        case '�':
          sb.append("&Ouml;");
          break;
        case '�':
          sb.append("&oslash;");
          break;
        case '�':
          sb.append("&Oslash;");
          break;
        case '�':
          sb.append("&szlig;");
          break;
        case '�':
          sb.append("&ugrave;");
          break;
        case '�':
          sb.append("&Ugrave;");
          break;
        case '�':
          sb.append("&ucirc;");
          break;
        case '�':
          sb.append("&Ucirc;");
          break;
        case '�':
          sb.append("&uuml;");
          break;
        case '�':
          sb.append("&Uuml;");
          break;
        case '�':
          sb.append("&reg;");
          break;
        case '�':
          sb.append("&copy;");
          break;
        case '�':
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
