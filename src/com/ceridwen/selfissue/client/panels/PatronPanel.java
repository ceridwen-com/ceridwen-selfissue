package com.ceridwen.selfissue.client.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.Border;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.helpers.FlagBitmap;
import com.ceridwen.circulation.SIP.messages.PatronInformation;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.selfissue.client.SelfIssueFrame;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.dialogs.PasswordDialog;


/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */



public class PatronPanel extends SelfIssuePanel {
  public class PatronPanelFocusTraversalPolicy
                extends FocusTraversalPolicy {

       public Component getComponentAfter(Container focusCycleRoot,
                                          Component aComponent) {
           if (aComponent.equals(NextButton)) {
               return ResetButton;
           } else if (aComponent.equals(ResetButton)) {
               return PatronField;
           } else if (aComponent.equals(PatronField)) {
               return NextButton;
           }
           return PatronField;
       }

       public Component getComponentBefore(Container focusCycleRoot,
                                      Component aComponent) {
         if (aComponent.equals(ResetButton)) {
             return NextButton;
         } else if (aComponent.equals(NextButton)) {
             return PatronField;
         } else if (aComponent.equals(PatronField)) {
             return ResetButton;
         }
         return PatronField;
       }

       public Component getDefaultComponent(Container focusCycleRoot) {
           return PatronField;
       }

       public Component getLastComponent(Container focusCycleRoot) {
           return ResetButton;
       }

       public Component getFirstComponent(Container focusCycleRoot) {
           return PatronField;
       }
   }


  static class PatronConnectionFailed extends Exception {
  }
  static class PatronBlocked extends Exception {
  }
  static class InvalidPatron extends Exception {
  }
  static class InvalidPatronBarcode extends Exception {
  }
  static class PatronIdTooShort extends Exception {
  }


  private static Log log = LogFactory.getLog(PatronPanel.class);

  private JPanel NavigationPanel = new JPanel();
  private BorderLayout PatronBorderLayout = new BorderLayout();
  private JButton NextButton = new JButton();
  private JButton ResetButton = new JButton();
  private BorderLayout NavigationBorderLayout = new BorderLayout();
  private JPanel InformationPanel = new JPanel();
  private JLabel PatronFieldLabel = new JLabel();
  private JPanel DataPanel = new JPanel();
  private BorderLayout InformationBorderLayout = new BorderLayout();
  private JTextField PatronField = new JTextField();
  private BorderLayout DataBorderLayout = new BorderLayout();
  private JPanel ResponsePanel = new JPanel();
  private BorderLayout ResponseBorderLayout = new BorderLayout();
  private Border border1;
  private Border border2;
  private JLabel CardIcon = new JLabel();
  private Border border3;
  private javax.swing.Timer ResetTimer;

