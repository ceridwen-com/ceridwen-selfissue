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
package com.ceridwen.selfissue.client.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.*;
import javax.swing.border.Border;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;

import com.ceridwen.circulation.SIP.messages.PatronInformation;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.types.flagfields.PatronStatus;
import com.ceridwen.selfissue.client.SelfIssueFrame;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.core.ConnectionFactory;
import com.ceridwen.selfissue.client.devices.IDReaderDeviceListener;
import com.ceridwen.selfissue.client.dialogs.PasswordDialog;


/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */



public class PatronPanel extends SelfIssuePanel implements IDReaderDeviceListener {
  /**
	 * 
	 */
	
/**
	 * 
	 */
	private static final long serialVersionUID = -7765316012028983494L;
/**
	 * 
	 */
	
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -1425736249516562467L;

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	
  }
  static class PatronBlocked extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6370764466269991288L;

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	
  }
  static class InvalidPatron extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5900996233221963525L;

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	
  }
  static class InvalidPatronBarcode extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7260107670696287289L;

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	
  }
  static class PatronIdTooShort extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9200672214377501836L;

	/**
	 * 
	 */
	

	/**
	 * 
	 */
	
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
  private Border border8;
  private javax.swing.Timer ResetTimer;

  private CirculationHandler handler;




  public PatronPanel(CirculationHandler handler, javax.swing.Timer ResetTimer) {
    try {
      this.ResetTimer = ResetTimer;
      this.handler = handler;
      jbInit();
      this.startPatronIDReader();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
	Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour");  
	Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour");
	Color WarningTextColour = Configuration.getForegroundColour("WarningTextColour");
	Color ButtonTextColour = Configuration.getForegroundColour("ButtonTextColour");
	Color ButtonBackgroundColour = Configuration.getBackgroundColour("ButtonBackgroundColour");
	  
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    border3 = BorderFactory.createEmptyBorder(10,10,10,10);
    border7 = BorderFactory.createEmptyBorder(140,0,140,0);
    border8 = BorderFactory.createEmptyBorder(0,0,0,0);
    this.setLayout(PatronBorderLayout);
    this.setOpaque(true);
    this.setBackground(BackgroundColour);
    NextButton.setFont(new java.awt.Font("Dialog", 1, 16));
    NextButton.setActionCommand(""); // We'll use Action Command to pass patron password if sent by IDReaderDevice
//    NextButton.setNextFocusableComponent(ResetButton);
    NextButton.setText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_Text"));
    NextButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronPanelNextButton_ToolTipText"));
    NextButton.addActionListener(new PatronPanel_NextButton_actionAdapter(this));
    NextButton.setForeground(ButtonTextColour);
    NextButton.setBackground(ButtonBackgroundColour);
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
    ResetButton.setForeground(ButtonTextColour);
    ResetButton.setBackground(ButtonBackgroundColour);
    NavigationPanel.setLayout(NavigationBorderLayout);
    NavigationPanel.setOpaque(false);
    PatronFieldLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    PatronFieldLabel.setForeground(DefaultTextColour);
    PatronFieldLabel.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_ToolTipText"));
    PatronFieldLabel.setLabelFor(PatronField);
    PatronFieldLabel.setText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_Text"));
    InformationPanel.setLayout(InformationBorderLayout);
    InformationPanel.setOpaque(false);
    PatronField.setFont(new java.awt.Font("Dialog", 1, 16));
    PatronField.setBackground(BackgroundColour);
    PatronField.setForeground(DefaultTextColour);
//    PatronField.setNextFocusableComponent(NextButton);
    PatronField.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronField_ToolTipText"));
    PatronField.setText(Configuration.getProperty("UI/PatronPanel/PatronField_DefaultText"));
    PatronField.addKeyListener(new PatronPanel_PatronField_keyAdapter(this));
    CardIcon.setIcon(Configuration.LoadImage("UI/PatronPanel/CardIcon_Icon"));
    DataPanel.setLayout(DataBorderLayout);
    DataPanel.setOpaque(false);
    ResponsePanel.setLayout(ResponseBorderLayout);
    ResponsePanel.setOpaque(false);
    NavigationPanel.setBorder(border1);
    DataPanel.setBorder(border2);
    ResponsePanel.setBorder(border3);
    DataBorderLayout.setHgap(5);
    DataBorderLayout.setVgap(5);
    PatronTextPanel.setLayout(PatronTextBorderLayout);
    PatronTextPanel.setOpaque(false);
    PatronTextPanel.setBorder(border7);
    PatronTextPanel.setDebugGraphicsOptions(0);
    PatronTextPanel.setDoubleBuffered(true);
    PatronTextPanel.setPreferredSize(new Dimension(100, 301));
    PatronText.setForeground(WarningTextColour);
    PatronText.setFont(new java.awt.Font("SansSerif", 1, 16));
    PatronText.setBackground(BackgroundColour);
    PatronText.setBorder(border8);
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
      this.stopPatronIDReader();
      this.PatronField.setEditable(false);
      this.PatronField.setEnabled(false);

      request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
      request.setTerminalPassword(Configuration.getProperty("Systems/SIP/TerminalPassword"));
      request.setPatronIdentifier(strim(this.PatronField.getText()));
      if (StringUtils.isEmpty(request.getPatronIdentifier())) {
        throw new PatronIdTooShort();
      }
      if (!this.validateBarcode(request.getPatronIdentifier(), Configuration.getProperty("UI/Validation/PatronBarcodeMask"))) {
        throw new InvalidPatronBarcode();
      }
      if (StringUtils.isNotEmpty(e.getActionCommand())) {
    	  request.setPatronPassword(e.getActionCommand());
      }
      if (Configuration.getBoolProperty("Systems/SIP/RequirePatronPassword") && (StringUtils.isEmpty(request.getPatronPassword()))) {
          SelfIssueFrame.setOnTop(false);
          PasswordDialog patronPasswordDialog = new PasswordDialog("Please enter your password");
          patronPasswordDialog.clearPassword();
          patronPasswordDialog.setVisible(true);
          String password = strim(patronPasswordDialog.getPassword());
          if (StringUtils.isEmpty(password)) {
        	  throw new PatronIdTooShort();
          }
          request.setPatronPassword(password);
          SelfIssueFrame.setOnTop(true);
      }
      this.PatronText.setText(Configuration.getMessage("CheckingPatronMessage", new String[]{request.getPatronIdentifier()}));
      try {
          this.PatronText.paint(this.PatronText.getGraphics());          
      } catch (Exception ex) {
          PatronPanel.log.warn("Error during redraw", ex);
      }
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
          response.getPatronStatus().unsetAll();
          response.setPersonalName(request.getPatronIdentifier());
          response.setPatronIdentifier(request.getPatronIdentifier());
        } else {
          throw new PatronConnectionFailed();
        }
      }
      if (! ( (response.isValidPatron() != null) ?
             response.isValidPatron().booleanValue() : false)) {
        if (trustMode &&
            (!retryPatronWhenError ||
             request.getPatronIdentifier().equals(lastEnteredId))) {
        } else {
          throw new InvalidPatron();
        }
      }
      if (isBlocked(response.getPatronStatus())) {
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
      this.stopPatronIDReader();      
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
    this.startPatronIDReader();
    ResetTimer.start();
  }

  private boolean isBlocked(PatronStatus patronStatus) {
	if (patronStatus.isSet(PatronStatus.CARD_REPORTED_LOST)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.CHARGE_PRIVILEGES_DENIED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.EXCESSIVE_OUTSTANDING_FEES)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.EXCESSIVE_OUTSTANDING_FINES)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.HOLD_PRIVILIGES_DENIED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.RECALL_OVERDUE)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.RECALL_PRIVILIGES_DENIED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.RENEWAL_PRIVILIGES_DENIED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_CLAIMS_OF_ITEMS_RETURNED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_ITEMS_BILLED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_ITEMS_CHARGED)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_ITEMS_LOST)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_ITEMS_OVERDUE)) {
		return true;
	}
	if (patronStatus.isSet(PatronStatus.TOO_MANY_RENEWALS)) {
		return true;
	}
	return false;
  }
