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
import com.ceridwen.circulation.SIP.messages.CheckOut;
import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
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

public class CheckOutPanel extends SelfIssuePanel implements IDReaderDeviceListener {
    /**
	 * 
	 */

    /**
	 * 
	 */
    private static final long serialVersionUID = 2719843299250704940L;

    /**
	 * 
	 */

    public class CheckOutPanelFocusTraversalPolicy
                extends FocusTraversalPolicy {

        @Override
        public Component getComponentAfter(Container focusCycleRoot,
                                          Component aComponent) {
            if (aComponent.equals(CheckOutPanel.this.BookField)) {
                return CheckOutPanel.this.CheckoutButton;
            } else if (aComponent.equals(CheckOutPanel.this.CheckoutButton)) {
                return CheckOutPanel.this.NextButton;
            } else if (aComponent.equals(CheckOutPanel.this.NextButton)) {
                return CheckOutPanel.this.BookField;
            }
            return CheckOutPanel.this.BookField;
        }

        @Override
        public Component getComponentBefore(Container focusCycleRoot,
                                      Component aComponent) {
            if (aComponent.equals(CheckOutPanel.this.BookField)) {
                return CheckOutPanel.this.NextButton;
            } else if (aComponent.equals(CheckOutPanel.this.NextButton)) {
                return CheckOutPanel.this.CheckoutButton;
            } else if (aComponent.equals(CheckOutPanel.this.CheckoutButton)) {
                return CheckOutPanel.this.BookField;
            }
            return CheckOutPanel.this.BookField;
        }

        @Override
        public Component getDefaultComponent(Container focusCycleRoot) {
            return CheckOutPanel.this.BookField;
        }

        @Override
        public Component getLastComponent(Container focusCycleRoot) {
            return CheckOutPanel.this.NextButton;
        }

        @Override
        public Component getFirstComponent(Container focusCycleRoot) {
            return CheckOutPanel.this.BookField;
        }
    }

    static class CheckinConnectionFailed extends Exception {
        /**
	 * 
	 */

        /**
	 * 
	 */
        private static final long serialVersionUID = -9174095569381531669L;

        /**
	 * 
	 */

        public CheckinConnectionFailed(String message) {
            super(message);
        }
    }

    static class CheckoutConnectionFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = 938940430814523317L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class CheckoutFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = 7053811637076480863L;

        /**
	 * 
	 */

