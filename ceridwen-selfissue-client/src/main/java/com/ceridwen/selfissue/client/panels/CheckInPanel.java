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

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.CheckIn;
import com.ceridwen.circulation.SIP.messages.CheckInResponse;
import com.ceridwen.circulation.SIP.messages.PatronInformation;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.devices.FailureException;
import com.ceridwen.selfissue.client.devices.IDReaderDeviceListener;
import com.ceridwen.selfissue.client.devices.TimeoutException;
import com.ceridwen.selfissue.client.log.OnlineLogEvent;
import java.awt.Font;
import java.util.Arrays;
import javax.swing.JTextPane;

public class CheckInPanel extends SelfIssuePanel implements IDReaderDeviceListener {
    /**
	 * 
	 */

    /**
	 * 
	 */
    private static final long serialVersionUID = 7067920005564294837L;

    /**
	 * 
	 */

    public class CheckInPanelFocusTraversalPolicy
                extends FocusTraversalPolicy {

        @Override
        public Component getComponentAfter(Container focusCycleRoot,
                                          Component aComponent) {
            if (aComponent.equals(CheckInPanel.this.BookField)) {
                return CheckInPanel.this.CheckinButton;
            } else if (aComponent.equals(CheckInPanel.this.CheckinButton)) {
                return CheckInPanel.this.NextButton;
            } else if (aComponent.equals(CheckInPanel.this.NextButton)) {
                return CheckInPanel.this.BookField;
            }
            return CheckInPanel.this.BookField;
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot,
                                      Component aComponent) {
            if (aComponent.equals(CheckInPanel.this.BookField)) {
                return CheckInPanel.this.NextButton;
            } else if (aComponent.equals(CheckInPanel.this.NextButton)) {
                return CheckInPanel.this.CheckinButton;
            } else if (aComponent.equals(CheckInPanel.this.CheckinButton)) {
                return CheckInPanel.this.BookField;
            }
            return CheckInPanel.this.BookField;
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return CheckInPanel.this.BookField;
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return CheckInPanel.this.NextButton;
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return CheckInPanel.this.BookField;
        }
    }

    static class CheckinConnectionFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = 4810801364830454236L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class CheckinFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = -4361861311241698925L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class LockFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = 8668156526646532613L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class InvalidItemBarcode extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = -171902407687933826L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class RepeatedOrTooShortItemId extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = -621643592923459839L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    private static Log log = LogFactory.getLog(CheckOutPanel.class);

    private JPanel NavigationPanel = new JPanel();
    private BorderLayout BookBorderLayout = new BorderLayout();
    private JButton NextButton = new JButton();
    private JButton CheckoutButton = new JButton();
    private BorderLayout NavigationBorderLayout = new BorderLayout();
    private JPanel InformationPanel = new JPanel();
    private JLabel BookFieldLabel = new JLabel();
    private JPanel DataPanel = new JPanel();
    private BorderLayout InformationBorderLayout = new BorderLayout();
    private JTextField BookField = new JTextField(Configuration.getIntProperty("UI/CheckInPanel/BookField_Length", 8));
    private JPanel ResponsePanel = new JPanel();
    private BorderLayout ResponseBorderLayout = new BorderLayout();
    private Border border1;
    private Border border2;
    private JLabel BooksIcon = new JLabel();
    private Border border3;
    private JPanel ResponseTextPanel = new JPanel();
    private JTextPane PatronText = new JTextPane();
    private BorderLayout ResponseTextBorderLayout = new BorderLayout();
    private JButton CheckinButton = new JButton();
    private FlowLayout DataFlowLayout = new FlowLayout();
    private javax.swing.Timer ResetTimer;
    private String lastEnteredId = "";
    private String lastCheckedInId = "";
    private JScrollPane CheckInScrollPane = new JScrollPane();
    private JEditorPane CheckinText = new JEditorPane();
    private JTextPane StatusText = new JTextPane();

