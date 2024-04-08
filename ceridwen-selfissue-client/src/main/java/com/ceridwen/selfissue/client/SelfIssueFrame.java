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
package com.ceridwen.selfissue.client;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
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
import com.ceridwen.util.versioning.LibraryIdentifier;
import com.ceridwen.util.versioning.LibraryRegistry;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;

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
  private JPanel TitlePanel = new JPanel();
  private BorderLayout TitleBorderLayout = new BorderLayout();
  private SelfIssuePanel MainPanel;
  private JLabel RightIcon = new JLabel();
  private JLabel LeftIcon = new JLabel();
  private JPanel TitleTextPanel = new JPanel();
  private JPanel TitleTextPanel2 = new JPanel();  
  private JLabel TitleText = new JLabel();
  private JLabel LibraryText = new JLabel();
  private static boolean onTop = true;
  private JPanel StatusPanel = new JPanel();
  private JLabel BuildVersion = new JLabel();
  private JLabel Mode = new JLabel();
  private JLabel Ceridwen = new JLabel();

  
  private javax.swing.Timer ResetTimer;

//  These are obsolete (I think)
//  private Timer backgroundTasks = new Timer();
//  OfflineLog offline = new OfflineLog();
//  CirculationDevice circ = new CIP3MDevice();
//  OnlineWebLogger logger = new OnlineWebLogger();
//  CirculationHandler handler = new CirculationHandler(circ, offline, logger);


  private final CirculationHandler handler;

  //Construct the frame
  public SelfIssueFrame()
  {
//    backgroundTasks.scheduleAtFixedRate(logger, (long)10000, (long)10000);
//    backgroundTasks.scheduleAtFixedRate(handler, (long)10000, (long)10000);

    enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK |
                 AWTEvent.WINDOW_STATE_EVENT_MASK);
    try {
      int resetTime = Configuration.getIntProperty("UI/Advanced/ResetTimeout", 5);
      
      ResetTimer = new javax.swing.Timer( resetTime * 1000, (ActionEvent e) -> {
          ResetTimer_actionPerformed(e);
      });
      jbInit();
    } catch (Exception e) {
      System.out.println("Self Issue Could not initialise user interface. Halting.");
      e.printStackTrace();
      System.exit(1);
    }

    ResetTimer.start();
    handler = new CirculationHandlerImpl(this); 
	if (MainPanel == null) {
		// If MainPanel has been set, it will be OutOfOrder due to CirculationHandlerImpl error
	    this.setPatronPanel();
	}
//    MainPanel = new PatronPanel(handler, ResetTimer);
//    MainPanel.addSelfIssuePanelListener(new
//        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
//    MainPane.add(MainPanel, BorderLayout.CENTER);
    

    System.out.println("Self Issue Started:- Spooled: " + handler.getSpoolSize());
    log.info("Self Issue Started:- Spooled: " + handler.getSpoolSize());
  }

  //Component initialization
  private void jbInit() throws Exception
  {
    Color OuterBorderColour = Configuration.getBackgroundColour("OuterBorderColour");
    Color InnerBorderColour = Configuration.getBackgroundColour("InnerBorderColour");
    Color TitleBackgroundColour = Configuration.getBackgroundColour("TitleBackgroundColour");
    Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour");  
    Color TitleTextColour = Configuration.getForegroundColour("TitleTextColour");
    Color LibraryTextColour = Configuration.getForegroundColour("SubTitleTextColour");
    Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour");
    Color VersionTextColour = Configuration.getForegroundColour("VersionTextColour");
    Font TitleTextFont = Configuration.getFont("TitleText");
    Font LibraryTextFont = Configuration.getFont("SubTitleText");
    Font VersionTextFont = Configuration.getFont("VersionText");
    int BorderWidthPt = Configuration.getScaledPointSize("UI/Styling/BorderWidth"); 
    int BorderWidthPxl = Configuration.pt2Pixel(BorderWidthPt);
    int VDividerWidthPt = Configuration.getScaledPointSize("UI/Styling/VerticalDividerWidth");
    int VDividerWidthPxl = Configuration.pt2Pixel(VDividerWidthPt);    
    int HDividerWidthPt = Configuration.getScaledPointSize("UI/Styling/HorizontalDividerWidth");
    int HDividerWidthPxl = Configuration.pt2Pixel(HDividerWidthPt);
    
    MainPane = (JPanel)this.getContentPane();
    MainPane.setLayout(new BorderLayout());
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
    this.setTitle("SelfIssue Terminal: " + Configuration.getProperty("UI/SelfIssue/TitleText") + " - " + Configuration.getProperty("UI/SelfIssue/SubTitleText"));
    
    
    jbStatusPanelInit(VersionTextFont, VersionTextColour, InnerBorderColour, BorderWidthPt);
  
    MainPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.
                                                 createCompoundBorder(new
        EtchedBorder(EtchedBorder.RAISED, Color.white, OuterBorderColour),
        BorderFactory.createEmptyBorder(BorderWidthPxl, BorderWidthPxl, 0, BorderWidthPxl)), null)); 
    MainPane.setOpaque(true);
    MainPane.setBackground(InnerBorderColour);
