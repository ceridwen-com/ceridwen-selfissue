package com.ceridwen.selfissue.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.logging.*;
import com.ceridwen.circulation.SIP.helpers.*;
import com.ceridwen.circulation.SIP.messages.*;


/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

class PatronConnectionFailed extends Exception {
}
class PatronBlocked extends Exception {
}
class InvalidPatron extends Exception {
}
class InvalidPatronBarcode extends Exception {
}


public class PatronPanel extends SelfIssuePanel {
  private static Log log = LogFactory.getLog(PatronPanel.class);

  JPanel NavigationPanel = new JPanel();
  BorderLayout PatronBorderLayout = new BorderLayout();
  JButton NextButton = new JButton();
  JButton ResetButton = new JButton();
  BorderLayout NavigationBorderLayout = new BorderLayout();
  JPanel InformationPanel = new JPanel();
  JLabel PatronFieldLabel = new JLabel();
  JPanel DataPanel = new JPanel();
  BorderLayout InformationBorderLayout = new BorderLayout();
  JTextField PatronField = new JTextField();
  BorderLayout DataBorderLayout = new BorderLayout();
  JPanel ResponsePanel = new JPanel();
  BorderLayout ResponseBorderLayout = new BorderLayout();
  Border border1;
  Border border2;
  JLabel CardIcon = new JLabel();
  Border border3;
  javax.swing.Timer ResetTimer;

  CirculationHandler handler;