  private CirculationHandler handler;




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
//    NextButton.setNextFocusableComponent(ResetButton);
    NextButton.setText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_Text"));
    NextButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_ToolTipText"));
    NextButton.addActionListener(new PatronPanel_NextButton_actionAdapter(this));
    ResetButton.setFont(new java.awt.Font("Dialog", 1, 16));
    ResetButton.setForeground(Color.black);
//    ResetButton.setNextFocusableComponent(PatronField);
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
//    PatronField.setNextFocusableComponent(NextButton);
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
    PatronPanelFocusTraversalPolicy policy = new PatronPanelFocusTraversalPolicy();
    this.setFocusTraversalPolicy(policy);
    this.setFocusCycleRoot(true);
    this.setFocusTraversalKeysEnabled(true);
    this.grabFocus();
  }

  private String lastEnteredId = "";
  private JPanel PatronTextPanel = new JPanel();
  private BorderLayout PatronTextBorderLayout = new BorderLayout();
  private Border border7;
  private JTextArea PatronText = new JTextArea();

  void NextButton_actionPerformed(ActionEvent e) {
    PatronInformation request = new PatronInformation();
    PatronInformationResponse response = null;

    if (Configuration.getBoolProperty("Modes/EnableBarcodeAlii")) {
      if (this.PatronField.getText().startsWith("$") && this.PatronField.getText().endsWith("%")) {
        this.commandProcessor(convert(this.PatronField.getText()));
        this.PatronField.setText("");
        this.PatronField.requestFocus();
        return;
      }
    }

    try {
      ResetTimer.stop();
      this.PatronField.setEditable(false);
      this.PatronField.setEnabled(false);

      request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
      request.setPatronIdentifier(strim(this.PatronField.getText()));
      if (request.getPatronIdentifier().length() < 1) {
        throw new PatronIdTooShort();
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
      SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckOutPanel.class);
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
    } catch (PatronIdTooShort ex) {
      // nothing to do but allow to drop through
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
    this.PatronField.setEditable(true);
    this.PatronField.setEnabled(true);
    this.PatronField.setText("");
    this.PatronField.requestFocus();
    ResetTimer.start();
  }

  private static String strim(String string) {
    String intermediate = string.trim();
    if (Configuration.getBoolProperty("UI/Control/StripPatronChecksumDigit")) {
      if (intermediate.length() > 0) {
        intermediate = intermediate.substring(0, intermediate.length()-1);
      }
    }
    return intermediate;
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, PatronPanel.class);
    this.firePanelChange(ev);
  }

  private static String convert(String command) {
    StringBuffer buffer = new StringBuffer();
    boolean lowercase = false;
    for (int n = 0; n < command.length(); n++) {
      if (command.charAt(n) == '$') {
        buffer.append('*');
      } else if (command.charAt(n) == '%') {
        ; // i.e. strim
      } else if (command.charAt(n) == ' ') {
        buffer.append(' ');
        lowercase = false;
      } else {
        if (lowercase) {
          buffer.append(Character.toString(command.charAt(n)).toLowerCase());
        } else {
          buffer.append(command.charAt(n));
          lowercase = true;
        }
      }
    }
    return buffer.toString();
  }

  private boolean commandProcessor(String command) {
    if (command.equals("*Test Connection")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowConnectionTest")) {
        this.PatronText.setText(handler.checkStatus(0));
        return true;
      }
    } else if (command.equals("*Shutdown System")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowSystemShutdown")) {
        SelfIssueFrame.setOnTop(false);
        PasswordDialog ShutdownConfirmation = new PasswordDialog();
        ShutdownConfirmation.clearPassword();
        ShutdownConfirmation.setVisible(true);
        if (ShutdownConfirmation.getPassword().equals(Configuration.Decrypt(
            Configuration.getProperty(
                "CommandInterface/SystemShutdownPassword")))) {
          System.exit(0);
        }
        SelfIssueFrame.setOnTop(true);
        return true;
      }
    } else if (command.equals("*About")) {
      com.ceridwen.util.versioning.AboutDialog dlg = new com.ceridwen.util.versioning.AboutDialog(null, true, com.ceridwen.selfissue.client.SelfIssueClient.class);
      SelfIssueFrame.setOnTop(false);
      dlg.setSize(800, 600);
      dlg.setVisible(true);
      SelfIssueFrame.setOnTop(true);
      return true;
    } else if (command.equals("*Check Systems")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowSystemsCheck")) {
        StringBuffer data = new StringBuffer();
        if (handler.securityDevice != null) {
          data.append("Sec: " +
                      handler.securityDevice.getClass().getName() + "\r\n");
        }
        data.append("Log: " +
                    Configuration.getProperty("Systems/Loggers/Logger/@class") +
                    "\r\n");
        data.append("Modes: ");
        if (trustMode) {
          data.append("Trust|");
        }
        if (allowOffline) {
          data.append("Offline|");
        }
        if (retryPatronWhenError) {
          data.append("PatronRetry|");
        }
        if (retryItemWhenError) {
          data.append("ItemRetry|");
        }
        if (allowRenews) {
          data.append("Renews|");
        }
        if (useNoBlock) {
          data.append("NoBlocks|");
        }
        if (suppressSecurityFailureMessages) {
          data.append("SuppressSecurityMsgs|");
        }
        data.append("\r\n");
        data.append("Spooler: " + handler.getSpoolSize() + "\r\n");
        data.append("Memory (Max, VM, Free): " + Runtime.getRuntime().maxMemory()/(1024*1024) + "MB, ");
        data.append(Runtime.getRuntime().totalMemory()/(1024*1024) + "MB, ");
        data.append(Runtime.getRuntime().freeMemory()/(1024*1024) + "MB\r\n");

        this.PatronText.setText(data.toString());
        return true;
      }
    } else if (command.equals("*Test Crash")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowLogTest")) {
        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, PatronPanel.class);
        this.firePanelChange(ev);
        throw new java.lang.InternalError("Test");
      }
    } else if (command.equals("*Check In")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowCheckIn")) {
        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckInPanel.class);
        this.firePanelChange(ev);
        return true;
      }
    } else if (command.startsWith("*Encrypt Password ")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowEncryptPassword")) {
        String password = command.replaceFirst("\\*Encrypt Password ", "");
        this.PatronText.setText(Configuration.Encrypt(password));
        this.PatronField.setText(Configuration.Encrypt(password));
        return true;
      }
    }
    return false;
  }

  void PatronField_keyTyped(KeyEvent e) {
    ResetTimer.restart();
    if (e.getKeyChar() == '\u001B') {
      this.ResetButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '^') {
      e.consume();
      this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
    if (e.getKeyChar() == '¦') {
      if (this.commandProcessor(this.PatronField.getText())) {
        e.consume();
        this.PatronField.setText("");
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
  private PatronPanel adaptee;

  PatronPanel_NextButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class PatronPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  private PatronPanel adaptee;

  PatronPanel_ResetButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class PatronPanel_PatronField_keyAdapter extends java.awt.event.KeyAdapter {
  private PatronPanel adaptee;

  PatronPanel_PatronField_keyAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.PatronField_keyTyped(e);
  }
}
