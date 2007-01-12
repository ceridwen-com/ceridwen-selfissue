/**
 * <p>Title: Self Issue</p>
 * <p>Description: Self Issue Client</p>
 * <p>Copyright: 2004,</p>
 * <p>Company: ceridwen.com</p>
 * @author Matthew J. Dovey
 * @version 2.1
 */

package com.ceridwen.selfissue.client.panels;

import java.util.Date;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.CheckIn;
import com.ceridwen.circulation.SIP.messages.CheckInResponse;
import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.log.OnlineLogEvent;


public class CheckInPanel extends SelfIssuePanel implements SecurityListener {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public class CheckInPanelFocusTraversalPolicy
                extends FocusTraversalPolicy {

       public Component getComponentAfter(Container focusCycleRoot,
                                          Component aComponent) {
           if (aComponent.equals(BookField)) {
               return CheckinButton;
           } else if (aComponent.equals(CheckinButton)) {
               return NextButton;
           } else if (aComponent.equals(NextButton)) {
               return BookField ;
           }
           return BookField;
       }

       public Component getComponentBefore(Container focusCycleRoot,
                                      Component aComponent) {
         if (aComponent.equals(BookField)) {
             return NextButton;
         } else if (aComponent.equals(NextButton)) {
             return CheckinButton;
         } else if (aComponent.equals(CheckinButton)) {
             return BookField;
         }
         return BookField;
       }

       public Component getDefaultComponent(Container focusCycleRoot) {
           return BookField;
       }

       public Component getLastComponent(Container focusCycleRoot) {
           return NextButton;
       }

       public Component getFirstComponent(Container focusCycleRoot) {
           return BookField;
       }
   }

  static class CheckinConnectionFailed extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  }
  static class CheckinFailed extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  }
  static class LockFailed extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  }
  static class InvalidItemBarcode extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  }

  private static Log log = LogFactory.getLog(CheckInPanel.class);

  private JPanel NavigationPanel = new JPanel();
  private BorderLayout BookBorderLayout = new BorderLayout();
  private JButton NextButton = new JButton();
  private JButton ResetButton = new JButton();
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
  private JButton CheckinButton = new JButton();
  private FlowLayout DataFlowLayout = new FlowLayout();
  private javax.swing.Timer ResetTimer;
  private String lastEnteredId = "";
  private String lastCheckedInId = "";
  private JScrollPane CheckInScrollPane = new JScrollPane();