        /**
	 * 
	 */

    }

    static class UnlockFailed extends Exception {

        /**
	 * 
	 */
        private static final long serialVersionUID = -4113648518632457004L;

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
        private static final long serialVersionUID = 8746850315375610829L;

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
        private static final long serialVersionUID = -621647592912309839L;

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
    private JButton CheckInButton = new JButton();
    private BorderLayout NavigationBorderLayout = new BorderLayout();
    private JPanel InformationPanel = new JPanel();
    private JLabel BookFieldLabel = new JLabel();
    private JPanel DataPanel = new JPanel();
    private BorderLayout InformationBorderLayout = new BorderLayout();
    private JTextField BookField = new JTextField(Configuration.getIntProperty("UI/CheckOutPanel/BookField_Length", 8));
    private JPanel ResponsePanel = new JPanel();
    private BorderLayout ResponseBorderLayout = new BorderLayout();
    private Border border1;
    private Border border2;
    private JLabel BooksIcon = new JLabel();
    private Border border3;
    private JPanel ResponseTextPanel = new JPanel();
    private JTextPane PatronText = new JTextPane();
    private BorderLayout ResponseTextBorderLayout = new BorderLayout();
    private JButton CheckoutButton = new JButton();
    private FlowLayout DataFlowLayout = new FlowLayout();
    private javax.swing.Timer ResetTimer;
    private String lastEnteredId = "";
    private String lastCheckedOutId = "";
    private JScrollPane CheckOutScrollPane = new JScrollPane();
    private JEditorPane CheckoutText = new JEditorPane();
    private JTextPane StatusText = new JTextPane();

    private CirculationHandler handler;

    private String PatronID;

    private String PatronPassword;

    private String PatronName;

    private String PatronMessage;
        
    private Boolean CheckInEnabled;

    public CheckOutPanel() {
    }

    public CheckOutPanel(CirculationHandler handler, String PatronID, String PatronPassword, String PatronName, String message, javax.swing.Timer ResetTimer) {
        try {
            this.handler = handler;
            this.PatronID = PatronID;
            this.PatronPassword = PatronPassword;
	    this.PatronMessage = (message == null) ? "" : message;
            this.PatronName = PatronName;
            this.CheckInEnabled = Configuration.getBoolProperty("Systems/Modes/EnableCheckIn");
            this.ResetTimer = ResetTimer;
            this.jbInit();
            ResetTimer.restart();
            this.enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
            this.startItemIDReader();
        } catch (Exception e) {
            CheckOutPanel.log.fatal("CheckOut Panel Failure: " + e.getMessage() + " - " +
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
        this.NextButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookPanelNextButton_ToolTipText"));
        this.NextButton.setText(Configuration.getProperty("UI/CheckOutPanel/BookPanelNextButton_Text"));
        this.NextButton.addActionListener(new CheckOutPanel_NextButton_actionAdapter(this));
        this.NextButton.setForeground(ButtonTextColour);
        this.NextButton.setBackground(ButtonBackgroundColour);
        this.NextButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));                
        this.CheckInButton.setFont(ButtonTextFont);
        this.CheckInButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookPanelCheckinButton_ToolTipText"));
        this.CheckInButton.setText(Configuration.getProperty("UI/CheckOutPanel/BookPanelCheckinButton_Text"));
        this.CheckInButton.addActionListener(new CheckOutPanel_CheckinButton_actionAdapter(this));
        this.CheckInButton.setVisible(this.CheckInEnabled);
        this.CheckInButton.setForeground(ButtonTextColour);
        this.CheckInButton.setBackground(ButtonBackgroundColour);
        this.CheckInButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));        
        this.NavigationPanel.setLayout(this.NavigationBorderLayout);
        this.BookFieldLabel.setFont(LabelTextFont);
        this.BookFieldLabel.setForeground(LabelTextColour);
        this.BookFieldLabel.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookFieldLabel_ToolTipText"));
        this.BookFieldLabel.setLabelFor(this.BookField);
        this.BookFieldLabel.setText(Configuration.getProperty("UI/CheckOutPanel/BookFieldLabel_Text"));
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
        this.BookField.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookField_ToolTipText"));
        this.BookField.setText("");
        this.BookField.setHorizontalAlignment(SwingConstants.LEADING);
        this.BookField.addKeyListener(new CheckOutPanel_BookField_keyAdapter(this));
        this.DataPanel.setLayout(this.DataFlowLayout);
        this.ResponsePanel.setLayout(this.ResponseBorderLayout);
        this.ResponsePanel.setOpaque(false);
        this.NavigationPanel.setBorder(this.border1);
        this.NavigationPanel.setOpaque(false);
        this.DataPanel.setBorder(this.border2);
        this.DataPanel.setOpaque(false);
        this.BooksIcon.setText("");
        this.BooksIcon.setIcon(Configuration.LoadImage("UI/CheckOutPanel/BooksIcon"));
        this.ResponsePanel.setBorder(this.border3);
        this.ResponsePanel.setOpaque(false);
        this.PatronText.setFont(DefaultTextFont);
        this.PatronText.setForeground(DefaultTextColour);
        this.PatronText.setBackground(BackgroundColour);
        this.PatronText.setOpaque(true);
        this.PatronText.setRequestFocusEnabled(false);
        this.PatronText.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/PatronText_ToolTipText"));
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
        this.PatronText.setBorder(null);
        this.ResponseTextPanel.setLayout(this.ResponseTextBorderLayout);
        this.ResponseTextPanel.setOpaque(true);
        this.ResponseTextPanel.setBackground(BackgroundColour);
        this.CheckoutButton.setFont(ButtonTextFont);
        this.CheckoutButton.setText(Configuration.getProperty("UI/CheckOutPanel/CheckoutButton_Text"));
        this.CheckoutButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/CheckoutButton_ToolTipText"));
        this.CheckoutButton.addActionListener(new CheckOutPanel_CheckoutButton_actionAdapter(this));
        this.CheckoutButton.setForeground(ButtonTextColour);
        this.CheckoutButton.setBackground(ButtonBackgroundColour);
        this.CheckoutButton.setBorderPainted(Configuration.getBoolProperty("UI/Styling/ButtonBorder"));                
        this.CheckOutScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.CheckOutScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.CheckOutScrollPane.setAutoscrolls(true);
        this.CheckOutScrollPane.setBorder(null);
        this.CheckOutScrollPane.setBackground(BackgroundColour);
        this.CheckOutScrollPane.setOpaque(true);
        this.CheckoutText.setFont(DefaultTextFont);
        this.CheckoutText.setForeground(DefaultTextColour);
        this.CheckoutText.setBackground(BackgroundColour);
        this.CheckoutText.setBorder(null);
        this.CheckoutText.setOpaque(true);
        this.CheckoutText.setRequestFocusEnabled(false);
        this.CheckoutText.setEditable(false);
        HTMLEditorKit CheckoutTextHtml = new HTMLEditorKit();
        CheckoutTextHtml.getStyleSheet().addRule(
            "body {font-family: " + DefaultTextFont.getFamily() + "; " +
                "font-size: " + DefaultTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(DefaultTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        CheckoutTextHtml.getStyleSheet().addRule(
            "em {font-family: " + StatusTextFont.getFamily() + "; " +
                "font-size: " + StatusTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(StatusTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");        
        CheckoutTextHtml.getStyleSheet().addRule(
            "strong {font-family: " + WarningTextFont.getFamily() + "; " +
                "font-size: " + WarningTextFont.getSize() + "pt; " +
                "font-style: normal; " +
                "color: " + Configuration.colorEncode(WarningTextColour) + "; " + 
                "background-color: " + Configuration.colorEncode(BackgroundColour) + ";}");
        this.CheckoutText.setEditorKit(CheckoutTextHtml);
        this.CheckoutText.setContentType("text/html");
        this.CheckoutText.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/CheckOutText_ToolTipText"));
        this.StatusText.setFont(StatusTextFont);
        this.StatusText.setForeground(StatusTextColour);
        this.StatusText.setBackground(BackgroundColour);
        this.StatusText.setOpaque(true);
        this.StatusText.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/StatusText_ToolTipText"));
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
        this.NavigationPanel.add(this.CheckInButton, BorderLayout.WEST);
        this.NavigationPanel.add(this.NextButton, BorderLayout.EAST);
        this.add(this.InformationPanel, BorderLayout.CENTER);
        this.InformationPanel.add(this.DataPanel, BorderLayout.SOUTH);
        this.DataPanel.add(this.BookFieldLabel, null);
        this.DataPanel.add(this.BookField, null);
        this.DataPanel.add(this.CheckoutButton, null);
        this.InformationPanel.add(this.ResponsePanel, BorderLayout.CENTER);
        this.ResponsePanel.add(this.BooksIcon, BorderLayout.EAST);
        this.ResponsePanel.add(this.ResponseTextPanel, BorderLayout.CENTER);
        this.ResponseTextPanel.add(this.PatronText, BorderLayout.NORTH);
        this.ResponseTextPanel.add(this.CheckOutScrollPane, BorderLayout.CENTER);
        this.ResponseTextPanel.add(this.StatusText, BorderLayout.SOUTH);
        this.CheckOutScrollPane.getViewport().add(this.CheckoutText, null);
        this.setPatronText(Configuration.getMessage("GreetPatronCheckout", new String[] { this.PatronName, this.PatronMessage }));
        this.setStatusText("");
        CheckOutPanelFocusTraversalPolicy policy = new CheckOutPanelFocusTraversalPolicy();
        this.setFocusTraversalPolicy(policy);
        this.setFocusCycleRoot(true);
        this.setFocusTraversalKeysEnabled(true);
        this.grabFocus();
    }

    void NextButton_actionPerformed(ActionEvent e) {
        this.stopItemIDReader();
        this.handler.printReceipt("Check-out Receipt for " + this.PatronName + " (" +
                         this.PatronID + ")\r\n\r\n" +
                         this.CheckoutText.getText()
                         + "\r\n\r\n" + new Date());
        this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
    }
    
    private void setPatronText(String patron) {
        PatronText.setText("<p>" + patron + "&nbsp;"); // formatting fix!
        PatronText.invalidate();
        ResponseTextPanel.validate();        
    }
    
    private void setStatusText(String status) {
        StatusText.setText("<p>" + status + "&nbsp;"); // formatting fix!
        StatusText.invalidate();
        ResponseTextPanel.validate();
    }
    
    private void appendCheckoutText(String entry) {
        String msg = this.CheckoutText.getText();
        if (msg == null) {
            msg = "";
        }
        msg = this.stripHTML(msg) + "<p>" + entry + "</p>";
        this.CheckoutText.setText(msg);
    }

    private void reportSuccess(CheckOut request, CheckOutResponse response) {
        this.PlaySound("CheckoutSuccess");
        this.appendCheckoutText(Configuration.getMessage("CheckOutSuccess",
        	new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
            	SelfIssuePanel.escapeHTML(response.getTitleIdentifier()) :
                        SelfIssuePanel.escapeHTML(response.getItemIdentifier()) }) + " " +
						(StringUtils.isNotEmpty(response.getDueDate()) ?
                         Configuration.getMessage("DueDateMessage",
                         	new String[] {this.demangleDate(response.getDueDate()) }) :
                            Configuration.getMessage("NoDueDateMessage",new String[] {})
		));
        this.lastCheckedOutId = request.getItemIdentifier();
    }
    
    void CheckinButton_actionPerformed(ActionEvent e) {
        this.stopItemIDReader();
        this.lastCheckedOutId = "";
        SelfIssuePanelEvent ev = new SelfIssuePanelEvent(this, CheckInPanel.class);
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
    
    void CheckoutButton_actionPerformed(ActionEvent e) {
        CheckOut request = new CheckOut();
        CheckOutResponse response = null;
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
            request.setPatronIdentifier(this.PatronID);
            request.setPatronPassword(this.PatronPassword);
            request.setItemIdentifier(CheckOutPanel.strim(this.BookField.getText()));
            request.setSCRenewalPolicy(SelfIssuePanel.allowRenews || SelfIssuePanel.trustMode);
            request.setTransactionDate(new Date());
            if (SelfIssuePanel.trustMode) {
                request.setNoBlock(SelfIssuePanel.useNoBlock);
            }
            if (StringUtils.isEmpty(request.getItemIdentifier()) ||
                    request.getItemIdentifier().equals(this.lastCheckedOutId)) {
                throw new RepeatedOrTooShortItemId();
            }
            if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
                throw new InvalidItemBarcode();
            }
            this.setStatusText(Configuration.getMessage("CheckoutPendingMessage",
                    new String[] { request.getItemIdentifier() }));
            try {
                this.StatusText.paint(this.StatusText.getGraphics());
            } catch (Exception ex) {
                CheckOutPanel.log.warn("Error during redraw", ex);
            }
            try {
                response = (CheckOutResponse) this.handler.send(request);
            } catch (java.lang.ClassCastException ex) {
                response = null;
            }
            if (response == null) {
                if ((SelfIssuePanel.trustMode || SelfIssuePanel.allowOffline) &&
                        (!SelfIssuePanel.retryItemWhenError ||
                        request.getItemIdentifier().equals(this.lastEnteredId))) {
                    // Network failing so cache request and fake positive
                    // response
                    request.setTransactionDate(new Date());
                    request.setNoBlock(SelfIssuePanel.useNoBlock);
                    this.handler.spool(request);
                    response = new CheckOutResponse();
                    response.setItemIdentifier(request.getItemIdentifier());
                    response.setTitleIdentifier(request.getItemIdentifier());
                    response.setPatronIdentifier(request.getPatronIdentifier());
                    response.setDueDate("");
                    response.setOk(true);
                } else {
                    throw new CheckoutConnectionFailed();
                }
            }
            if (!((response.isOk() != null) ? response.isOk() : false)) {
                if (SelfIssuePanel.trustMode &&
                        (!SelfIssuePanel.retryItemWhenError ||
                        request.getItemIdentifier().equals(this.lastEnteredId))) {
                    // System checkout failed so report to tracking log and
                    // proceed as per success
                    this.handler.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", new Date(), request, response);
                } else {
                    throw new CheckoutFailed();
                }
            } else {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTSUCCESS, "", new Date(), request, response);
            }
            try {
                this.unlockItem();
            } catch (TimeoutException | FailureException ex) {
                throw new UnlockFailed();
            }
            this.reportSuccess(request, response);
		} catch (RepeatedOrTooShortItemId ex) {
            // don't need to do anything for this - just let if fall through
        } catch (InvalidItemBarcode ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Invalid Barcode Entered", new Date(), request, response);
            this.PlaySound("InvalidItemBarcode");
            finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {request.getItemIdentifier()});
        } catch (CheckoutConnectionFailed ex) {
            if (SelfIssuePanel.trustMode) {
                this.PlaySound("CheckoutRetry");
                finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
            } else {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Network Connection Failure", new Date(), request, response);
                this.PlaySound("CheckoutNetworkError");
                this.appendCheckoutText(Configuration.getMessage(
                        "CheckoutNetworkError", new String[] {
                        request.getItemIdentifier() }));
            }
        } catch (CheckoutFailed ex) {
            if (SelfIssuePanel.trustMode) {
                this.PlaySound("CheckoutRetry");
                finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
            } else {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Server refused checkout", new Date(), request, response);
                this.PlaySound("CheckoutFailedError");
                this.appendCheckoutText(Configuration.getMessage(
                        "CheckoutFailedError",
                        new String[] {  StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                                        response.getTitleIdentifier() :
                                        response.getItemIdentifier(), StringUtils.isNotEmpty(response.getScreenMessage()) ?
                                        response.getScreenMessage() : "" }));
            }
        } catch (UnlockFailed ex) {
            CheckInResponse checkinr = null;
            try {
                CheckIn checkin = new CheckIn();
                checkin.setItemIdentifier(request.getItemIdentifier());
                checkin.setCancel(true);
                checkinr = (CheckInResponse) this.handler.send(checkin);
                if (checkinr == null) {
                    throw new CheckinConnectionFailed("Null response");
                }
                if (!((checkinr.isOk() != null) ? checkinr.isOk() : false)) {
                    throw new CheckinConnectionFailed((checkinr.getScreenMessage() != null) ?
                                            checkinr.getScreenMessage() :
                                            "No message");
                }
                this.handler.recordEvent(OnlineLogEvent.STATUS_UNLOCKFAILURE, "", new Date(), request, response);
                this.PlaySound("UnlockFailedError");
                this.appendCheckoutText(Configuration.getMessage("UnlockFailedError",
                        new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                                response.getTitleIdentifier() :
                                response.getItemIdentifier() }));
            } catch (CheckinConnectionFailed ex1) {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE, "", new Date(), request, checkinr);
                if (SelfIssuePanel.suppressSecurityFailureMessages) {
                    this.reportSuccess(request, response);
                } else {
                    this.PlaySound("UnlockFailedCheckonFailedError");
                    this.appendCheckoutText(Configuration.getMessage(
                            "UnlockFailedCheckInFailedError",
                            new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                                    response.getTitleIdentifier() :
                                    response.getItemIdentifier() }));
                }
                CheckOutPanel.log.fatal("Check back in failure: " + ex1.getMessage() + " - " +
                        Arrays.toString(ex1.getStackTrace()), ex1);
            }
        } catch (Exception ex) {
            if (SelfIssuePanel.trustMode) {
                this.PlaySound("CheckoutRetry");
                finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
            } else {
                this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Unexpected checkout error!", new Date(), request, response);
                this.PlaySound("UnexpectedCheckoutError");
                this.appendCheckoutText(Configuration.getMessage(
                        "UnexpectedCheckoutError", new String[] {
                        request.getItemIdentifier()
                }));
            }
            CheckOutPanel.log.fatal("Unexpected checkout failure: " + ex.getMessage() + " - " +
			                    Arrays.toString(ex.getStackTrace()), ex);
        }
        this.lastEnteredId = request.getItemIdentifier();
        this.setStatusText(finalStatusText);
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
         * if (e.getKeyChar() == '' && this.CheckInEnabled) { e.consume();
         * this.CheckinButton_actionPerformed(new ActionEvent(this, 0, "")); }
         */
        if (e.getKeyChar() == '\u001B') {
            e.consume();
            this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
        }
        if ((e.getKeyChar() == '\n') || (e.getKeyChar() == '^')) {
            e.consume();
            this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
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

    private void unlockItem() throws TimeoutException, FailureException {
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

class CheckOutPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
    private final CheckOutPanel adaptee;

    CheckOutPanel_NextButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.NextButton_actionPerformed(e);
    }
}

class CheckOutPanel_CheckinButton_actionAdapter implements java.awt.event.ActionListener {
    private final CheckOutPanel adaptee;

    CheckOutPanel_CheckinButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckinButton_actionPerformed(e);
    }
}

class CheckOutPanel_CheckoutButton_actionAdapter implements java.awt.event.ActionListener {
    private final CheckOutPanel adaptee;

    CheckOutPanel_CheckoutButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckoutButton_actionPerformed(e);
    }
}

class CheckOutPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
    private final CheckOutPanel adaptee;

    CheckOutPanel_BookField_keyAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        this.adaptee.BookField_keyTyped(e);
    }
}