  public PatronPanel(CirculationHandler handler, javax.swing.Timer ResetTimer) {
    try {
      this.ResetTimer = ResetTimer;
      this.handler = handler;
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    border3 = BorderFactory.createEmptyBorder(10,10,10,10);
    border7 = BorderFactory.createEmptyBorder(140,0,140,0);
    this.setLayout(PatronBorderLayout);
    NextButton.setFont(new java.awt.Font("Dialog", 1, 16));
    NextButton.setNextFocusableComponent(ResetButton);
    NextButton.setText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_Text"));
    NextButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_ToolTipText"));
    NextButton.addActionListener(new PatronPanel_NextButton_actionAdapter(this));
    ResetButton.setFont(new java.awt.Font("Dialog", 1, 16));
    ResetButton.setForeground(Color.black);
    ResetButton.setNextFocusableComponent(PatronField);
    ResetButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronPanelResetButton_ToolTipText"));
    ResetButton.setVerifyInputWhenFocusTarget(true);
    ResetButton.setActionCommand("jButton2");
    ResetButton.setHorizontalTextPosition(SwingConstants.TRAILING);
    ResetButton.setRolloverEnabled(false);
    ResetButton.setText(Configuration.getProperty("UI/PatronPanel/PatronPanelResetButton_Text"));
    ResetButton.addActionListener(new PatronPanel_ResetButton_actionAdapter(this));
    NavigationPanel.setLayout(NavigationBorderLayout);
    PatronFieldLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    PatronFieldLabel.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_ToolTipText"));
    PatronFieldLabel.setLabelFor(PatronField);
    PatronFieldLabel.setText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_Text"));
    InformationPanel.setLayout(InformationBorderLayout);
    PatronField.setFont(new java.awt.Font("Dialog", 1, 16));
    PatronField.setNextFocusableComponent(NextButton);
    PatronField.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronField_ToolTipText"));
    PatronField.setText(Configuration.getProperty("UI/PatronPanel/PatronField_DefaultText"));
    PatronField.addKeyListener(new PatronPanel_PatronField_keyAdapter(this));
    CardIcon.setIcon(Configuration.LoadImage("UI/PatronPanel/CardIcon_Icon"));
    DataPanel.setLayout(DataBorderLayout);
    ResponsePanel.setLayout(ResponseBorderLayout);
    NavigationPanel.setBorder(border1);
    DataPanel.setBorder(border2);
    ResponsePanel.setBorder(border3);
    DataBorderLayout.setHgap(5);
    DataBorderLayout.setVgap(5);
    PatronTextPanel.setLayout(PatronTextBorderLayout);
    PatronTextPanel.setBorder(border7);
    PatronTextPanel.setDebugGraphicsOptions(0);
    PatronTextPanel.setDoubleBuffered(true);
    PatronTextPanel.setPreferredSize(new Dimension(100, 301));
    PatronText.setForeground(Color.red);
    PatronText.setFont(new java.awt.Font("SansSerif", 1, 16));
    PatronText.setForeground(Color.red);
    PatronText.setOpaque(false);
    PatronText.setRequestFocusEnabled(false);
    PatronText.setEditable(false);
    PatronText.setText("");
    PatronText.setLineWrap(true);
    PatronText.setWrapStyleWord(true);
    this.add(NavigationPanel,  BorderLayout.SOUTH);
    NavigationPanel.add(ResetButton, BorderLayout.WEST);
    NavigationPanel.add(NextButton,  BorderLayout.EAST);
    this.add(InformationPanel,  BorderLayout.CENTER);
    InformationPanel.add(DataPanel,  BorderLayout.SOUTH);
    DataPanel.add(PatronFieldLabel, BorderLayout.WEST);
    DataPanel.add(PatronField, BorderLayout.CENTER);
    InformationPanel.add(ResponsePanel,  BorderLayout.CENTER);
    ResponsePanel.add(CardIcon,  BorderLayout.EAST);
    ResponsePanel.add(PatronTextPanel, BorderLayout.CENTER);
    PatronTextPanel.add(PatronText, BorderLayout.CENTER);
    this.grabFocus();
  }

  String lastEnteredId = "";
  Border border4;
  Border border5;
  Border border6;
  JPanel PatronTextPanel = new JPanel();
  BorderLayout PatronTextBorderLayout = new BorderLayout();
  Border border7;
  JTextArea PatronText = new JTextArea();

  void NextButton_actionPerformed(ActionEvent e) {
    PatronInformation request = new PatronInformation();
    PatronInformationResponse response = null;
    try {
      ResetTimer.stop();
      request.setPatronIdentifier(this.PatronField.getText().trim());
      if (request.getPatronIdentifier().length() < 1) {
        this.PatronField.setText("");
        this.PatronField.requestFocus();
        ResetTimer.start();
        return;
      }
      if (!this.validateBarcode(request.getPatronIdentifier(), Configuration.getProperty("UI/Validation/PatronBarcodeMask"))) {
        throw new InvalidPatronBarcode();
      }
      this.PatronText.setText(Configuration.getMessage("CheckingPatronMessage", new String[]{request.getPatronIdentifier()}));
      this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
      try {
        response = (PatronInformationResponse) handler.send(request);
      } catch (java.lang.ClassCastException ex) {
        response = null;
      }
      if (response == null) {
        if ((trustMode || allowOffline) &&
            (!retryPatronWhenError ||
             request.getPatronIdentifier().equals(lastEnteredId))) {
          response = new PatronInformationResponse();
          response.setValidPatron(new Boolean(true));
          response.setPatronStatus("              ");
          response.setPersonalName(request.getPatronIdentifier());
          response.setPatronIdentifier(request.getPatronIdentifier());
        } else {
          throw new PatronConnectionFailed();
        }
      }
      if (! ( (response.getValidPatron() != null) ?
             response.getValidPatron().booleanValue() : false)) {
        if (trustMode &&
            (!retryPatronWhenError ||
             request.getPatronIdentifier().equals(lastEnteredId))) {
        } else {
          throw new InvalidPatron();
        }
      }
      if (FlagBitmap.flagSet(response.getPatronStatus())) {
        if (trustMode &&
            (!retryPatronWhenError ||
             request.getPatronIdentifier().equals(lastEnteredId))) {
        } else {
          throw new PatronBlocked();
        }
      }
      SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this);
      // Sanity check
      if (response.getPatronIdentifier() == null) {
        response.setPatronIdentifier(request.getPatronIdentifier());
      }
      if (!request.getPatronIdentifier().equalsIgnoreCase(response.getPatronIdentifier())) {
          log.error("Patron ID mismatch" + request.getPatronIdentifier() + " - " + response.getPatronIdentifier());
      }
      ev.request = request;
      ev.response = response;
      this.PlaySound("ValidPatron");
      this.firePanelChange(ev);
    } catch (InvalidPatronBarcode ex) {
      this.PlaySound("InvalidPatronBarcode");
      this.PatronText.setText(Configuration.getMessage("InvalidPatronBarcode",
          new String[] {}));
    } catch (PatronConnectionFailed ex) {
      if (trustMode) {
        this.PlaySound("PatronRetry");
        this.PatronText.setText(Configuration.getMessage("PatronRetry",
            new String[] {}));
      } else {
        this.PlaySound("PatronNetworkError");
        this.PatronText.setText(Configuration.getMessage("PatronNetworkError",
            new String[] {request.getPatronIdentifier()}));
      }
    } catch (PatronBlocked ex) {
      if (trustMode) {
        this.PlaySound("PatronRetry");
        this.PatronText.setText(Configuration.getMessage("PatronRetry",
            new String[] {}));
      } else {
        this.PlaySound("BlockedPatronError");
        this.PatronText.setText(Configuration.getMessage("BlockedPatronError",
            new String[] {response.getScreenMessage()}));
      }
    } catch (InvalidPatron ex) {
      if (trustMode) {
        this.PlaySound("PatronRetry");
        this.PatronText.setText(Configuration.getMessage("PatronRetry",
            new String[] {}));
      } else {
        this.PlaySound("InvalidPatronError");
        this.PatronText.setText(Configuration.getMessage("InvalidPatronError",
            new String[] {request.getPatronIdentifier(),
                                response.getScreenMessage()}));
      }
    } catch (Exception ex) {
      if (trustMode) {

        /**@todo: This will loop if the error isn't transient!
         *
         */

        this.PlaySound("PatronRetry");
        this.PatronText.setText(Configuration.getMessage("PatronRetry",
            new String[] {}));
      } else {
        this.PlaySound("UnexpectedPatronError");
        this.PatronText.setText(Configuration.getMessage(
            "UnexpectedPatronError",
            new String[] {}));
      }
      log.fatal("Patron lookup failure: " + ex.getMessage() + ":" + ex.getLocalizedMessage() + " - " +
                ex.getCause());
    }
    lastEnteredId = request.getPatronIdentifier();
    this.PatronField.setText("");
    this.PatronField.requestFocus();
    ResetTimer.start();
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    SelfIssuePanelEvent ev = new SelfIssuePanelEvent(new BookPanel());
    this.firePanelChange(ev);
  }

