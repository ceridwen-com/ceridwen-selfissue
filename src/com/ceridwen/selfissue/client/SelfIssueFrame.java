/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
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
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import java.util.Hashtable;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.PatronInformation;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.core.CirculationHandlerImpl;
import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import com.ceridwen.selfissue.client.panels.*;
import com.ceridwen.util.versioning.ComponentRegistry;

public class SelfIssueFrame extends JFrame implements OutOfOrderInterface
{
  /**
	 *
	 */


/**
	 *
	 */
	private static final long serialVersionUID = 2192523113945844754L;

/**
	 *
	 */


private static Log log = LogFactory.getLog(SelfIssueFrame.class);

  private JPanel MainPane;
  private BorderLayout MainBorderLayout = new BorderLayout();
  private JPanel TitlePanel = new JPanel();
  private Border border1;
  private BorderLayout TitleBorderLayout = new BorderLayout();

  private javax.swing.Timer ResetTimer;

//  These are obsolete (I think)
//  private Timer backgroundTasks = new Timer();
//  OfflineLog offline = new OfflineLog();
//  CirculationDevice circ = new CIP3MDevice();
//  OnlineWebLogger logger = new OnlineWebLogger();
//  CirculationHandler handler = new CirculationHandler(circ, offline, logger);

  private SelfIssuePanel MainPanel;
  private JLabel RightIcon = new JLabel();
  private JLabel LeftIcon = new JLabel();
  private JPanel TitleTextPanel = new JPanel();
  private JLabel TitleText = new JLabel();
  private JLabel LibraryText = new JLabel();
  private BorderLayout TitleTextBorderLayout = new BorderLayout();

  private CirculationHandler handler = new CirculationHandlerImpl(this);
  private Border border2;