private static String strim(String string) {
    String intermediate = string.trim();
    if (Configuration.getBoolProperty("UI/Control/StripPatronChecksumDigit")) {
      if (StringUtils.isNotEmpty(intermediate)) {
        intermediate = intermediate.substring(0, intermediate.length()-1);
      }
    }
    return intermediate;
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    this.stopPatronIDReader();      
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
        PasswordDialog ShutdownConfirmation = new PasswordDialog("Please enter system password");
        ShutdownConfirmation.clearPassword();
        ShutdownConfirmation.setVisible(true);
        if (ShutdownConfirmation.getPassword().equals(Configuration.Decrypt(
            Configuration.getProperty(
                "CommandInterface/SystemPassword")))) {
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
/*TODO        
        if (handler.getRFIDDeviceClass() != null) {
          data.append("RFID: " +
                      handler.getRFIDDeviceClass().getName() + "\r\n");
        }
        if (handler.getSecurityDeviceClass() != null) {
            data.append("Security: " +
                        handler.getSecurityDeviceClass().getName() + "\r\n");
          }
*/          
        data.append("Loggers: ");
        NodeList loggers = Configuration.getPropertyList("Systems/Loggers/Logger");
        for (int i = 0; i < loggers.getLength(); i++) {
            data.append(Configuration.getSubProperty(loggers.item(i), "@class") +
            "\r\n");
        }
        try {
        	Connection conn = ConnectionFactory.getConnection(false);
	        ConnectionFactory.releaseConnection(conn);
        data.append("Host: " + conn.getHost());
        data.append(": " + conn.getPort() + "\r\n");
        data.append("Timeouts: " + conn.getConnectionTimeout() + ","
        		+ conn.getIdleTimeout() + "\r\n");
        data.append("Retries: " + conn.getRetryAttempts() + ","
        		+ conn.getRetryWait() + "\r\n");
        data.append("Error handling: ");
        if (conn.getAddSequenceAndChecksum()) {
        	data.append("AddChecksum|");
        }
        if (conn.getStrictChecksumChecking()) {
        	data.append("CheckChecksum|");
        }
        if (conn.getStrictSequenceChecking()) {
        	data.append("CheckSequence");
        }
        data.append("\r\n");
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
          data.append("SuppressSecurityMsgs");
        }
        data.append("\r\n");
        data.append("Spooler: " + handler.getSpoolSize() + "\r\n");
        data.append("Memory (Max, VM, Free): " + Runtime.getRuntime().maxMemory()/(1024*1024) + "MB, ");
        
        data.append(Runtime.getRuntime().totalMemory()/(1024*1024) + "MB, ");
        data.append(Runtime.getRuntime().freeMemory()/(1024*1024) + "MB\r\n");

        this.PatronText.setText(data.toString());
        } catch (Exception ex) {
        }
        return true;
      }
    } else if (command.equals("*Test Crash")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowLogTest")) {
        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, PatronPanel.class);
        this.firePanelChange(ev);
        throw new java.lang.InternalError("Test");
      }
    } else if (command.equals("*Out Of Order")) {
        if (Configuration.getBoolProperty("CommandInterface/AllowOutOfOrder")) {
            SelfIssueFrame.setOnTop(false);
            PasswordDialog OOOConfirmation = new PasswordDialog("Please enter system password");
            OOOConfirmation.clearPassword();
            OOOConfirmation.setVisible(true);
            if (OOOConfirmation.getPassword().equals(Configuration.Decrypt(
                Configuration.getProperty(
                    "CommandInterface/SystemPassword")))) {
                SelfIssueFrame.setOnTop(true);
                SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, OutOfOrderPanel.class);
                this.firePanelChange(ev);
            }
            SelfIssueFrame.setOnTop(true);
            return true;
        }         
    } else if (command.equals("*Check In")) {
      if (Configuration.getBoolProperty("CommandInterface/AllowCheckIn")) {
          SelfIssueFrame.setOnTop(false);
          PasswordDialog OOOConfirmation = new PasswordDialog("Please enter system password");
          OOOConfirmation.clearPassword();
          OOOConfirmation.setVisible(true);
          if (OOOConfirmation.getPassword().equals(Configuration.Decrypt(
              Configuration.getProperty(
                  "CommandInterface/SystemPassword")))) {
              SelfIssueFrame.setOnTop(true);
              SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckInPanel.class);
              ev.request = new PatronInformation();
              ev.response = new PatronInformationResponse();
              this.firePanelChange(ev);
          }
          SelfIssueFrame.setOnTop(true);
          return true;
      }
    } 
