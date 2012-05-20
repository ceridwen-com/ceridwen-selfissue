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
/**
 * <p>Title: Self Issue</p>
 * <p>Description: Self Issue Client</p>
 * <p>Copyright: 2004,</p>
 * <p>Company: ceridwen.com</p>
 * @author Matthew J. Dovey
 * @version 2.1
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
import javax.swing.JTextArea;
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

        public Component getDefaultComponent(Container focusCycleRoot) {
            return CheckOutPanel.this.BookField;
        }

        public Component getLastComponent(Container focusCycleRoot) {
            return CheckOutPanel.this.NextButton;
        }

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
    private JTextField BookField = new JTextField();
    private JPanel ResponsePanel = new JPanel();
    private BorderLayout ResponseBorderLayout = new BorderLayout();
    private Border border1;
    private Border border2;
    private JLabel BooksIcon = new JLabel();
    private Border border3;
    private JPanel ResponseTextPanel = new JPanel();
    private JTextArea PatronText = new JTextArea();
    private BorderLayout ResponseTextBorderLayout = new BorderLayout();
    private JButton CheckoutButton = new JButton();
    private FlowLayout DataFlowLayout = new FlowLayout();
    private String PatronID;
    private String PatronName;
    private javax.swing.Timer ResetTimer;
    private String lastEnteredId = "";
    private String lastCheckedOutId = "";
    private JScrollPane CheckOutScrollPane = new JScrollPane();
    // JTextArea CheckoutText = new JTextArea();
    private JEditorPane CheckoutText = new JEditorPane();
    private JLabel StatusText = new JLabel();

    private CirculationHandler handler;

    private String PatronPassword;

    private boolean CheckInEnabled;

    public CheckOutPanel() {
    }

    public CheckOutPanel(CirculationHandler handler, String PatronID, String PatronPassword, String PatronName, String message, javax.swing.Timer ResetTimer) {
        try {
            this.handler = handler;
            this.PatronID = PatronID;
            this.PatronPassword = PatronPassword;
            this.CheckInEnabled = Configuration.getBoolProperty("Modes/EnableCheckIn");
            this.PatronName = PatronName;
            this.ResetTimer = ResetTimer;
            this.jbInit();
            this.PatronText.setText(Configuration.getMessage("GreetPatronCheckout", new String[] { this.PatronName, ((message == null) ? "" : message) }));
            ResetTimer.restart();
            this.enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
            this.startItemIDReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        Color BackgroundColour = Configuration.getBackgroundColour("BackgroundColour");
        Color DefaultTextColour = Configuration.getForegroundColour("DefaultTextColour");
        Color WarningTextColour = Configuration.getForegroundColour("WarningTextColour");
        Color ButtonTextColour = Configuration.getForegroundColour("ButtonTextColour");
        Color ButtonBackgroundColour = Configuration.getBackgroundColour("ButtonBackgroundColour");

        this.border1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.border2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.border3 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        this.setLayout(this.BookBorderLayout);
        this.setOpaque(true);
        this.setBackground(BackgroundColour);
        this.NextButton.setFont(new java.awt.Font("Dialog", 1, 16));
        // NextButton.setNextFocusableComponent(BookField);
        this.NextButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookPanelNextButton_ToolTipText"));
        this.NextButton.setText(Configuration.getProperty("UI/CheckOutPanel/BookPanelNextButton_Text"));
        this.NextButton.addActionListener(new BookPanel_NextButton_actionAdapter(this));
        this.NextButton.setForeground(ButtonTextColour);
        this.NextButton.setBackground(ButtonBackgroundColour);
        this.CheckInButton.setFont(new java.awt.Font("Dialog", 1, 16));
        // ResetButton.setNextFocusableComponent(BookField);
        this.CheckInButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookPanelCheckinButton_ToolTipText"));
        this.CheckInButton.setText(Configuration.getProperty("UI/CheckOutPanel/BookPanelCheckinButton_Text"));
        this.CheckInButton.addActionListener(new BookPanel_CheckinButton_actionAdapter(this));
        this.CheckInButton.setVisible(this.CheckInEnabled);
        this.CheckInButton.setForeground(ButtonTextColour);
        this.CheckInButton.setBackground(ButtonBackgroundColour);
        this.NavigationPanel.setLayout(this.NavigationBorderLayout);
        this.BookFieldLabel.setFont(new java.awt.Font("Dialog", 1, 16));
        this.BookFieldLabel.setForeground(DefaultTextColour);
        this.BookFieldLabel.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookFieldLabel_ToolTipText"));
        this.BookFieldLabel.setLabelFor(this.BookField);
        this.BookFieldLabel.setText(Configuration.getProperty("UI/CheckOutPanel/BookFieldLabel_Text"));
        this.InformationPanel.setLayout(this.InformationBorderLayout);
        this.InformationPanel.setOpaque(false);
        this.BookField.setFont(new java.awt.Font("Dialog", 1, 16));
        this.BookField.setForeground(DefaultTextColour);
        this.BookField.setBackground(BackgroundColour);
        this.BookField.setMinimumSize(new Dimension(200, 27));
        // BookField.setNextFocusableComponent(CheckoutButton);
        this.BookField.setPreferredSize(new Dimension(200, 27));
        this.BookField.setRequestFocusEnabled(true);
        this.BookField.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/BookField_ToolTipText"));
        this.BookField.setText(Configuration.getProperty("UI/CheckOutPanel/BookField_DefaultText"));
        this.BookField.setHorizontalAlignment(SwingConstants.LEADING);
        this.BookField.addKeyListener(new BookPanel_BookField_keyAdapter(this));
        this.DataPanel.setLayout(this.DataFlowLayout);
        this.ResponsePanel.setLayout(this.ResponseBorderLayout);
        this.ResponsePanel.setOpaque(false);
        this.NavigationPanel.setBorder(this.border1);
        this.NavigationPanel.setOpaque(false);
        this.DataPanel.setBorder(this.border2);
        this.DataPanel.setOpaque(false);
        this.BooksIcon.setText("");
        this.BooksIcon.setIcon(Configuration.LoadImage("UI/CheckOutPanel/BooksIcon_Icon"));
        this.ResponsePanel.setBorder(this.border3);
        this.ResponsePanel.setOpaque(false);
        this.PatronText.setFont(new java.awt.Font("Dialog", 1, 18));
        this.PatronText.setForeground(DefaultTextColour);
        this.PatronText.setBackground(BackgroundColour);
        this.PatronText.setOpaque(true);
        this.PatronText.setRequestFocusEnabled(false);
        this.PatronText.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/PatronText_ToolTipText"));
        this.PatronText.setEditable(false);
        this.PatronText.setText(Configuration.getProperty("UI/CheckOutPanel/PatronText_DefaultText"));
        this.PatronText.setLineWrap(true);
        this.PatronText.setRows(2);
        this.PatronText.setBorder(null);
        // CheckoutText.setLineWrap(true);
        this.ResponseTextPanel.setLayout(this.ResponseTextBorderLayout);
        this.ResponseTextPanel.setOpaque(true);
        this.ResponseTextPanel.setBackground(BackgroundColour);
        this.CheckoutButton.setFont(new java.awt.Font("Dialog", 1, 16));
        // CheckoutButton.setNextFocusableComponent(NextButton);
        this.CheckoutButton.setText(Configuration.getProperty("UI/CheckOutPanel/CheckoutButton_Text"));
        this.CheckoutButton.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/CheckoutButton_ToolTipText"));
        this.CheckoutButton.addActionListener(new BookPanel_CheckoutButton_actionAdapter(this));
        this.CheckoutButton.setForeground(ButtonTextColour);
        this.CheckoutButton.setBackground(ButtonBackgroundColour);
        this.CheckOutScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.CheckOutScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.CheckOutScrollPane.setAutoscrolls(true);
        this.CheckOutScrollPane.setBorder(null);
        this.CheckOutScrollPane.setBackground(BackgroundColour);
        this.CheckOutScrollPane.setOpaque(true);
        this.CheckoutText.setFont(new java.awt.Font("Dialog", 1, 16));
        this.CheckoutText.setForeground(DefaultTextColour);
        this.CheckoutText.setBackground(BackgroundColour);
        this.CheckoutText.setBorder(null);
        this.CheckoutText.setOpaque(true);
        this.CheckoutText.setRequestFocusEnabled(false);
        this.CheckoutText.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        kit.getStyleSheet().addRule(
                "body {font-family: Dialog; font-size: 16pt; background-color:#" + Configuration.getProperty("UI/Palette/BackgroundColour") + ";}");
        kit.getStyleSheet().addRule("em {font-style: normal; color:#" + Configuration.getProperty("UI/Palette/WarningTextColour") + ";}");
        this.CheckoutText.setEditorKit(kit);
        this.CheckoutText.setContentType("text/html");
        // CheckoutText.setLineWrap(true);
        // CheckoutText.setWrapStyleWord(true);
        this.StatusText.setFont(new java.awt.Font("Dialog", 1, 18));
        this.StatusText.setForeground(WarningTextColour);
        this.StatusText.setBackground(BackgroundColour);
        this.StatusText.setMinimumSize(new Dimension(33, 15));
        this.StatusText.setOpaque(true);
        this.StatusText.setPreferredSize(new Dimension(0, 24));
        this.StatusText.setToolTipText(Configuration.getProperty("UI/CheckOutPanel/StatusText_ToolTipText"));
        this.StatusText.setText(Configuration.getProperty("UI/CheckOutPanel/StatusText_DefaultText"));
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

    void CheckinButton_actionPerformed(ActionEvent e) {
        this.stopItemIDReader();
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

    private void appendCheckoutText(String entry) {
        String msg = this.CheckoutText.getText();
        if (msg == null) {
            msg = "";
        }
        msg = this.stripHTML(msg) + entry + "<br>";
        this.CheckoutText.setText(msg.replaceAll("\r\n", "<br>"));
    }

    private void reportSuccess(CheckOut request, CheckOutResponse response) {
        this.PlaySound("CheckoutSuccess");
        this.appendCheckoutText(Configuration.getMessage("CheckOutSuccess",
                new String[] { StringUtils.isNotEmpty(response.getTitleIdentifier()) ?
                        SelfIssuePanel.escapeHTML(response.getTitleIdentifier()) :
                        SelfIssuePanel.escapeHTML(response.getItemIdentifier()) }) +
                             " " +
                             (StringUtils.isNotEmpty(response.getDueDate()) ?
                                     Configuration.
                                             getMessage("DueDateMessage",
                                                     new String[] {
                                                     this.demangleDate(response.getDueDate()) }) :
                                     Configuration.getMessage("NoDueDateMessage",
                                             new String[] {}))
                             );
        this.lastCheckedOutId = new String(request.getItemIdentifier());
    }

    void CheckoutButton_actionPerformed(ActionEvent e) {
        CheckOut request = new CheckOut();
        CheckOutResponse response = null;
        String finalStatusText = "";

        if (Configuration.getBoolProperty("Modes/EnableBarcodeAlii")) {
            if (this.BookField.getText().equals("$ESCAPE%")) {
                this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
            }
        }

        try {
            this.ResetTimer.stop();
            this.stopItemIDReader();
            this.BookField.setEditable(true);
            this.BookField.setEnabled(true);
/*
             try {
 
                this.DataPanel.paint(this.DataPanel.getGraphics());
            } catch (Exception ex) {
                CheckOutPanel.log.warn("Error during redraw", ex);
            }
*/
            request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
            request.setTerminalPassword(Configuration.getProperty("Systems/SIP/TerminalPassword"));
            request.setPatronIdentifier(this.PatronID);
            request.setPatronPassword(this.PatronPassword);
            request.setItemIdentifier(CheckOutPanel.strim(this.BookField.getText()));
            request.setSCRenewalPolicy(new Boolean(SelfIssuePanel.allowRenews || SelfIssuePanel.trustMode));
            request.setTransactionDate(new Date());
            if (SelfIssuePanel.trustMode) {
                request.setNoBlock(new Boolean(SelfIssuePanel.useNoBlock));
            }
            if (StringUtils.isEmpty(request.getItemIdentifier()) ||
                    request.getItemIdentifier().equals(this.lastCheckedOutId)) {
                throw new RepeatedOrTooShortItemId();
            }
            if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
                throw new InvalidItemBarcode();
            }
            this.StatusText.setText(Configuration.getMessage("CheckoutPendingMessage",
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
                    request.setNoBlock(new Boolean(SelfIssuePanel.useNoBlock));
                    this.handler.spool(request);
                    response = new CheckOutResponse();
                    response.setItemIdentifier(request.getItemIdentifier());
                    response.setTitleIdentifier(request.getItemIdentifier());
                    response.setPatronIdentifier(request.getPatronIdentifier());
                    response.setDueDate("");
                    response.setOk(new Boolean(true));
                } else {
                    throw new CheckoutConnectionFailed();
                }
            }

            if (!((response.isOk() != null) ? response.isOk().booleanValue() : false)) {
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
            } catch (TimeoutException ex) {
                throw new UnlockFailed();
            } catch (FailureException ex) {
                throw new UnlockFailed();
            }

            this.reportSuccess(request, response);
        } catch (RepeatedOrTooShortItemId ex) {
            // don't need to do anything for this - just let if fall through
        } catch (InvalidItemBarcode ex) {
            this.handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "Invalid Barcode Entered", new Date(), request, response);
            this.PlaySound("InvalidItemBarcode");
            finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {});
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
                checkin.setCancel(new Boolean(true));
                checkinr = (CheckInResponse) this.handler.send(checkin);
                if (checkinr == null) {
                    throw new CheckinConnectionFailed("Null response");
                }
                if (!((checkinr.isOk() != null) ? checkinr.isOk().booleanValue() : false)) {
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
            } catch (Exception ex1) {
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
                        ex1.getStackTrace(), ex1);
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
                    ex.getStackTrace(), ex);
        }
        this.lastEnteredId = new String(request.getItemIdentifier());
        this.StatusText.setText(finalStatusText);
        this.BookField.setText("");
        this.BookField.requestFocus();