  //Construct the frame
  public SelfIssueFrame()
  {
    log.info("RTSI Start Up:- Spooled: " + handler.getSpoolSize());


//    backgroundTasks.scheduleAtFixedRate(logger, (long)10000, (long)10000);
//    backgroundTasks.scheduleAtFixedRate(handler, (long)10000, (long)10000);

    enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK |
                 AWTEvent.WINDOW_STATE_EVENT_MASK);
    try {
      ResetTimer = new javax.swing.Timer(Configuration.getIntProperty(
          "UI/Control/ResetTimeout") * 1000,
                                         new java.awt.event.ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          ResetTimer_actionPerformed(e);
        }
      }
      );
      MainPanel = new PatronPanel(handler, ResetTimer);
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResetTimer.start();
  }

  //Component initialization
  private void jbInit() throws Exception
  {
	Color OuterBorderColour = Configuration.getBackgroundColour("OuterBorderColour"); //new Color(0x3F, 0x46, 0x6B);
	Color InnerBorderColour = Configuration.getBackgroundColour("InnerBorderColour"); //new Color(0x40, 0x96, 0xEE);
	Color TitleBackgroundColour = Configuration.getBackgroundColour("TitleBackgroundColour"); //new Color(0xC9, 0xD3, 0xDD);
	Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour"); //new Color(0xC9, 0xD3, 0xDD);  
	Color TitleTextColour = Configuration.getForegroundColour("TitleTextColour"); //new Color(0, 0, 0x44);
	Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour"); //Color.black;
	Color VersionTextColour = Configuration.getForegroundColour("VersionTextColour"); //Color.gray;
	  
    MainPane = (JPanel)this.getContentPane();
    border1 = BorderFactory.createCompoundBorder(BorderFactory.
                                                 createCompoundBorder(new
        EtchedBorder(EtchedBorder.RAISED, Color.white, OuterBorderColour),
        BorderFactory.createEmptyBorder(32, 32, 0, 32)), border1);
    border2 = BorderFactory.createEmptyBorder(16, 0, 0, 0);
    border3 = BorderFactory.createEmptyBorder(16, 16, 0, 0);
    MainPane.setLayout(MainBorderLayout);
    this.setCursor(null);
    this.setEnabled(true);
    this.setBackground(BackgroundColour);
    this.setForeground(DefaultTextColour);
    this.setResizable(false);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int h = screenSize.height;
    int w = screenSize.width;
    this.setSize(new Dimension(w, h));
    this.setMaximizedBounds(new Rectangle(w, h));
    this.setUndecorated(true);
    this.setExtendedState(MAXIMIZED_BOTH);
    this.setTitle(Configuration.getProperty("UI/SelfIssue/WindowTitle"));
    MainPane.setBorder(border1);
    MainPane.setOpaque(true);
    MainPane.setBackground(InnerBorderColour);
    TitlePanel.setLayout(TitleBorderLayout);
    TitlePanel.setOpaque(true);
    TitlePanel.setBackground(TitleBackgroundColour);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    TitleBorderLayout.setHgap(2);
    TitleBorderLayout.setVgap(2);
    RightIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/RightIcon_Icon"));
    RightIcon.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 0));
    LeftIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/LeftIcon_Icon"));
    LeftIcon.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
    TitleText.setEnabled(true);
    TitleText.setFont(new java.awt.Font(Configuration.getProperty("UI/SelfIssue/TitleText_Font"), 1, Configuration.getIntProperty("UI/SelfIssue/TitleText_Size")));
    TitleText.setForeground(TitleTextColour);
    TitleText.setOpaque(false);
    TitleText.setRequestFocusEnabled(false);
    TitleText.setToolTipText("");
    TitleText.setHorizontalAlignment(SwingConstants.CENTER);
    TitleText.setHorizontalTextPosition(SwingConstants.TRAILING);
    TitleText.setText(Configuration.getProperty("UI/SelfIssue/TitleText_Text"));
    TitleText.setVerticalAlignment(SwingConstants.BOTTOM);
    TitleText.setVerticalTextPosition(SwingConstants.CENTER);
    LibraryText.setFont(new java.awt.Font(Configuration.getProperty("UI/SelfIssue/LibraryText_Font"), 1, Configuration.getIntProperty("UI/SelfIssue/LibraryText_Size")));
    LibraryText.setForeground(TitleTextColour);
    LibraryText.setHorizontalAlignment(SwingConstants.CENTER);
    LibraryText.setText(Configuration.getProperty(
        "UI/SelfIssue/LibraryText_Text"));
    LibraryText.setVerticalAlignment(SwingConstants.TOP);
    TitleTextPanel.setLayout(TitleTextBorderLayout);
    TitleTextPanel.setOpaque(false);
    BuildVersion.setFont(new java.awt.Font("Dialog", 2, 10));
    BuildVersion.setForeground(VersionTextColour);
    BuildVersion.setBorder(border2);
    BuildVersion.setHorizontalAlignment(SwingConstants.RIGHT);
    BuildVersion.setText(ComponentRegistry.getName(SelfIssueClient.class) + " " +
                         ComponentRegistry.getVersionString(SelfIssueClient.class));
    StatusPanel.setLayout(borderLayout1);
    StatusPanel.setOpaque(true);
    StatusPanel.setBackground(InnerBorderColour);
    Mode.setFont(new java.awt.Font("Dialog", 2, 10));
    Mode.setBorder(border3);
    Mode.setForeground(VersionTextColour);
    Mode.setHorizontalAlignment(SwingConstants.RIGHT);
    Mode.setText(ComponentRegistry.getAuthor(SelfIssueClient.class));
    Ceridwen.setFont(new java.awt.Font("Dialog", 2, 10));
    Ceridwen.setBorder(border3);
    Ceridwen.setForeground(VersionTextColour);
    Ceridwen.setHorizontalAlignment(SwingConstants.LEFT);
    Ceridwen.setText("Ceridwen.com");
    TitlePanel.add(RightIcon, BorderLayout.EAST);
    TitlePanel.add(LeftIcon, BorderLayout.WEST);
    MainPane.add(TitlePanel, BorderLayout.NORTH);
    TitlePanel.add(TitleTextPanel, BorderLayout.CENTER);
    TitleTextPanel.add(LibraryText, BorderLayout.CENTER);
    TitleTextPanel.add(TitleText, BorderLayout.NORTH);
    MainPane.add(MainPanel, BorderLayout.CENTER);
    MainPane.add(StatusPanel, BorderLayout.SOUTH);
    StatusPanel.add(BuildVersion, BorderLayout.CENTER);
    StatusPanel.add(Mode, BorderLayout.EAST);
    StatusPanel.add(Ceridwen, BorderLayout.WEST);
  }

  private static boolean onTop = true;
  private JPanel StatusPanel = new JPanel();
  private JLabel BuildVersion = new JLabel();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JLabel Mode = new JLabel();
  private Border border3;
  private JLabel Ceridwen = new JLabel();

  public static void setOnTop(boolean val)
  {
    onTop = val;
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e)
  {

    if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      this.setState(NORMAL);
      return;
    }

    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      return;
    }

    if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
      if (onTop) {
        this.toFront();
      }
    }

    super.processWindowEvent(e);

    if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
      this.MainPanel.grabFocus();
    }

    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      this.handler.stopRFIDDevice();
      this.handler.deinitRFIDDevice();
      System.exit(0);
    }
  }

  public void setPatronPanel()
  {
    MainPane.remove(MainPanel);
    MainPanel = new PatronPanel(this.handler, this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  public String demangleName(String name)
  {
    if (name == null) {
      return null;
    }
    String components[] = name.split(",", 2);
    if (components.length > 1) {
      String forenames[] = components[1].trim().split(" ", 2);
      return forenames[0] + " " + components[0];
    } else {
      return name;
    }
  }

  public void setCheckOutPanel(String id, String password, String name, String message)
  {
    MainPane.remove(MainPanel);
    MainPanel = new CheckOutPanel(handler, id, password, demangleName(name), message,
                                  this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  public void setCheckInPanel(String id, String password, String name, String message)
  {
    MainPane.remove(MainPanel);
    MainPanel = new CheckInPanel(handler, id, password, demangleName(name), message,
            this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  public void setOutOfOrderPanel()
  {
    MainPane.remove(MainPanel);
    MainPanel = new OutOfOrderPanel();
    MainPanel.addSelfIssuePanelListener(new
            SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  void MainPanel_PanelChange(SelfIssuePanelEvent e)
  {
    if (e.nextPanel == CheckOutPanel.class) {
      setCheckOutPanel( ( (PatronInformationResponse) e.response).
                       getPatronIdentifier(),
                       ( (PatronInformation) e.request).
                       getPatronPassword(),
                       ( (PatronInformationResponse) e.response).
                       getPersonalName(),
                       ( (PatronInformationResponse) e.response).
                       getScreenMessage());
    } else if (e.nextPanel == CheckInPanel.class) {
        setCheckInPanel( ( (PatronInformationResponse) e.response).
                getPatronIdentifier(),
                ( (PatronInformation) e.request).
                getPatronPassword(),
                ( (PatronInformationResponse) e.response).
                getPersonalName(),
                ( (PatronInformationResponse) e.response).
                getScreenMessage());
    } else if (e.nextPanel == OutOfOrderPanel.class) {
      setOutOfOrderPanel();
    } else {
      setPatronPanel();
    }
  }

  void ResetTimer_actionPerformed(ActionEvent e)
  {
    ResetTimer.stop();
    this.handler.stopRFIDDevice();
    this.handler.deinitRFIDDevice();
    setPatronPanel();
    ResetTimer.start();
  }

  public String getRFIDDevice() {
    return handler.getRFIDDeviceClass().getCanonicalName();
  }
  public String getLoggingDevice() {
    return handler.getSpoolerClass().getCanonicalName();
  }
  public void terminateSelfIssue()
  {
    this.handler.stopRFIDDevice();
    this.handler.deinitRFIDDevice();
    System.exit(0);
  }
  public String checkConfiguration(String key) {
    return Configuration.getProperty(key);
  }
  public String checkConnectivity()
  {
    return this.handler.checkStatus(0);
  }
  public Hashtable<String, Boolean> getModes() {
    Hashtable<String, Boolean> modes = new Hashtable<String, Boolean>();
    modes.put("allowOffline", new Boolean(SelfIssuePanel.allowOffline));
    modes.put("allowRenews", new Boolean(SelfIssuePanel.allowRenews));
    modes.put("retryItemWhenError", new Boolean(SelfIssuePanel.retryItemWhenError));
    modes.put("retryPatronWhenError", new Boolean(SelfIssuePanel.retryPatronWhenError));
    modes.put("suppressSecurityFailureMessages", new Boolean(SelfIssuePanel.suppressSecurityFailureMessages));
    modes.put("trustMode", new Boolean(SelfIssuePanel.trustMode));
    modes.put("useNoBlock", new Boolean(SelfIssuePanel.useNoBlock));
    return modes;
  }
  public void resetSecurity()
  {
    this.handler.resetRFIDDevice();
  }
  public int getSpoolSize()
  {
    return this.handler.getSpoolSize();
  }
  public String getCurrentScreen()
  {
    return MainPanel.getClass().getSimpleName();
  }
  public boolean getOutOfOrder()
  {
    return MainPanel instanceof OutOfOrderPanel;
  }
  public void setOutOfOrder(boolean b)
  {
    if (b) {
      ResetTimer.stop();
      this.MainPanel_PanelChange(new SelfIssuePanelEvent(this, OutOfOrderPanel.class));
    } else {
      this.MainPanel_PanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
      ResetTimer.start();
    }
  }
  public void resetSystem()
  {
    this.handler.stopRFIDDevice();
    this.handler.deinitRFIDDevice();
    handler = new CirculationHandlerImpl(this);
    this.MainPanel_PanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
  }
}

class SelfIssueFrame_MainPanel_selfIssuePanelAdapter implements com.ceridwen.selfissue.client.panels.SelfIssuePanelListener {
  private SelfIssueFrame adaptee;

  SelfIssueFrame_MainPanel_selfIssuePanelAdapter(SelfIssueFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void PanelChange(SelfIssuePanelEvent e) {
    adaptee.MainPanel_PanelChange(e);
  }
}
