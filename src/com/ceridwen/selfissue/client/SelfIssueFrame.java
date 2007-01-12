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

import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.panels.*;
import com.ceridwen.util.versioning.ComponentRegistry;

public class SelfIssueFrame extends JFrame implements SelfIssueFrameMBean
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
  private JLabel CrestIcon = new JLabel();
  private JLabel OlisIcon = new JLabel();
  private JPanel TitleTextPanel = new JPanel();
  private JLabel TitleText = new JLabel();
  private JLabel LibraryText = new JLabel();
  private BorderLayout TitleTextBorderLayout = new BorderLayout();

  private CirculationHandler handler = new CirculationHandler();
  private Border border2;

  //Construct the frame
  public SelfIssueFrame()
  {
    log.error("RTSI Start Up:- Spooled: " + handler.getSpoolSize());

    try {
      Object management = Class.forName("com.ceridwen.selfissue.client.SelfIssueFrameMBInit").getConstructor(new Class[]{SelfIssueFrame.class}).newInstance(new Object[]{this});
    } catch (Exception ex) {
      log.warn("Could not initialise SelfIssueFrame management", ex);
    } catch (java.lang.NoClassDefFoundError ex) {
      log.warn("Could not initialise SelfIssueFrame management", ex);
    }

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
//      MainPanel = new BookPanel(handler, "2103655", "Matthew", "test", ResetTimer);
      jbInit();
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResetTimer.start();
  }

  //Component initialization
  private void jbInit() throws Exception
  {
    MainPane = (JPanel)this.getContentPane();
    border1 = BorderFactory.createCompoundBorder(BorderFactory.
                                                 createCompoundBorder(new
        EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(172, 172, 172)),
        BorderFactory.createEmptyBorder(32, 32, 0, 32)), border1);
    border2 = BorderFactory.createEmptyBorder(16, 0, 0, 0);
    border3 = BorderFactory.createEmptyBorder(16, 16, 0, 0);
    MainPane.setLayout(MainBorderLayout);
    this.setCursor(null);
    this.setEnabled(true);
    this.setForeground(Color.black);
    this.setResizable(false);
    this.setSize(new Dimension(1024, 768));
    this.setMaximizedBounds(new Rectangle(1024, 768));
    this.setUndecorated(true);
    this.setExtendedState(MAXIMIZED_BOTH);
    this.setTitle(Configuration.getProperty("UI/SelfIssue/WindowTitle"));
    MainPane.setBorder(border1);
    TitlePanel.setLayout(TitleBorderLayout);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    TitleBorderLayout.setHgap(2);
    TitleBorderLayout.setVgap(2);
    CrestIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/RightIcon_Icon"));
    OlisIcon.setIcon(Configuration.LoadImage("UI/SelfIssue/LeftIcon_Icon"));
    TitleText.setEnabled(true);
    TitleText.setFont(new java.awt.Font("Dialog", 1, 48));
    TitleText.setForeground(new Color(0, 0, 128));
    TitleText.setOpaque(false);
    TitleText.setRequestFocusEnabled(false);
    TitleText.setToolTipText("");
    TitleText.setHorizontalAlignment(SwingConstants.CENTER);
    TitleText.setHorizontalTextPosition(SwingConstants.TRAILING);
    TitleText.setText(Configuration.getProperty("UI/SelfIssue/TitleText_Text"));
    TitleText.setVerticalAlignment(SwingConstants.BOTTOM);
    TitleText.setVerticalTextPosition(SwingConstants.CENTER);
    LibraryText.setFont(new java.awt.Font("Dialog", 1, 40));
    LibraryText.setForeground(new Color(0, 0, 128));
    LibraryText.setHorizontalAlignment(SwingConstants.CENTER);
    LibraryText.setText(Configuration.getProperty(
        "UI/SelfIssue/LibraryText_Text"));
    LibraryText.setVerticalAlignment(SwingConstants.TOP);
    TitleTextPanel.setLayout(TitleTextBorderLayout);
    BuildVersion.setFont(new java.awt.Font("Dialog", 2, 10));
    BuildVersion.setForeground(Color.gray);
    BuildVersion.setBorder(border2);
    BuildVersion.setHorizontalAlignment(SwingConstants.RIGHT);
    BuildVersion.setText(ComponentRegistry.getName(SelfIssueClient.class) + " " +
                         ComponentRegistry.getVersionString(SelfIssueClient.class));
    StatusPanel.setLayout(borderLayout1);
    Mode.setFont(new java.awt.Font("Dialog", 2, 10));
    Mode.setBorder(border3);
    Mode.setForeground(Color.lightGray);
    Mode.setHorizontalAlignment(SwingConstants.RIGHT);
    Mode.setText(ComponentRegistry.getAuthor(SelfIssueClient.class));
    Ceridwen.setFont(new java.awt.Font("Dialog", 2, 10));
    Ceridwen.setBorder(border3);
    Ceridwen.setForeground(Color.lightGray);
    Ceridwen.setHorizontalAlignment(SwingConstants.LEFT);
    Ceridwen.setText("Ceridwen.com");
    TitlePanel.add(CrestIcon, BorderLayout.EAST);
    TitlePanel.add(OlisIcon, BorderLayout.WEST);
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
      this.handler.securityDevice.stop();
      this.handler.securityDevice.deinit();
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

  public void setCheckOutPanel(String id, String name, String message)
  {
    MainPane.remove(MainPanel);
    MainPanel = new CheckOutPanel(handler, id, demangleName(name), message,
                                  this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new
        SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  public void setCheckInPanel()
  {
    MainPane.remove(MainPanel);
    MainPanel = new CheckInPanel(handler, this.ResetTimer);
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
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  void MainPanel_PanelChange(SelfIssuePanelEvent e)
  {
    if (e.nextPanel == CheckOutPanel.class) {
      setCheckOutPanel( ( (PatronInformationResponse) e.response).
                       getPatronIdentifier(),
                       ( (PatronInformationResponse) e.response).
                       getPersonalName(),
                       ( (PatronInformationResponse) e.response).
                       getScreenMessage());
    } else if (e.nextPanel == CheckInPanel.class) {
      setCheckInPanel();
    } else if (e.nextPanel == OutOfOrderPanel.class) {
      setOutOfOrderPanel();
    } else {
      setPatronPanel();
    }
  }

  void ResetTimer_actionPerformed(ActionEvent e)
  {
    ResetTimer.stop();
    this.handler.securityDevice.stop();
    this.handler.securityDevice.deinit();
    setPatronPanel();
    ResetTimer.start();
  }

  public String getSecurityDevice() {
    return handler.securityDevice.getClass().getCanonicalName();
  }
  public String getLoggingDevice() {
    return handler.getSpoolerClass().getCanonicalName();
  }
  public void terminateSelfIssue()
  {
    this.handler.securityDevice.stop();
    this.handler.securityDevice.deinit();
    System.exit(0);
  }
  public String checkConfiguration(String key) {
    return Configuration.getProperty(key);
  }
  public String checkConnectivity()
  {
    return this.handler.checkStatus(0);
  }
  public Hashtable getModes() {
    Hashtable modes = new Hashtable();
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
    this.handler.securityDevice.reset();
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
    this.handler.securityDevice.stop();
    this.handler.securityDevice.deinit();
    handler = new CirculationHandler();
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