    private CirculationHandler handler;

    private String PatronID;

    private String PatronPassword;

    private String PatronName;

    private Boolean CheckOutEnabled;

    public CheckInPanel() {
    }

    public CheckInPanel(CirculationHandler handler, String PatronID, String PatronPassword, String PatronName, String message, javax.swing.Timer ResetTimer) {
        try {
            this.handler = handler;
            this.PatronID = PatronID;
            this.PatronPassword = PatronPassword;
            this.PatronName = (PatronName != null) ? PatronName : "Staff User";
            this.CheckOutEnabled = (PatronName != null);
            this.ResetTimer = ResetTimer;
            this.jbInit();
            this.PatronText.setText(Configuration.getMessage("GreetPatronCheckin", new String[] { this.PatronName, ((message == null) ? "" : message) }));
            ResetTimer.restart();
            this.enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
            this.startItemIDReader();
        } catch (Exception e) {
            CheckInPanel.log.fatal("Checkin Panel Failure: " + e.getMessage() + " - " +
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

        this.border1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.border2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.border3 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.setLayout(this.BookBorderLayout);
        this.setOpaque(true);
        this.setBackground(BackgroundColour);
        this.NextButton.setFont(ButtonTextFont);
        this.NextButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookPanelNextButton_ToolTipText"));
        this.NextButton.setText(Configuration.getProperty("UI/CheckInPanel/BookPanelNextButton_Text"));
        this.NextButton.addActionListener(new CheckInPanel_NextButton_actionAdapter(this));
        this.NextButton.setForeground(ButtonTextColour);
        this.NextButton.setBackground(ButtonBackgroundColour);
        this.NextButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));                
        this.CheckoutButton.setFont(ButtonTextFont);
        this.CheckoutButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookPanelCheckoutButton_ToolTipText"));
        this.CheckoutButton.setText(Configuration.getProperty("UI/CheckInPanel/BookPanelCheckoutButton_Text"));
        this.CheckoutButton.addActionListener(new CheckInPanel_CheckoutButton_actionAdapter(this));
        this.CheckoutButton.setVisible(this.CheckOutEnabled);
        this.CheckoutButton.setForeground(ButtonTextColour);
        this.CheckoutButton.setBackground(ButtonBackgroundColour);
        this.CheckoutButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));                
        this.NavigationPanel.setLayout(this.NavigationBorderLayout);
        this.BookFieldLabel.setFont(LabelTextFont);
        this.BookFieldLabel.setForeground(LabelTextColour);
        this.BookFieldLabel.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookFieldLabel_ToolTipText"));
        this.BookFieldLabel.setLabelFor(this.BookField);
        this.BookFieldLabel.setText(Configuration.getProperty("UI/CheckInPanel/BookFieldLabel_Text"));
        this.InformationPanel.setLayout(this.InformationBorderLayout);
        this.InformationPanel.setOpaque(false);
        this.BookField.setFont(InputTextFont);
        this.BookField.setForeground(InputTextColour);
        this.BookField.setBackground(InputBackgroundColour);
        this.BookField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(InputBorderColour,2),
                inset));
        this.BookField.setSelectionColor(InputSelectionColour);
        this.BookField.setSelectedTextColor(InputSelectedTextColour);
        this.BookField.setCaretColor(InputCaretColour);
        this.BookField.setDisabledTextColor(InputDisabledTextColour);          
        this.BookField.setRequestFocusEnabled(true);
        this.BookField.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookField_ToolTipText"));
        this.BookField.setText("");
        this.BookField.setHorizontalAlignment(SwingConstants.LEADING);
        this.BookField.addKeyListener(new CheckInPanel_BookField_keyAdapter(this));
        this.DataPanel.setLayout(this.DataFlowLayout);
        this.ResponsePanel.setLayout(this.ResponseBorderLayout);
        this.ResponsePanel.setOpaque(false);
        this.NavigationPanel.setBorder(this.border1);
        this.NavigationPanel.setOpaque(false);
        this.DataPanel.setBorder(this.border2);
        this.DataPanel.setOpaque(false);
        this.BooksIcon.setText("");
        this.BooksIcon.setIcon(Configuration.LoadImage("UI/CheckInPanel/BooksIcon"));
        this.ResponsePanel.setBorder(this.border3);
        this.ResponsePanel.setOpaque(false);
        this.PatronText.setFont(DefaultTextFont);
        this.PatronText.setForeground(DefaultTextColour);
        this.PatronText.setBackground(BackgroundColour);
        this.PatronText.setOpaque(true);
        this.PatronText.setRequestFocusEnabled(false);
        this.PatronText.setToolTipText(Configuration.getProperty("UI/CheckInPanel/PatronText_ToolTipText"));
        this.PatronText.setPreferredSize(new Dimension(Configuration.pt2Pixel(DefaultTextFont.getSize())*32, Configuration.pt2Pixel(DefaultTextFont.getSize())*2));
        this.PatronText.setEditable(false);
        HTMLEditorKit PatronTextHtml = new HTMLEditorKit();
        PatronTextHtml.getStyleSheet().addRule(
            "body {font-family: " + DefaultTextFont.getFamily() + "; " +
                "font-size: " + DefaultTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(DefaultTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        PatronTextHtml.getStyleSheet().addRule(
            "em {font-family: " + StatusTextFont.getFamily() + "; " +
                "font-size: " + StatusTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(StatusTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");        
        PatronTextHtml.getStyleSheet().addRule(
            "strong {font-family: " + WarningTextFont.getFamily() + "; " +
                "font-size: " + WarningTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(WarningTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        this.PatronText.setEditorKit(PatronTextHtml);
        this.PatronText.setContentType("text/html");        
        this.PatronText.setText("");
        this.PatronText.setBorder(null);
        this.ResponseTextPanel.setLayout(this.ResponseTextBorderLayout);
        this.ResponseTextPanel.setOpaque(true);
        this.ResponseTextPanel.setBackground(BackgroundColour);
        this.CheckinButton.setFont(ButtonTextFont);
        this.CheckinButton.setText(Configuration.getProperty("UI/CheckInPanel/CheckinButton_Text"));
        this.CheckinButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/CheckinButton_ToolTipText"));
        this.CheckinButton.addActionListener(new CheckInPanel_CheckinButton_actionAdapter(this));
        this.CheckinButton.setForeground(ButtonTextColour);
        this.CheckinButton.setBackground(ButtonBackgroundColour);
        this.CheckinButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));                        
        this.CheckInScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.CheckInScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.CheckInScrollPane.setAutoscrolls(true);
        this.CheckInScrollPane.setBorder(null);
        this.CheckInScrollPane.setBackground(BackgroundColour);
        this.CheckInScrollPane.setOpaque(true);
        this.CheckinText.setFont(DefaultTextFont);
        this.CheckinText.setForeground(DefaultTextColour);
        this.CheckinText.setBackground(BackgroundColour);
        this.CheckinText.setBorder(null);
        this.CheckinText.setOpaque(true);
        this.CheckinText.setRequestFocusEnabled(false);
        this.CheckinText.setEditable(false);
        HTMLEditorKit CheckinTextHtml = new HTMLEditorKit();
        CheckinTextHtml.getStyleSheet().addRule(
            "body {font-family: " + DefaultTextFont.getFamily() + "; " +
                "font-size: " + DefaultTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(DefaultTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        CheckinTextHtml.getStyleSheet().addRule(
            "em {font-family: " + StatusTextFont.getFamily() + "; " +
                "font-size: " + StatusTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(StatusTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");        
        CheckinTextHtml.getStyleSheet().addRule(
            "strong {font-family: " + WarningTextFont.getFamily() + "; " +
                "font-size: " + WarningTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(WarningTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        this.CheckinText.setEditorKit(CheckinTextHtml);
        this.CheckinText.setContentType("text/html");
        this.CheckinText.setToolTipText(Configuration.getProperty("UI/CheckInPanel/CheckInText_ToolTipText"));
        this.CheckinText.setText("");
        this.StatusText.setFont(StatusTextFont);
        this.StatusText.setForeground(StatusTextColour);
        this.StatusText.setBackground(BackgroundColour);
        this.StatusText.setOpaque(true);
        this.StatusText.setPreferredSize(new Dimension(Configuration.pt2Pixel(StatusTextFont.getSize())*16, Configuration.pt2Pixel(StatusTextFont.getSize())));
        this.StatusText.setToolTipText(Configuration.getProperty("UI/CheckInPanel/StatusText_ToolTipText"));
        this.StatusText.setText("");
        this.StatusText.setRequestFocusEnabled(false);
        this.StatusText.setEditable(false);
        HTMLEditorKit StatusTextHtml = new HTMLEditorKit();
        StatusTextHtml.getStyleSheet().addRule(
            "body {font-family: " + DefaultTextFont.getFamily() + "; " +
                "font-size: " + DefaultTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(DefaultTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        StatusTextHtml.getStyleSheet().addRule(
            "em {font-family: " + StatusTextFont.getFamily() + "; " +
                "font-size: " + StatusTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(StatusTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");        
        StatusTextHtml.getStyleSheet().addRule(
            "strong {font-family: " + WarningTextFont.getFamily() + "; " +
                "font-size: " + WarningTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(WarningTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        this.StatusText.setEditorKit(StatusTextHtml);
		this.StatusText.setContentType("text/html");
		this.add(this.NavigationPanel, BorderLayout.SOUTH);
        this.NavigationPanel.add(this.CheckoutButton, BorderLayout.WEST);
        this.NavigationPanel.add(this.NextButton, BorderLayout.EAST);
        this.add(this.InformationPanel, BorderLayout.CENTER);
        this.InformationPanel.add(this.DataPanel, BorderLayout.SOUTH);
        this.DataPanel.add(this.BookFieldLabel, null);
        this.DataPanel.add(this.BookField, null);
        this.DataPanel.add(this.CheckinButton, null);
        this.InformationPanel.add(this.ResponsePanel, BorderLayout.CENTER);
        this.ResponsePanel.add(this.BooksIcon, BorderLayout.EAST);
        this.ResponsePanel.add(this.ResponseTextPanel, BorderLayout.CENTER);
        this.ResponseTextPanel.add(this.PatronText, BorderLayout.NORTH);
        this.ResponseTextPanel.add(this.CheckInScrollPane, BorderLayout.CENTER);
        this.ResponseTextPanel.add(this.StatusText, BorderLayout.SOUTH);
        this.CheckInScrollPane.getViewport().add(this.CheckinText, null);
        CheckInPanelFocusTraversalPolicy policy = new CheckInPanelFocusTraversalPolicy();
        this.setFocusTraversalPolicy(policy);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalKeysEnabled(true);
        this.grabFocus();
    }

    void NextButton_actionPerformed(ActionEvent e) {
        this.stopItemIDReader();
        this.handler.printReceipt("Check-out Receipt for " + this.PatronName + " (" +
                         this.PatronID + ")\r\n\r\n" +
                         this.CheckinText.getText()
                         + "\r\n\r\n" + new Date());
        this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
    }
    
    private void appendCheckinText(String entry) {
        String msg = this.CheckinText.getText();
        if (msg == null) {
            msg = "";
        }
        msg = this.stripHTML(msg) + "<p>" + entry + "</p>";
        this.CheckinText.setText(msg);
    }

    private void reportSuccess(CheckIn request, CheckInResponse response) {
		this.PlaySound("CheckinSuccess");
		this.appendCheckinText(Configuration.getMessage("CheckInSuccess",
        	new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
            	SelfIssuePanel.escapeHTML(response.getTitleIdentifier()) :
				SelfIssuePanel.escapeHTML(response.getItemIdentifier()),
				StringUtils.isNotEmpty(response.getScreenMessage()) ?
				SelfIssuePanel.escapeHTML(response.getScreenMessage()) : "" }
		));
		this.lastCheckedInId = request.getItemIdentifier();		
    }

    void CheckoutButton_actionPerformed(ActionEvent e) {
        this.stopItemIDReader();
        this.lastCheckedInId = "";
        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckOutPanel.class);
        // Need this to pass back id and password
        PatronInformation rq = new PatronInformation();
        rq.setPatronPassword(this.PatronPassword);
        PatronInformationResponse rp = new PatronInformationResponse();
        rp.setPatronIdentifier(this.PatronID);
        rp.setPersonalName(this.PatronName);
        ev.request = rq;
        ev.response = rp;
        this.firePanelChange(ev);
	}
    
    void CheckinButton_actionPerformed(ActionEvent e) {
        CheckIn request = new CheckIn();
        CheckInResponse response = null;
        String finalStatusText = "";

        if (Configuration.getBoolProperty("Systems/Modes/EnableBarcodeAliases")) {
            if (this.BookField.getText().equals("$ESCAPE%")) {
                this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
            }
        }

        try {
            this.ResetTimer.stop();
            this.stopItemIDReader();
            this.BookField.setEditable(true);
            this.BookField.setEnabled(true);
            request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
            request.setTerminalPassword(Configuration.getProperty("Systems/SIP/TerminalPassword"));
            request.setItemIdentifier(CheckInPanel.strim(this.BookField.getText()));
            request.setTransactionDate(new Date());
            request.setReturnDate(new Date());
            request.setNoBlock(false);
            if (StringUtils.isEmpty(request.getItemIdentifier()) ||
                    request.getItemIdentifier().equals(this.lastCheckedInId)) {
                throw new RepeatedOrTooShortItemId();
            }
            if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
                throw new InvalidItemBarcode();
            }
            this.StatusText.setText(Configuration.getMessage("CheckinPendingMessage", 
                    new String[] { request.getItemIdentifier() }));
            try {
                this.StatusText.paint(this.StatusText.getGraphics());
            } catch (Exception ex) {
                CheckInPanel.log.warn("Error during redraw", ex);
            }
            try {
                response = (CheckInResponse) this.handler.send(request);
            } catch (java.lang.ClassCastException ex) {
                response = null;
            }
            if (response == null) {
                throw new CheckinConnectionFailed();
            }
            if (!((response.isOk() != null) ? response.isOk() : false)) {
                throw new CheckinFailed();
            } else {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKINSUCCESS, "", new Date(), request, response);
            }

            try {
                this.lockItem();
            } catch (TimeoutException | FailureException ex) {
                throw new LockFailed();
            }
            this.reportSuccess(request, response);
		} catch (RepeatedOrTooShortItemId ex) {
            // don't need to do anything for this - just let if fall through
        } catch (InvalidItemBarcode ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Invalid Barcode Entered", new Date(), request, response);
            this.PlaySound("InvalidItemBarcode");
            finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {request.getItemIdentifier()});
        } catch (CheckinConnectionFailed ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "Network Connection Failure", new Date(), request, response);
            this.PlaySound("CheckinNetworkError");
            this.appendCheckinText(Configuration.getMessage(
                    "CheckinNetworkError", new String[] {
                    request.getItemIdentifier() }));
        } catch (CheckinFailed ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "Server refused checkin", new Date(), request, response);
            this.PlaySound("CheckinFailedError");
            this.appendCheckinText(Configuration.getMessage(
                    "CheckinFailedError",
                    new String[] {  StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                                    response.getTitleIdentifier() :
                                    response.getItemIdentifier(), StringUtils.isNotEmpty(response.getScreenMessage()) ?
                                    response.getScreenMessage() : "" }));
        } catch (LockFailed ex) {
//TODO: Check this - does it need a checkin undo?        	
            this.handler.recordEvent(OnlineLogEvent.STATUS_LOCKFAILURE, "", new Date(), request, response);
            this.PlaySound("LockFailedError");
            this.appendCheckinText(Configuration.getMessage("LockFailedError",
                    new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                            response.getTitleIdentifier() :
                            response.getItemIdentifier() }));
        } catch (Exception ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "Unexpected checkin error!", new Date(), request, response);
            this.PlaySound("UnexpectedCheckinError");
            this.appendCheckinText(Configuration.getMessage(
                    "UnexpectedCheckinError", new String[] {
                    request.getItemIdentifier()
            }));
            CheckInPanel.log.fatal("Unexpected checkin failure: " + ex.getMessage() + " - " +
			                    Arrays.toString(ex.getStackTrace()), ex);
        }
        this.lastEnteredId = request.getItemIdentifier();
        this.StatusText.setText(finalStatusText);
        this.BookField.setText("");
        this.BookField.requestFocus();
        this.BookField.setEditable(true);
        this.BookField.setEnabled(true);
        this.startItemIDReader();
        this.ResetTimer.start();
    }


    private static String strim(String string) {
        String intermediate = string.trim();
        if (Configuration.getBoolProperty("UI/Advanced/StripItemChecksumDigit")) {
            if (StringUtils.isNotEmpty(intermediate)) {
                intermediate = intermediate.substring(0, intermediate.length() - 1);
            }
        }
        return intermediate;
    }

    void BookField_keyTyped(KeyEvent e) {
        this.ResetTimer.restart();
        /**
         * if (e.getKeyChar() == '' && this.CheckOutEnabled) { e.consume();
         * this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, "")); }
         */
        if (e.getKeyChar() == '\u001B') {
            e.consume();
            this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
        }
        if ((e.getKeyChar() == '\n') || (e.getKeyChar() == '^')) {
            e.consume();
            this.CheckinButton_actionPerformed(new ActionEvent(this, 0, ""));
        }
    }

    @Override
    public void grabFocus() {
        super.grabFocus();
        this.BookField.grabFocus();
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        this.BookField.requestFocus();
    }

    private void startItemIDReader() {
        this.handler.initIDReaderDevice(CirculationHandler.IDReaderDeviceType.ITEM_IDREADER);
        this.handler.startIDReaderDevice(this);
    }

    private void stopItemIDReader() {
        this.handler.stopIDReaderDevice();
        this.handler.deinitIDReaderDevice();
    }

    private void lockItem() throws TimeoutException, FailureException {
        this.handler.initItemSecurityDevice();
        this.handler.unlockItem();
        this.handler.deinitItemSecurityDevice();
    }

    Stack<String> repeatPreventer = new Stack<>();
    
    @Override
    public void autoInputData(String serial, String passcode) {
        if (!repeatPreventer.contains(serial)) {
            repeatPreventer.push(serial);
            this.BookField.setText(serial);
            this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
        }
    }
}

class CheckInPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
    private CheckInPanel adaptee;

    CheckInPanel_NextButton_actionAdapter(CheckInPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.NextButton_actionPerformed(e);
    }
}

class CheckInPanel_CheckoutButton_actionAdapter implements java.awt.event.ActionListener {
    private CheckInPanel adaptee;

    CheckInPanel_CheckoutButton_actionAdapter(CheckInPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckoutButton_actionPerformed(e);
    }
}

class CheckInPanel_CheckinButton_actionAdapter implements java.awt.event.ActionListener {
    private final CheckInPanel adaptee;

    CheckInPanel_CheckinButton_actionAdapter(CheckInPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckinButton_actionPerformed(e);
    }
}

class CheckInPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
    private CheckInPanel adaptee;

    CheckInPanel_BookField_keyAdapter(CheckInPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.adaptee.BookField_keyTyped(e);
    }
}
