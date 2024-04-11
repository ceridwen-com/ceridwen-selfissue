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

import com.ceridwen.circulation.SIP.messages.Message;
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
import com.ceridwen.util.versioning.LibraryIdentifier;
import java.util.Arrays;
import javax.swing.text.html.HTMLEditorKit;


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

       @Override
       public Component getComponentAfter(Container focusCycleRoot,
                                          Component aComponent) {
           if (aComponent.equals(NextButton)) {
               return ResetButton;
           } else if (aComponent.equals(ResetButton)) {
               return patronId.isBlank()?PatronField:PasswordField;
           } else if (aComponent.equals(patronId.isBlank()?PatronField:PasswordField)) {
               return NextButton;
           }
           return patronId.isBlank()?PatronField:PasswordField;
       }

       @Override
       public Component getComponentBefore(Container focusCycleRoot,
                                      Component aComponent) {
         if (aComponent.equals(ResetButton)) {
             return NextButton;
         } else if (aComponent.equals(NextButton)) {
             return patronId.isBlank()?PatronField:PasswordField;
         } else if (aComponent.equals(patronId.isBlank()?PatronField:PasswordField)) {
             return ResetButton;
         }
         return patronId.isBlank()?PatronField:PasswordField;
       }

       @Override
       public Component getDefaultComponent(Container focusCycleRoot) {
           return patronId.isBlank()?PatronField:PasswordField;
       }

       @Override
       public Component getLastComponent(Container focusCycleRoot) {
           return patronId.isBlank()?PatronField:PasswordField;
       }

       @Override
       public Component getFirstComponent(Container focusCycleRoot) {
           return patronId.isBlank()?PatronField:PasswordField;
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
  static class InvalidPatronPassword extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7260107670696287543L;

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


  private static final Log log = LogFactory.getLog(PatronPanel.class);

  private JPanel NavigationPanel = new JPanel();
  private BorderLayout PatronBorderLayout = new BorderLayout();
  private JButton NextButton = new JButton();
  private JButton ResetButton = new JButton();
  private BorderLayout NavigationBorderLayout = new BorderLayout();
  private JPanel InformationPanel = new JPanel();
  private JLabel PatronFieldLabel = new JLabel();
  private JPanel DataPanel = new JPanel();
  private BorderLayout InformationBorderLayout = new BorderLayout();
  private JTextField PatronField = new JTextField(Configuration.getIntProperty("UI/PatronPanel/PatronField_Length", 8));
  private JPasswordField PasswordField = new JPasswordField(Configuration.getIntProperty("UI/PatronPanel/PasswordField_Length", 8));
  private FlowLayout DataFlowLayout = new FlowLayout();
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
            PatronPanel.log.fatal("Patron Panel Failure: " + e.getMessage() + " - " +
                    Arrays.toString(e.getStackTrace()));
    }
  }
  private void jbInit() throws Exception {
    Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour");  
    Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour");
    Font  DefaultTextFont = Configuration.getFont("DefaultText");
    Color StatusTextColour = Configuration.getForegroundColour("StatusTextColour");
    Font  StatusTextFont = Configuration.getFont("StatusText");
    Color WarningTextColour = Configuration.getForegroundColour("WarningTextColour");
    Font  WarningTextFont = Configuration.getFont("WarningText");
    Color ButtonTextColour = Configuration.getForegroundColour("ButtonTextColour");
    Font  ButtonTextFont = Configuration.getFont("ButtonText");
    Color ButtonBackgroundColour = Configuration.getBackgroundColour("ButtonBackgroundColour");
    Color InputTextColour = Configuration.getForegroundColour("InputTextColour");
    Font  InputTextFont = Configuration.getFont("InputText");
    Color InputBackgroundColour = Configuration.getBackgroundColour("InputBackgroundColour");
    Color InputBorderColour = Configuration.getBackgroundColour("InputBorderColour");
    Color InputSelectionColour = Configuration.getBackgroundColour("InputSelectionColour");
    Color InputSelectedTextColour = Configuration.getBackgroundColour("InputSelectedTextColour");
    Color InputCaretColour = Configuration.getBackgroundColour("InputCaretColour");
    Color InputDisabledTextColour = Configuration.getBackgroundColour("InputDisabledTextColour");
    Color LabelTextColour = Configuration.getForegroundColour("LabelTextColour");
    Font  LabelTextFont = Configuration.getFont("LabelText");
    
    int insetPt = Configuration.getScaledPointSize("UI/Styling/InputInset", 2);
    int insetPx = Configuration.pt2Pixel(insetPt);
    Border inset = BorderFactory.createEmptyBorder(insetPx, insetPx, insetPx, insetPx);
	      
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    border3 = BorderFactory.createEmptyBorder(10,10,10,10);
    border7 = BorderFactory.createEmptyBorder(140,0,140,0);
    border8 = BorderFactory.createEmptyBorder(0,0,0,0);
    this.setLayout(PatronBorderLayout);
    this.setOpaque(true);
    this.setBackground(BackgroundColour);
    NextButton.setFont(ButtonTextFont);
    NextButton.setActionCommand(""); // We'll use Action Command to pass patron password if sent by IDReaderDevice
    NextButton.addActionListener(new PatronPanel_NextButton_actionAdapter(this));
    NextButton.setForeground(ButtonTextColour);
    NextButton.setBackground(ButtonBackgroundColour);
    NextButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));
    ResetButton.setFont(ButtonTextFont);
    ResetButton.setForeground(ButtonTextColour);
    ResetButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronPanelResetButton_ToolTipText"));
    ResetButton.setVerifyInputWhenFocusTarget(true);
    ResetButton.setActionCommand("jButton2");
    ResetButton.setHorizontalTextPosition(SwingConstants.TRAILING);
    ResetButton.setRolloverEnabled(false);
    ResetButton.setText(Configuration.getProperty("UI/PatronPanel/PatronPanelResetButton_Text"));
    ResetButton.addActionListener(new PatronPanel_ResetButton_actionAdapter(this));
    ResetButton.setForeground(ButtonTextColour);
    ResetButton.setBackground(ButtonBackgroundColour);
    ResetButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));
    NavigationPanel.setLayout(NavigationBorderLayout);
    NavigationPanel.setOpaque(false);
    PatronFieldLabel.setFont(LabelTextFont);
    PatronFieldLabel.setForeground(LabelTextColour);
    InformationPanel.setLayout(InformationBorderLayout);
    InformationPanel.setOpaque(false);
    PatronField.setFont(InputTextFont);
    PatronField.setBackground(InputBackgroundColour);
    PatronField.setForeground(InputTextColour);
    PatronField.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(InputBorderColour,2), 
            inset));
    PatronField.setSelectionColor(InputSelectionColour);
    PatronField.setSelectedTextColor(InputSelectedTextColour);
    PatronField.setCaretColor(InputCaretColour);
    PatronField.setDisabledTextColor(InputDisabledTextColour);
    PatronField.addKeyListener(new PatronPanel_PatronField_keyAdapter(this));    
    PatronField.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronField_ToolTipText"));
    PatronField.setText(""); 
    PasswordField.setFont(InputTextFont);
    PasswordField.setBackground(InputBackgroundColour);
    PasswordField.setForeground(InputTextColour);
    PasswordField.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(InputBorderColour,2), 
            inset));
    PasswordField.setSelectionColor(InputSelectionColour);
    PasswordField.setSelectedTextColor(InputSelectedTextColour);
    PasswordField.setCaretColor(InputCaretColour);
    PasswordField.setDisabledTextColor(InputDisabledTextColour);
    PasswordField.addKeyListener(new PatronPanel_PatronField_keyAdapter(this));        
    PasswordField.setToolTipText(Configuration.getProperty("UI/PatronPanel/PasswordField_ToolTipText"));
    PasswordField.setText("");
    ConfigForId();
    CardIcon.setIcon(Configuration.LoadImage("UI/PatronPanel/CardIcon"));
    DataPanel.setLayout(DataFlowLayout);
    DataPanel.setOpaque(false);
    ResponsePanel.setLayout(ResponseBorderLayout);
    ResponsePanel.setOpaque(false);
    NavigationPanel.setBorder(border1);
    DataPanel.setBorder(border2);
    ResponsePanel.setBorder(border3);
    DataFlowLayout.setHgap(5);
    DataFlowLayout.setVgap(5);
    PatronTextPanel.setLayout(PatronTextBorderLayout);
    PatronTextPanel.setOpaque(false);
    PatronTextPanel.setBorder(border7);
    PatronTextPanel.setDebugGraphicsOptions(0);
    PatronTextPanel.setDoubleBuffered(true);
    PatronText.setForeground(DefaultTextColour);
    PatronText.setFont(DefaultTextFont);
    PatronText.setBackground(BackgroundColour);
    PatronText.setBorder(border8);
    PatronText.setRequestFocusEnabled(false);
    PatronText.setEditable(false);
    PatronText.setText("");
    HTMLEditorKit PatronHtml = new HTMLEditorKit();
    PatronHtml.getStyleSheet().addRule(
        "body {font-family: " + DefaultTextFont.getFamily() + "; " +
            "font-size: " + DefaultTextFont.getSize() + "pt; " +
            "font-style: normal; " +
            "color: " + Configuration.colorEncode(DefaultTextColour) + "; " + 
            "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
    PatronHtml.getStyleSheet().addRule(
        "em {font-family: " + StatusTextFont.getFamily() + "; " +
            "font-size: " + StatusTextFont.getSize() + "pt; " +
            "font-style: normal; " +
            "color: " + Configuration.colorEncode(StatusTextColour) + "; " + 
            "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");        
    PatronHtml.getStyleSheet().addRule(
        "strong {font-family: " + WarningTextFont.getFamily() + "; " +
            "font-size: " + WarningTextFont.getSize() + "pt; " +
            "font-style: normal; " +
            "color: " + Configuration.colorEncode(WarningTextColour) + "; " + 
            "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
    PatronText.setEditorKit(PatronHtml);
    PatronText.setContentType("text/html"); 
    PatronText.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronText_ToolTipText"));
    this.add(NavigationPanel,  BorderLayout.SOUTH);
    NavigationPanel.add(ResetButton, BorderLayout.EAST);
    this.add(InformationPanel,  BorderLayout.CENTER);
    InformationPanel.add(DataPanel,  BorderLayout.SOUTH);
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
  private JEditorPane PatronText = new JEditorPane();

  
  private String patronId = "";
  private String patronPassword = "";
  
  void ConfigForId() {
    patronId = "";
    patronPassword = "";
    PatronFieldLabel.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_ToolTipText"));
    PatronFieldLabel.setText(Configuration.getProperty("UI/PatronPanel/PatronFieldLabel_Text"));
    NextButton.setText(Configuration.getProperty("UI/PatronPanel/PatronSubmitButton_Text"));
    NextButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PatronSubmitButton_ToolTipText")); 
    PatronFieldLabel.setLabelFor(PatronField);
    DataPanel.removeAll();
    DataPanel.add(PatronFieldLabel, null);
    DataPanel.add(PatronField, null);
    DataPanel.add(NextButton, null);    
  }
  
  void ConfigForPassword() {
    patronPassword = "";
    PatronFieldLabel.setToolTipText(Configuration.getProperty("UI/PatronPanel/PasswordFieldLabel_ToolTipText"));
    PatronFieldLabel.setText(Configuration.getProperty("UI/PatronPanel/PasswordFieldLabel_Text"));
    NextButton.setText(Configuration.getProperty("UI/PatronPanel/PasswordSubmitButton_Text"));
    NextButton.setToolTipText(Configuration.getProperty("UI/PatronPanel/PasswordSubmitButton_ToolTipText"));   
    PatronFieldLabel.setLabelFor(PasswordField);
    DataPanel.removeAll();
    DataPanel.add(PatronFieldLabel, null);
    DataPanel.add(PasswordField, null);
    DataPanel.add(NextButton, null);      
  }
  
  void NextButton_actionPerformed(ActionEvent e) {
    ResetTimer.stop();
    this.stopPatronIDReader();
    this.PatronField.setEditable(false);
    this.PatronField.setEnabled(false);
    this.PasswordField.setEditable(false);
    this.PasswordField.setEnabled(false);    

    try {
        if (patronId.isBlank()) {
            if (Configuration.getBoolProperty("Systems/Modes/EnableBarcodeAliases")) {
                if (this.PatronField.getText().startsWith("$") && this.PatronField.getText().endsWith("%")) {
                    this.commandProcessor(convert(this.PatronField.getText()));
                    this.PatronField.setText("");
                    this.PatronField.requestFocus();
                    return;
                }
            }
            patronId = strim(this.PatronField.getText());
            if (StringUtils.isEmpty(patronId)) {
                throw new PatronIdTooShort();
            }
            if (!this.validateBarcode(patronId, Configuration.getProperty("UI/Validation/PatronBarcodeMask"))) {
                throw new InvalidPatronBarcode();
            }
            if (Configuration.getBoolProperty("Systems/SIP/RequirePatronPassword")) {
                ConfigForPassword();
            } else {
                SIPPatronLogon(patronId, patronPassword);  
                ConfigForId();
            }
        } else {
            if (patronPassword.isBlank()) {
                patronPassword = strim(String.valueOf(this.PasswordField.getPassword()));
                if (!this.validateBarcode(patronPassword, Configuration.getProperty("UI/Validation/PatronPasswordMask"))) {
                    throw new InvalidPatronPassword();
                }            
            }
            SIPPatronLogon(patronId, patronPassword); 
            ConfigForId();            
        }
    } catch (PatronIdTooShort ex) {
        ConfigForId();
    } catch (InvalidPatronBarcode ex) {
        this.PlaySound("InvalidPatronBarcode");
        this.PatronText.setText(Configuration.getMessage("InvalidPatronBarcode",
                new String[] {patronId}));
        ConfigForId();
    } catch (InvalidPatronPassword ex) {
        this.PlaySound("InvalidPatronPassword");
        this.PatronText.setText(Configuration.getMessage("InvalidPatronPassword",
                new String[] {}));
        ConfigForPassword();
    } finally {
        this.PatronField.setEditable(true);
        this.PatronField.setEnabled(true);
        this.PatronField.setText("");
        this.PasswordField.setEditable(true);
        this.PasswordField.setEnabled(true);
        this.PasswordField.setText("");
        (patronId.isBlank()?PatronField:PasswordField).requestFocus();
        this.startPatronIDReader();
        ResetTimer.start();      
    }
  }

    private void SIPPatronLogon(String patronId, String patronPassword) {
        PatronInformation request = new PatronInformation();
        PatronInformationResponse response = null;
        try {
            request.setPatronIdentifier(patronId);
            request.setPatronPassword(patronPassword);
            request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
            request.setTerminalPassword(Configuration.getProperty("Systems/SIP/TerminalPassword"));
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
                    response.setValidPatron(true);
                    response.setValidPatronPassword(true);
                    response.getPatronStatus().clear();
                    response.setPersonalName(request.getPatronIdentifier());
                    response.setPatronIdentifier(request.getPatronIdentifier());
                } else {
                    throw new PatronConnectionFailed();
                }
            }
            if (! ((response.isValidPatron() != null) ? response.isValidPatron() : false))
            {
                if (trustMode &&
                        (!retryPatronWhenError ||
                        request.getPatronIdentifier().equals(lastEnteredId))) {
                    response.setPersonalName(request.getPatronIdentifier());
                } else {
                    throw new InvalidPatron();
                }
            }
            if (! (((response.isValidPatronPassword() != null) ? response.isValidPatronPassword() : false) ||
                    !Configuration.getBoolProperty("Systems/SIP/RequirePatronPassword")))
            {
                if (trustMode &&
                        (!retryPatronWhenError ||
                        request.getPatronIdentifier().equals(lastEnteredId))) {
                } else {
                    throw new InvalidPatronPassword();
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
        } catch (InvalidPatronPassword ex) {
            if (trustMode) {
                this.PlaySound("PatronRetry");
                this.PatronText.setText(Configuration.getMessage("PatronRetry",
                        new String[] {}));
            } else {
                this.PlaySound("InvalidPatronError");
                this.PatronText.setText(Configuration.getMessage("InvalidPatronPasswordError",
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
        }   lastEnteredId = request.getPatronIdentifier();
    }

  private boolean isBlocked(PatronStatus patronStatus) {
	if (patronStatus.isCardReportedLost()) {
		return true;
	}
	if (patronStatus.isChargePrivilegesDenied()) {
		return true;
	}
	if (patronStatus.isExcessiveOutstandingFees()) {
		return true;
	}
	if (patronStatus.isExcessiveOutstandingFines()) {
		return true;
	}
	if (patronStatus.isHoldPrivilegesDenied()) {
		return true;
	}
	if (patronStatus.isRecallOverdue()) {
		return true;
	}
	if (patronStatus.isRecallPrivilegesDenied()) {
		return true;
	}
        if (patronStatus.isRenewalPrivilegesDenied()) {
		return true;
	}
        if (patronStatus.isTooManyClaimsOfItemsReturned()) {
		return true;
	}
        if (patronStatus.isTooManyItemsBilled()) {
		return true;
	}
        if (patronStatus.isTooManyItemsCharged()) {
		return true;
	}
        if (patronStatus.isTooManyItemsLost()) {
		return true;
	}
        if (patronStatus.isTooManyItemsOverdue()) {
		return true;
	}
	return patronStatus.isTooManyRenewals();
  }
private static String strim(String string) {
    String intermediate = string.trim();
    if (Configuration.getBoolProperty("UI/Advanced/StripPatronChecksumDigit")) {
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
    StringBuilder buffer = new StringBuilder();
    boolean lowercase = false;
    for (int n = 0; n < command.length(); n++) {
        switch (command.charAt(n)) {
            case '$':
                buffer.append('*');
                break;
            case '%':
                ; // i.e. strim
                break;
            case ' ':
                buffer.append(' ');
                lowercase = false;
                break;
            default:
                if (lowercase) {
                    buffer.append(Character.toString(command.charAt(n)).toLowerCase());
                } else {
                    buffer.append(command.charAt(n));
                    lowercase = true;
                }       break;
        }
    }
    return buffer.toString();
  }

  private boolean commandProcessor(String command) {
            switch (command) {
                case "*Test Connection":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowConnectionTest")) {
                        this.PatronText.setText(handler.checkStatus(0));
                        return true;
                    }             break;
                case "*Shutdown System":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowSystemShutdown")) {
                        SelfIssueFrame.setOnTop(false);
                        PasswordDialog ShutdownConfirmation = new PasswordDialog("Please enter system password");
                        ShutdownConfirmation.clearPassword();
                        ShutdownConfirmation.setVisible(true);
                        if (ShutdownConfirmation.getPassword().equals(Configuration.Decrypt(
                                Configuration.getProperty(
                                        "Admin/CommandInterface/SystemPassword")))) {
                            System.exit(0);
                        }
                        SelfIssueFrame.setOnTop(true);
                        return true;
                    }             break;
                case "*About":
                    com.ceridwen.util.versioning.AboutDialog dlg = new com.ceridwen.util.versioning.AboutDialog(null, true, new LibraryIdentifier("com.ceridwen", "Ceridwen Self Issue Client"));
                    SelfIssueFrame.setOnTop(false);
                    dlg.setSize(800, 600);
                    dlg.setVisible(true);
                    SelfIssueFrame.setOnTop(true);
                    return true;
                case "*Check Systems":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowSystemsCheck")) {
                        StringBuilder data = new StringBuilder();
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
                            data.append(Configuration.getSubProperty(loggers.item(i), "@class")).append("\r\n");
                        }
                        try {
                            Connection conn = ConnectionFactory.getConnection(false);
                            ConnectionFactory.releaseConnection(conn);
                            data.append("Host: ").append(conn.getHost());
                            data.append(": ").append(conn.getPort()).append("\r\n");
                            data.append("Timeouts: ").append(conn.getConnectionTimeout()).append(",").append(conn.getIdleTimeout()).append("\r\n");
                            data.append("Retries: ").append(conn.getRetryAttempts()).append(",").append(conn.getRetryWait()).append("\r\n");
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
                            data.append("Encoding: ").append(Message.getCharsetEncoding()).append("\r\n");
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
                            data.append("Spooler: ").append(handler.getSpoolSize()).append("\r\n");
                            data.append("Memory (Max, VM, Free): ").append(Runtime.getRuntime().maxMemory()/(1024*1024)).append("MB, ");
                            
                            data.append(Runtime.getRuntime().totalMemory()/(1024*1024)).append("MB, ");
                            data.append(Runtime.getRuntime().freeMemory()/(1024*1024)).append("MB\r\n");
                            
                            this.PatronText.setText(data.toString());
                        } catch (Exception ex) {
                        }
                        return true;
                    }             break;
                case "*Test Crash":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowLogTest")) {
                        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, PatronPanel.class);
                        this.firePanelChange(ev);
                        throw new java.lang.InternalError("Test");
                    }             break;
                case "*Out Of Order":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowOutOfOrder")) {
                        SelfIssueFrame.setOnTop(false);
                        PasswordDialog OOOConfirmation = new PasswordDialog("Please enter system password");
                        OOOConfirmation.clearPassword();
                        OOOConfirmation.setVisible(true);
                        if (OOOConfirmation.getPassword().equals(Configuration.Decrypt(
                                Configuration.getProperty(
                                        "Admin/CommandInterface/SystemPassword")))) {
                            SelfIssueFrame.setOnTop(true);
                            SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, OutOfOrderPanel.class);
                            this.firePanelChange(ev);
                        }
                        SelfIssueFrame.setOnTop(true);
                        return true;
                    }           break;
                case "*Check In":
                    if (Configuration.getBoolProperty("Admin/CommandInterface/AllowCheckIn")) {
                        SelfIssueFrame.setOnTop(false);
                        PasswordDialog OOOConfirmation = new PasswordDialog("Please enter system password");
                        OOOConfirmation.clearPassword();
                        OOOConfirmation.setVisible(true);
                        if (OOOConfirmation.getPassword().equals(Configuration.Decrypt(
                                Configuration.getProperty(
                                        "Admin/CommandInterface/SystemPassword")))) {
                            SelfIssueFrame.setOnTop(true);
                            SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckInPanel.class);
                            ev.request = new PatronInformation();
                            ev.response = new PatronInformationResponse();
                            this.firePanelChange(ev);
                        }
                        SelfIssueFrame.setOnTop(true);
                        return true;
                    }             break;
                default:
                    break;
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
    if (patronId.isBlank()) {
        if (e.getKeyChar() == '¦' || e.getKeyChar() == '|') {
          if (this.commandProcessor(this.PatronField.getText())) {
            e.consume();
            this.PatronField.setText("");
          }
        }
    }
  }

        @Override
  public void grabFocus() {
    super.grabFocus();
    (patronId.isBlank()?PatronField:PasswordField).grabFocus();
  }
        @Override
  public void requestFocus() {
    super.requestFocus();
    (patronId.isBlank()?PatronField:PasswordField).requestFocus();
  }

  private void startPatronIDReader() {
      this.handler.initIDReaderDevice(CirculationHandler.IDReaderDeviceType.PATRON_IDREADER);
      this.handler.startIDReaderDevice(this);
  }

  private void stopPatronIDReader() {
      this.handler.stopIDReaderDevice();
      this.handler.deinitIDReaderDevice();
  }
  
Stack<String> repeatPreventer = new Stack<>();

@Override
public void autoInputData(String identifier, String passcode) {
    if (!repeatPreventer.contains(identifier)) {
        repeatPreventer.push(identifier);
        patronId = identifier;
        patronPassword = passcode;
        this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
}
}

class PatronPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
  private final PatronPanel adaptee;

  PatronPanel_NextButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class PatronPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  private final PatronPanel adaptee;

  PatronPanel_ResetButton_actionAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  @Override
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class PatronPanel_PatronField_keyAdapter extends java.awt.event.KeyAdapter {
  private final PatronPanel adaptee;

  PatronPanel_PatronField_keyAdapter(PatronPanel adaptee) {
    this.adaptee = adaptee;
  }
  @Override
  public void keyTyped(KeyEvent e) {
    adaptee.PatronField_keyTyped(e);
  }
}