  void PatronField_keyTyped(KeyEvent e) {
    ResetTimer.restart();
    if (e.getKeyChar() == '\u001B')
      this.ResetButton_actionPerformed(new ActionEvent(this, 0, ""));
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '^') {
      e.consume();
      this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
    if (e.getKeyChar() == '¦') {
      if (this.PatronField.getText().equals("*Test Connection")) {
        if (Configuration.getBoolProperty("CommandInterface/AllowConnectionTest")) {
          e.consume();
          this.PatronField.setText("");
          ACSStatus response;
          try {
            response = (ACSStatus) handler.checkStatus(0);
            this.PatronText.setText(
                "DateTimeSync: " + response.getDateTimeSync() + " | " +
                "InstId: " + response.getInstitutionId() + " | " +
                "LibName: " + response.getLibraryName() + " | " +
                "ProtVer: " + response.getProtocolVersion() + " | " +
                "Retries: " + response.getRetriesAllowed() + " | " +
                "SupMsgs: " + response.getSupportedMessages() + " | " +
                "TermLoc: " + response.getTerminalLocation() + " | " +
                "Timeout: " + response.getTimeoutPeriod() + " | " +
                "PrtLine: " + response.getPrintLine() + " | " +
                "SrcnMsg: " + response.getScreenMessage() + " | " +
                "CheckIn: " + response.isCheckInOk() + " | " +
                "CheckOut: " + response.isCheckOutOk() + " | " +
                "Offline: " + response.isOfflineOk() + " | " +
                "Online: " + response.isOnLineStatus() + " | " +
                "Renewal: " + response.isRenewalPolicy() + " | " +
                "StatusUpdate: " + response.isStatusUpdateOk()
                );
          } catch (java.lang.ClassCastException ex) {
            this.PatronText.setText("Connection error");
          } catch (java.lang.NullPointerException ex) {
            this.PatronText.setText("Connection error");
          }
        }
      } else
      if (this.PatronField.getText().equals("*Shutdown System")) {
        if (Configuration.getBoolProperty("CommandInterface/AllowSystemShutdown")) {
          e.consume();
          this.PatronField.setText("");
          SelfIssueFrame.setOnTop(false);
          PasswordDialog ShutdownConfirmation = new PasswordDialog();
          ShutdownConfirmation.clearPassword();
          ShutdownConfirmation.show();
          if (ShutdownConfirmation.getPassword().equals(Configuration.Decrypt(
              Configuration.getProperty(
              "CommandInterface/SystemShutdownPassword")))) {
            System.exit(0);
          }
          SelfIssueFrame.setOnTop(true);
        }
      } else
      if (this.PatronField.getText().equals("*About")) {
        e.consume();
        this.PatronField.setText("");
        com.ceridwen.util.versioning.AboutDialog dlg = new com.ceridwen.util.versioning.AboutDialog(null, true, com.ceridwen.selfissue.client.SelfIssueClient.class);
        SelfIssueFrame.setOnTop(false);
        dlg.setSize(800, 600);
        dlg.show();
        SelfIssueFrame.setOnTop(true);
      } else
      if (this.PatronField.getText().equals("*Check Systems")) {
        if (Configuration.getBoolProperty("CommandInterface/AllowSystemsCheck")) {
          e.consume();
          this.PatronField.setText("");
          StringBuffer data = new StringBuffer();
          if (handler.securityDevice != null) {
            data.append("Sec: " +
                        handler.securityDevice.getClass().getName() + "\r\n");
          }
          data.append("Log: " +
                      Configuration.getProperty("Systems/Loggers/Logger/@class") +
                      "\r\n");
          data.append("Modes: ");
          if (trustMode) data.append("Trust|");
          if (allowOffline) data.append("Offline|");
          if (retryPatronWhenError) data.append("PatronRetry|");
          if (retryItemWhenError) data.append("ItemRetry|");
          if (allowRenews) data.append("Renews|");
          data.append("\r\n");
          data.append("Spooler: " + handler.spool.size() + "\r\n");
          data.append("Memory (Max, VM, Free): " + Runtime.getRuntime().maxMemory()/(1024*1024) + "MB, ");
          data.append(Runtime.getRuntime().totalMemory()/(1024*1024) + "MB, ");
          data.append(Runtime.getRuntime().freeMemory()/(1024*1024) + "MB\r\n");

          this.PatronText.setText(data.toString());
        }
      } else
/*
      if (this.PatronField.getText().equals("*Debug Console")) {
          bsh.Interpreter i = new bsh.Interpreter(new java.io.InputStreamReader(System.in), System.out, System.err, true);
          i.run();
      } else
*/
      if (this.PatronField.getText().equals("*Test Crash")) {
        if (Configuration.getBoolProperty("CommandInterface/AllowLogTest")) {
          e.consume();
          this.PatronField.setText("");
          throw new java.lang.InternalError("Test");
        }
      } else
      if (this.PatronField.getText().equals("*CheckIn")) {
        if (true || Configuration.getBoolProperty("CommandInterface/AllowCheckIn")) {
          e.consume();
          this.PatronField.setText("");
          this.PatronText.setText("Check In Not Implemented");
        }
      }
    }
  }

  public void grabFocus() {
    super.grabFocus();
    PatronField.grabFocus();
  }
  public void requestFocus() {
    super.requestFocus();
    PatronField.requestFocus();
  }

}

class PatronPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
  PatronPanel adaptee;

  PatronPanel_NextButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class PatronPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  PatronPanel adaptee;

  PatronPanel_ResetButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class PatronPanel_PatronField_keyAdapter extends java.awt.event.KeyAdapter {
  PatronPanel adaptee;

  PatronPanel_PatronField_keyAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.PatronField_keyTyped(e);
  }
}