/*
        try {
            this.DataPanel.paint(this.DataPanel.getGraphics());
        } catch (Exception ex) {
            CheckOutPanel.log.warn("Error during redraw", ex);
        }
        try {
            this.ResponseTextPanel.paint(this.ResponseTextPanel.getGraphics());
        } catch (Exception ex) {
            CheckOutPanel.log.warn("Error during redraw", ex);
        }
        try {
            this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
        } catch (Exception ex) {
            CheckOutPanel.log.warn("Error during redraw", ex);
        }
*/
        this.BookField.setEditable(true);
        this.BookField.setEnabled(true);
        this.startItemIDReader();
        this.ResetTimer.start();

    }

    private static String strim(String string) {
        String intermediate = string.trim();
        if (Configuration.getBoolProperty("UI/Control/StripItemChecksumDigit")) {
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

    public void grabFocus() {
        super.grabFocus();
        this.BookField.grabFocus();
    }

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

    protected void finalize() throws java.lang.Throwable {
        this.stopItemIDReader();
        super.finalize();
    }

    Stack<String> repeatPreventer = new Stack<String>();
    public void autoInputData(String serial, String passcode) {
        if (!repeatPreventer.contains(serial)) {
            repeatPreventer.push(serial);
            this.BookField.setText(serial);
            this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
        }
    }
}

class BookPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
    private CheckOutPanel adaptee;

    BookPanel_NextButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        this.adaptee.NextButton_actionPerformed(e);
    }
}

class BookPanel_CheckinButton_actionAdapter implements java.awt.event.ActionListener {
    private CheckOutPanel adaptee;

    BookPanel_CheckinButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckinButton_actionPerformed(e);
    }
}

class BookPanel_CheckoutButton_actionAdapter implements java.awt.event.ActionListener {
    private CheckOutPanel adaptee;

    BookPanel_CheckoutButton_actionAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void actionPerformed(ActionEvent e) {
        this.adaptee.CheckoutButton_actionPerformed(e);
    }
}

class BookPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
    private CheckOutPanel adaptee;

    BookPanel_BookField_keyAdapter(CheckOutPanel adaptee) {
        this.adaptee = adaptee;
    }

    public void keyTyped(KeyEvent e) {
        this.adaptee.BookField_keyTyped(e);
    }
}