//  JTextArea CheckoutText = new JTextArea();
  private JEditorPane CheckinText = new JEditorPane();
  private JLabel StatusText = new JLabel();

  private CirculationHandler handler;


  public CheckInPanel() {
  }

  public CheckInPanel(CirculationHandler handler, javax.swing.Timer ResetTimer) {
    try {
      this.handler = handler;
      this.ResetTimer = ResetTimer;
      jbInit();
      ResetTimer.restart();
      enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
      handler.securityDevice.start(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    border3 = BorderFactory.createEmptyBorder(10,10,10,10);
    this.setLayout(BookBorderLayout);
    NextButton.setFont(new java.awt.Font("Dialog", 1, 16));
//    NextButton.setNextFocusableComponent(BookField);
    NextButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookPanelNextButton_ToolTipText"));
    NextButton.setText(Configuration.getProperty("UI/CheckInPanel/BookPanelNextButton_Text"));
    NextButton.addActionListener(new CheckInPanel_NextButton_actionAdapter(this));
    ResetButton.setFont(new java.awt.Font("Dialog", 1, 16));
//    ResetButton.setNextFocusableComponent(BookField);
    ResetButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookPanelResetButton_ToolTipText"));
    ResetButton.setText(Configuration.getProperty("UI/CheckInPanel/BookPanelResetButton_Text"));
    ResetButton.addActionListener(new CheckInPanel_ResetButton_actionAdapter(this));
    ResetButton.setVisible(false);
    NavigationPanel.setLayout(NavigationBorderLayout);
    BookFieldLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    BookFieldLabel.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookFieldLabel_ToolTipText"));
    BookFieldLabel.setLabelFor(BookField);
    BookFieldLabel.setText(Configuration.getProperty("UI/CheckInPanel/BookFieldLabel_Text"));
    InformationPanel.setLayout(InformationBorderLayout);
    BookField.setFont(new java.awt.Font("Dialog", 1, 16));
    BookField.setMinimumSize(new Dimension(200, 27));
//    BookField.setNextFocusableComponent(CheckinButton);
    BookField.setPreferredSize(new Dimension(200, 27));
    BookField.setRequestFocusEnabled(true);
    BookField.setToolTipText(Configuration.getProperty("UI/CheckInPanel/BookField_ToolTipText"));
    BookField.setText("");
    BookField.setHorizontalAlignment(SwingConstants.LEADING);
    BookField.addKeyListener(new CheckInPanel_BookField_keyAdapter(this));
    DataPanel.setLayout(DataFlowLayout);
    ResponsePanel.setLayout(ResponseBorderLayout);
    NavigationPanel.setBorder(border1);
    DataPanel.setBorder(border2);
    BooksIcon.setText("");
    BooksIcon.setIcon(Configuration.LoadImage("UI/CheckInPanel/BooksIcon_Icon"));
    ResponsePanel.setBorder(border3);
    PatronText.setFont(new java.awt.Font("Dialog", 1, 18));
    PatronText.setOpaque(false);
    PatronText.setRequestFocusEnabled(false);
    PatronText.setToolTipText(Configuration.getProperty("UI/CheckInPanel/PatronText_ToolTipText"));
    PatronText.setEditable(false);
    PatronText.setText(Configuration.getProperty("UI/CheckInPanel/PatronText_DefaultText"));
    PatronText.setLineWrap(true);
    PatronText.setRows(2);
//    CheckoutText.setLineWrap(true);
    ResponseTextPanel.setLayout(ResponseTextBorderLayout);
    CheckinButton.setFont(new java.awt.Font("Dialog", 1, 16));
//    CheckinButton.setNextFocusableComponent(NextButton);
    CheckinButton.setText(Configuration.getProperty("UI/CheckInPanel/CheckinButton_Text"));
    CheckinButton.setToolTipText(Configuration.getProperty("UI/CheckInPanel/CheckinButton_ToolTipText"));
    CheckinButton.addActionListener(new CheckInPanel_CheckinButton_actionAdapter(this));
    CheckInScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    CheckInScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    CheckInScrollPane.setAutoscrolls(true);
    CheckInScrollPane.setBorder(null);
    CheckinText.setFont(new java.awt.Font("Dialog", 1, 16));
    CheckinText.setOpaque(false);
    CheckinText.setRequestFocusEnabled(false);
    CheckinText.setEditable(false);
    HTMLEditorKit kit = new HTMLEditorKit();
    kit.getStyleSheet().addRule("body {font-family: Dialog; font-size: 16pt;}");
    CheckinText.setEditorKit(kit);
    CheckinText.setContentType("text/html");
//    CheckoutText.setLineWrap(true);
//    CheckoutText.setWrapStyleWord(true);
    StatusText.setFont(new java.awt.Font("Dialog", 1, 18));
    StatusText.setForeground(Color.red);
    StatusText.setMinimumSize(new Dimension(33, 15));
    StatusText.setOpaque(false);
    StatusText.setPreferredSize(new Dimension(0, 24));
    StatusText.setToolTipText(Configuration.getProperty("UI/CheckInPanel/StatusText_ToolTipText"));
    StatusText.setText(Configuration.getProperty("UI/CheckInPanel/StatusText_DefaultText"));
    this.add(NavigationPanel,  BorderLayout.SOUTH);
    NavigationPanel.add(ResetButton, BorderLayout.WEST);
    NavigationPanel.add(NextButton,  BorderLayout.EAST);
    this.add(InformationPanel,  BorderLayout.CENTER);
    InformationPanel.add(DataPanel,  BorderLayout.SOUTH);
    DataPanel.add(BookFieldLabel, null);
    DataPanel.add(BookField, null);
    DataPanel.add(CheckinButton, null);
    InformationPanel.add(ResponsePanel,  BorderLayout.CENTER);
    ResponsePanel.add(BooksIcon,  BorderLayout.EAST);
    ResponsePanel.add(ResponseTextPanel,  BorderLayout.CENTER);
    ResponseTextPanel.add(PatronText, BorderLayout.NORTH);
    ResponseTextPanel.add(CheckInScrollPane,  BorderLayout.CENTER);
    ResponseTextPanel.add(StatusText,  BorderLayout.SOUTH);
    CheckInScrollPane.getViewport().add(CheckinText, null);
    CheckInPanelFocusTraversalPolicy policy = new CheckInPanelFocusTraversalPolicy();
    this.setFocusTraversalPolicy(policy);
    this.setFocusCycleRoot(true);
    this.setFocusTraversalKeysEnabled(true);
    this.grabFocus();
  }

  private static String strim(String string) {
    String intermediate = string.trim();
    if (Configuration.getBoolProperty("UI/Control/StripItemChecksumDigit")) {
      if (intermediate.length() > 0) {
        intermediate = intermediate.substring(0, intermediate.length()-1);
      }
    }
    return intermediate;
  }

  void NextButton_actionPerformed(ActionEvent e) {
    handler.securityDevice.stop();
    this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    handler.securityDevice.stop();
    this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
  }

  private void appendCheckinText(String entry) {
    String msg = this.CheckinText.getText();
    if (msg == null) {
      msg = "";
    }
    msg = this.stripHTML(msg) + entry + "<br>";
    this.CheckinText.setText(msg.replaceAll("\r\n", "<br>"));
  }

  void CheckinButton_actionPerformed(ActionEvent e) {
    CheckIn request = new CheckIn();
    CheckInResponse response = null;
    String finalStatusText = "";

    if (Configuration.getBoolProperty("Modes/EnableBarcodeAlii")) {
      if (this.BookField.getText().equals("$ESCAPE%")) {
        this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
      }
    }

    try {
      ResetTimer.stop();
      request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
      request.setItemIdentifier(strim(this.BookField.getText()));
      request.setTransactionDate(new Date());
      request.setNoBlock(new Boolean(false));
      if (request.getItemIdentifier().length() < 1 ||
          request.getItemIdentifier().equals(lastCheckedInId)) {
        this.BookField.setText("");
        this.BookField.requestFocus();
        ResetTimer.start();
        return;
      }
      if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
        throw new InvalidItemBarcode();
      }
      this.StatusText.setText(Configuration.getMessage("CheckinPendingMessage",  /* check this */
          new String[] {request.getItemIdentifier()}));
      this.ResponseTextPanel.paint(this.ResponseTextPanel.getGraphics());
      this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
      try {
        response = (CheckInResponse) handler.send(request);
      } catch (java.lang.ClassCastException ex) {
        response = null;
      }
      if (response == null) {
        throw new CheckinConnectionFailed();
      }

      if (! ( (response.getOk() != null) ? response.getOk().booleanValue() : false)) {
        throw new CheckinFailed();
      } else {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKINSUCCESS, "", "", request, response);
      }

      try {
        handler.securityDevice.lock();
      }
      catch (TimeoutException ex) {
          throw new LockFailed();
      }
      catch (FailureException ex) {
          throw new LockFailed();
      }
      this.PlaySound("CheckInSuccess");
      this.appendCheckinText(Configuration.getMessage(
          "CheckInSuccess",
          new String[] { (response.getTitleIdentifier().length() !=
                          0) ?
          response.getTitleIdentifier() :
          response.getItemIdentifier(), (response.getScreenMessage() != null) ?
            response.getScreenMessage() : ""}));
        lastCheckedInId = new String(request.getItemIdentifier());
   } catch (InvalidItemBarcode ex) {
     handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Invalid Barcode Entered", request, response);
     this.PlaySound("InvalidItemBarcode");
     finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {});
    } catch (CheckinConnectionFailed ex) {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "", "Network Connection Failure", request, response);
        this.PlaySound("CheckinNetworkError");
        this.appendCheckinText(Configuration.getMessage(
            "CheckinNetworkError", new String[] {
            request.getItemIdentifier()}));
    } catch (CheckinFailed ex) {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "", "Server refused checkout", request, response);
        this.PlaySound("CheckinFailedError");
        this.appendCheckinText(Configuration.getMessage(
            "CheckinFailedError",
            new String[] { (response.getTitleIdentifier().length() !=
                            0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier(), (response.getScreenMessage() != null) ?
            response.getScreenMessage() : ""}));
    } catch (LockFailed ex) {
        handler.securityDevice.reset();
        handler.log.recordEvent(OnlineLogEvent.STATUS_LOCKFAILURE,"", "", request, response);
        this.PlaySound("LockFailedError");
        this.appendCheckinText(Configuration.getMessage("LockFailedError",
            new String[] { (response.getTitleIdentifier().length() != 0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier()}));
    } catch (Exception ex) {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKINFAILURE, "", "Unexpected checkin error!", request, response);
        this.PlaySound("UnexpectedCheckinError");
        this.appendCheckinText(Configuration.getMessage(
            "UnexpectedCheckinError", new String[] {
            request.getItemIdentifier()
        }));
      log.fatal("Checkin failure: " + ex.getMessage() + " - " +
                ex.getStackTrace());
    }
    lastEnteredId = new String(request.getItemIdentifier());
    this.StatusText.setText(finalStatusText);
    this.BookField.setText("");
    this.BookField.requestFocus();
    ResetTimer.start();
  }

  void BookField_keyTyped(KeyEvent e) {
    ResetTimer.restart();
    if (e.getKeyChar() == '\u001B') {
      this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '^') {
      e.consume();
      this.CheckinButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
  }

  public void grabFocus() {
    super.grabFocus();
    BookField.grabFocus();
  }

  public void requestFocus() {
    super.requestFocus();
    BookField.requestFocus();
  }

  protected void finalize() throws java.lang.Throwable {
    handler.securityDevice.stop();
    super.finalize();
  }

  public void autoInputId(String serial) {
    this.BookField.setText(serial);
    this.CheckinButton_actionPerformed(new ActionEvent(this, 0, ""));
  }
}


class CheckInPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckInPanel adaptee;

  CheckInPanel_NextButton_actionAdapter(CheckInPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class CheckInPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckInPanel adaptee;

  CheckInPanel_ResetButton_actionAdapter(CheckInPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class CheckInPanel_CheckinButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckInPanel adaptee;

  CheckInPanel_CheckinButton_actionAdapter(CheckInPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.CheckinButton_actionPerformed(e);
  }
}

class CheckInPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
  private CheckInPanel adaptee;

  CheckInPanel_BookField_keyAdapter(CheckInPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.BookField_keyTyped(e);
  }
}
