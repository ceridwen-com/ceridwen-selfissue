package com.ceridwen.selfissue.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.util.versioning.ComponentRegistry;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

import com.ceridwen.circulation.*;
import java.util.Timer;
import java.util.ResourceBundle;

public class SelfIssueFrame extends JFrame {
  JPanel MainPane;
  BorderLayout MainBorderLayout = new BorderLayout();
  JPanel TitlePanel = new JPanel();
  Border border1;
  BorderLayout TitleBorderLayout = new BorderLayout();

  Timer backgroundTasks = new Timer();
  javax.swing.Timer ResetTimer;

//  OfflineLog offline = new OfflineLog();
//  CirculationDevice circ = new CIP3MDevice();
//  OnlineWebLogger logger = new OnlineWebLogger();
//  CirculationHandler handler = new CirculationHandler(circ, offline, logger);
  SelfIssuePanel MainPanel;
  JLabel CrestIcon = new JLabel();
  JLabel OlisIcon = new JLabel();
  JPanel TitleTextPanel = new JPanel();
  JLabel TitleText = new JLabel();
  JLabel LibraryText = new JLabel();
  BorderLayout TitleTextBorderLayout = new BorderLayout();

  CirculationHandler handler = new CirculationHandler();
  Border border2;

  //Construct the frame
  public SelfIssueFrame() {

//    backgroundTasks.scheduleAtFixedRate(logger, (long)10000, (long)10000);
//    backgroundTasks.scheduleAtFixedRate(handler, (long)10000, (long)10000);

    enableEvents(AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_FOCUS_EVENT_MASK | AWTEvent.WINDOW_STATE_EVENT_MASK);
    try {
      ResetTimer = new javax.swing.Timer(Configuration.getIntProperty("UI/Control/ResetTimeout") * 1000,
        new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ResetTimer_actionPerformed(e);
          }
        }
      );
      MainPanel = new PatronPanel(handler, ResetTimer);
//      MainPanel = new BookPanel(handler, "2103655", "Matthew", "test", ResetTimer);
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    ResetTimer.start();
  }
  //Component initialization
  private void jbInit() throws Exception  {
    MainPane = (JPanel) this.getContentPane();
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(172, 172, 172)),BorderFactory.createEmptyBorder(32,32,0,32)),border1);
    border2 = BorderFactory.createEmptyBorder(16,0,0,0);
    border3 = BorderFactory.createEmptyBorder(16,16,0,0);
    MainPane.setLayout(MainBorderLayout);
    this.setCursor(null);
    this.setEnabled(true);
    this.setForeground(Color.black);
    this.setResizable(false);
    this.setSize(new Dimension(1024, 768));
    this.setMaximizedBounds(new Rectangle(1024, 768));
    this.setUndecorated(true);
    this.setExtendedState(this.MAXIMIZED_BOTH);
    this.setTitle(Configuration.getProperty("UI/SelfIssue/WindowTitle"));
    MainPane.setBorder(border1);
    TitlePanel.setLayout(TitleBorderLayout);
    MainPanel.addSelfIssuePanelListener(new SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
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
    LibraryText.setText(Configuration.getProperty("UI/SelfIssue/LibraryText_Text"));
    LibraryText.setVerticalAlignment(SwingConstants.TOP);
    TitleTextPanel.setLayout(TitleTextBorderLayout);
    BuildVersion.setFont(new java.awt.Font("Dialog", 2, 10));
    BuildVersion.setForeground(Color.gray);
    BuildVersion.setBorder(border2);
    BuildVersion.setHorizontalAlignment(SwingConstants.RIGHT);
    BuildVersion.setText(ComponentRegistry.getName(SelfIssueClient.class) + " " + ComponentRegistry.getVersionString(SelfIssueClient.class));
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
    TitlePanel.add(CrestIcon,  BorderLayout.EAST);
    TitlePanel.add(OlisIcon,  BorderLayout.WEST);
    MainPane.add(TitlePanel,  BorderLayout.NORTH);
    TitlePanel.add(TitleTextPanel,  BorderLayout.CENTER);
    TitleTextPanel.add(LibraryText,  BorderLayout.CENTER);
    TitleTextPanel.add(TitleText,  BorderLayout.NORTH);
    MainPane.add(MainPanel, BorderLayout.CENTER);
    MainPane.add(StatusPanel,  BorderLayout.SOUTH);
    StatusPanel.add(BuildVersion,  BorderLayout.CENTER);
    StatusPanel.add(Mode,  BorderLayout.EAST);
    StatusPanel.add(Ceridwen,  BorderLayout.WEST);
  }

  static boolean onTop = true;
  JPanel StatusPanel = new JPanel();
  JLabel BuildVersion = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();
  JLabel Mode = new JLabel();
  Border border3;
  JLabel Ceridwen = new JLabel();

  public static void setOnTop(boolean val) {
    onTop = val;
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {

    if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      this.setState(this.NORMAL);
      return;
    }

    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
        return;
    }

    if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
      if (onTop)
        this.toFront();
    }

    super.processWindowEvent(e);

    if (e.getID() == WindowEvent.WINDOW_ACTIVATED) {
      this.MainPanel.grabFocus();
    }


    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      this.handler.securityDevice.deinit();
      System.exit(0);
    }
  }

  public void setPatronPanel() {
    MainPane.remove(MainPanel);
    MainPanel = new PatronPanel(this.handler, this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  public String demangleName(String name) {
    if (name == null)
      return null;
    String components[] = name.split(",",2);
    if (components.length > 1) {
      String forenames[] = components[1].trim().split(" ", 2);
      return forenames[0] + " " + components[0];
    } else {
      return name;
    }
  }

  public void setBookPanel(String id, String name, String message) {
    MainPane.remove(MainPanel);
    MainPanel = new BookPanel(handler, id, demangleName(name), message, this.ResetTimer);
    MainPanel.addSelfIssuePanelListener(new SelfIssueFrame_MainPanel_selfIssuePanelAdapter(this));
    MainPane.add(MainPanel);
    this.validate();
    MainPanel.grabFocus();
  }

  void MainPanel_PanelChange(SelfIssuePanelEvent e) {
    if (e.getSource().getClass() == BookPanel.class)
        setPatronPanel();
      else
        setBookPanel(((PatronInformationResponse)e.response).getPatronIdentifier(),
                     ((PatronInformationResponse)e.response).getPersonalName(),
                     ((PatronInformationResponse)e.response).getScreenMessage());
  }

  void ResetTimer_actionPerformed(ActionEvent e) {
    ResetTimer.stop();
    setPatronPanel();
    ResetTimer.start();
  }

}


class SelfIssueFrame_MainPanel_selfIssuePanelAdapter implements com.ceridwen.selfissue.client.SelfIssuePanelListener {
  SelfIssueFrame adaptee;

  SelfIssueFrame_MainPanel_selfIssuePanelAdapter(SelfIssueFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void PanelChange(SelfIssuePanelEvent e) {
    adaptee.MainPanel_PanelChange(e);
  }
}