//    TitleBorderLayout.setHgap(2);
//    TitleBorderLayout.setVgap(2);
    TitlePanel.setLayout(TitleBorderLayout);
    TitlePanel.setOpaque(true);
    TitlePanel.setBackground(TitleBackgroundColour);
    RightIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/RightIcon"));
    RightIcon.setBorder(BorderFactory.createMatteBorder(0, VDividerWidthPxl, 0 , 0, InnerBorderColour));
    LeftIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/LeftIcon"));
    LeftIcon.setBorder(BorderFactory.createMatteBorder(0, 0 , 0, VDividerWidthPxl, InnerBorderColour));
    TitleText.setEnabled(true);
    TitleText.setFont(TitleTextFont);
    TitleText.setForeground(TitleTextColour);
    TitleText.setOpaque(false);
    TitleText.setRequestFocusEnabled(false);
    TitleText.setToolTipText("");
    TitleText.setHorizontalAlignment(SwingConstants.CENTER);
    TitleText.setHorizontalTextPosition(SwingConstants.CENTER);
    TitleText.setText(Configuration.getProperty("UI/SelfIssue/TitleText"));
    TitleText.setVerticalAlignment(SwingConstants.BOTTOM);
    TitleText.setVerticalTextPosition(SwingConstants.BOTTOM);
    TitleText.setAlignmentX(Component.CENTER_ALIGNMENT);
    LibraryText.setFont(LibraryTextFont);
    LibraryText.setForeground(LibraryTextColour);
    LibraryText.setHorizontalTextPosition(SwingConstants.CENTER);    
    LibraryText.setHorizontalAlignment(SwingConstants.CENTER);
    LibraryText.setText(Configuration.getProperty("UI/SelfIssue/SubTitleText"));
    LibraryText.setVerticalAlignment(SwingConstants.TOP);
    LibraryText.setVerticalTextPosition(SwingConstants.TOP);
    LibraryText.setAlignmentX(Component.CENTER_ALIGNMENT);
    TitleTextPanel.setLayout(new BoxLayout(TitleTextPanel, BoxLayout.Y_AXIS));
    TitleTextPanel.setOpaque(false);
    TitleTextPanel.add(TitleText);
    TitleTextPanel.add(LibraryText);
    TitleTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    TitleTextPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
    TitleTextPanel2.setLayout(new GridBagLayout());
    TitleTextPanel2.setOpaque(false);
    GridBagConstraints c = new GridBagConstraints();
    c.anchor = GridBagConstraints.CENTER;
    TitleTextPanel2.add(TitleTextPanel, c);
    TitlePanel.add(RightIcon, BorderLayout.EAST);
    TitlePanel.add(LeftIcon, BorderLayout.WEST);
    TitlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, HDividerWidthPxl, 0, InnerBorderColour));
    TitlePanel.add(TitleTextPanel2, BorderLayout.CENTER);
    MainPane.add(TitlePanel, BorderLayout.NORTH);

    MainPane.add(StatusPanel, BorderLayout.SOUTH);
  }

    private void jbStatusPanelInit(Font VersionTextFont, Color VersionTextColour, Color InnerBorderColour, int BorderWidthPt) {
        final double RATIO = 0.3;
        int StatusWidthPt = (int)Math.round(BorderWidthPt - VersionTextFont.getSize()*(1+RATIO));
        int StatusWidthPxl = Configuration.pt2Pixel(StatusWidthPt);
        int VSpacerPxl = Configuration.pt2Pixel((int)Math.round(VersionTextFont.getSize()*RATIO));
        int HSpacerPxl = Configuration.pt2Pixel(VersionTextFont.getSize());
        
        BuildVersion.setFont(VersionTextFont);
        BuildVersion.setForeground(VersionTextColour);
        BuildVersion.setBorder(BorderFactory.createEmptyBorder(StatusWidthPxl, 0, VSpacerPxl, HSpacerPxl));
        BuildVersion.setHorizontalAlignment(SwingConstants.RIGHT);
        LibraryRegistry registry = new LibraryRegistry();
        LibraryIdentifier selfissueID = new LibraryIdentifier("com.ceridwen.selfissue", "com.ceridwen.selfissue:ceridwen-selfissue-client");
        BuildVersion.setText(registry.getLibraryVendorId(selfissueID) + " " +
                registry.getLibraryVersion(selfissueID) + " (" +
                registry.getLibraryBuildDate(selfissueID) + ")");
        StatusPanel.setLayout(new BorderLayout());
        StatusPanel.setOpaque(true);
        StatusPanel.setBackground(InnerBorderColour);
        Mode.setFont(VersionTextFont);
        Mode.setBorder(BorderFactory.createEmptyBorder(StatusWidthPxl, 0, VSpacerPxl, HSpacerPxl));
        Mode.setForeground(VersionTextColour);
        Mode.setHorizontalAlignment(SwingConstants.RIGHT);
        Mode.setText(registry.getLibraryVendor(selfissueID));
        Ceridwen.setFont(VersionTextFont);
        Ceridwen.setBorder(BorderFactory.createEmptyBorder(StatusWidthPxl, HSpacerPxl, VSpacerPxl, 0));
        Ceridwen.setForeground(VersionTextColour);
        Ceridwen.setHorizontalAlignment(SwingConstants.LEFT);
        Ceridwen.setText("ceridwen.com");
        StatusPanel.add(BuildVersion, BorderLayout.CENTER);
        StatusPanel.add(Mode, BorderLayout.EAST);
        StatusPanel.add(Ceridwen, BorderLayout.WEST);
    }

  public static void setOnTop(boolean val)
  {
    onTop = val;
  }

  //Overridden so we can exit when window is closed
  @Override
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
      this.handler.stopIDReaderDevice();
      this.handler.deinitIDReaderDevice();
      System.exit(0);
    }
  }

  public void setPatronPanel()
  {
    if (MainPanel != null) {
      MainPane.remove(MainPanel);
      MainPanel.clearSelfIssuePanelListeners();
      MainPanel = null;
      this.validate();
    }
    MainPanel = new PatronPanel(this.handler, this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new
    SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    CheckInPanel = null;
    CheckOutPanel = null;
    MainPane.add(MainPanel);
    MainPane.revalidate();
    MainPane.repaint();
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
  
  private SelfIssuePanel CheckInPanel = null;
  private SelfIssuePanel CheckOutPanel = null;

  public void setCheckOutPanel(String id, String password, String name, String message)
  {
    if (MainPanel != null) {
      MainPane.remove(MainPanel);
      MainPanel.clearSelfIssuePanelListeners();
      MainPanel = null;
      this.validate();
    }
    if (CheckOutPanel == null) {
      CheckOutPanel = new CheckOutPanel(handler, id, password, demangleName(name), message,
                                    this.ResetTimer);
    }
    MainPanel = CheckOutPanel;
    MainPanel.addSelfIssuePanelListener(new
    SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));    
    MainPane.add(MainPanel);
    MainPane.revalidate();
    MainPane.repaint();
    this.validate();
    MainPanel.grabFocus();
  }

  public void setCheckInPanel(String id, String password, String name, String message)
  {
    if (MainPanel != null) {
      MainPane.remove(MainPanel);
      MainPanel.clearSelfIssuePanelListeners();
      MainPanel = null;
      this.validate();
    }
    if (CheckInPanel == null) {
      CheckInPanel = new CheckInPanel(handler, id, password, demangleName(name), message,
                                    this.ResetTimer);
    }
    MainPanel = CheckInPanel;
    MainPanel.addSelfIssuePanelListener(new
    SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));    
    MainPane.add(MainPanel);
    MainPane.revalidate();
    MainPane.repaint();
    this.validate();
    MainPanel.grabFocus();
  }

  public void setOutOfOrderPanel()
  {
	if (MainPanel != null) {
		MainPane.remove(MainPanel);
	}
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
    this.handler.stopIDReaderDevice();
    this.handler.deinitIDReaderDevice();
    setPatronPanel();
    ResetTimer.start();
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
  public boolean getOutOfOrder()
  {
    return MainPanel instanceof OutOfOrderPanel;
  }

/** TODO Review this code  
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
  public void resetSystem()
  {
    this.handler.stopRFIDDevice();
    this.handler.deinitRFIDDevice();
    handler = new CirculationHandlerImpl(this);
    this.MainPanel_PanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
  }
*/  
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