//    else if (command.startsWith("*Edit Configuration")) {
//      if (Configuration.getBoolProperty("CommandInterface/AllowConfigurationEditor")) {
//	      PasswordDialog EditorConfirmation = new PasswordDialog("Please enter system password");
//	      EditorConfirmation.clearPassword();
//	      EditorConfirmation.setVisible(true);
//	      if (EditorConfirmation.getPassword().equals(Configuration.Decrypt(
//	          Configuration.getProperty(
//	              "CommandInterface/SystemPassword")))) {
//	    	  			
//	    	  			Editor.main(new String[]{});	    	  
//	      }
//	      return true;
//      }
//    }
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

  private void startPatronIDReader() {
      this.handler.initIDReaderDevice(CirculationHandler.IDReaderDeviceType.PATRON_IDREADER);
      this.handler.startIDReaderDevice(this);
  }

  private void stopPatronIDReader() {
      this.handler.stopIDReaderDevice();
      this.handler.deinitIDReaderDevice();
  }
  
  protected void finalize() throws java.lang.Throwable {
      this.stopPatronIDReader();
      super.finalize();
  }
  
Stack<String> repeatPreventer = new Stack<String>();

@Override
public void autoInputData(String identifier, String passcode) {
    if (!repeatPreventer.contains(identifier)) {
        repeatPreventer.push(identifier);
        this.PatronField.setText(identifier);
        this.NextButton_actionPerformed(new ActionEvent(this, 0, (passcode==null?"":passcode)));
    }
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
