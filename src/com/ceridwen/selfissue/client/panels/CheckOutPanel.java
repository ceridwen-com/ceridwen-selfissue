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
import com.ceridwen.circulation.SIP.messages.CheckOut;
import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.CirculationHandler;
import com.ceridwen.selfissue.client.log.OnlineLogEvent;



public class CheckOutPanel extends SelfIssuePanel implements SecurityListener {
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
           if (aComponent.equals(BookField)) {
               return CheckoutButton;
           } else if (aComponent.equals(CheckoutButton)) {
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
             return CheckoutButton;
         } else if (aComponent.equals(CheckoutButton)) {
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

   static class CheckinConnectionFailed extends Exception
   {
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
	

	public CheckinConnectionFailed(String message)
     {
       super(message);
     }
   }
   static class CheckoutConnectionFailed extends Exception
   {

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
   static class CheckoutFailed extends Exception
   {

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
   static class UnlockFailed extends Exception
   {

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
   static class InvalidItemBarcode extends Exception
   {

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
   static class RepeatedOrTooShortItemId extends Exception
   {

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
   private JButton CheckoutButton = new JButton();
   private FlowLayout DataFlowLayout = new FlowLayout();
   private String PatronID;
   private String PatronName;
   private javax.swing.Timer ResetTimer;
   private String lastEnteredId = "";
   private String lastCheckedOutId = "";
   private JScrollPane CheckOutScrollPane = new JScrollPane();
//  JTextArea CheckoutText = new JTextArea();
   private JEditorPane CheckoutText = new JEditorPane();
   private JLabel StatusText = new JLabel();

   private CirculationHandler handler;


  public CheckOutPanel() {
  }

  public CheckOutPanel(CirculationHandler handler, String PatronID, String PatronName, String message, javax.swing.Timer ResetTimer) {
    try {
      this.handler = handler;
      this.PatronID = PatronID;
      this.PatronName = PatronName;
      this.ResetTimer = ResetTimer;
      jbInit();
      PatronText.setText(Configuration.getMessage("GreetPatron", new String[]{PatronName, ((message == null)?"":message)}));
      ResetTimer.restart();
      enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
      startSecurity();
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
    NextButton.setToolTipText(Configuration.getProperty("UI/BookPanel/BookPanelNextButton_ToolTipText"));
    NextButton.setText(Configuration.getProperty("UI/BookPanel/BookPanelNextButton_Text"));
    NextButton.addActionListener(new BookPanel_NextButton_actionAdapter(this));
    ResetButton.setFont(new java.awt.Font("Dialog", 1, 16));
//    ResetButton.setNextFocusableComponent(BookField);
    ResetButton.setToolTipText(Configuration.getProperty("UI/BookPanel/BookPanelResetButton_ToolTipText"));
    ResetButton.setText(Configuration.getProperty("UI/BookPanel/BookPanelResetButton_Text"));
    ResetButton.addActionListener(new BookPanel_ResetButton_actionAdapter(this));
    ResetButton.setVisible(false);
    NavigationPanel.setLayout(NavigationBorderLayout);
    BookFieldLabel.setFont(new java.awt.Font("Dialog", 1, 16));
    BookFieldLabel.setToolTipText(Configuration.getProperty("UI/BookPanel/BookFieldLabel_ToolTipText"));
    BookFieldLabel.setLabelFor(BookField);
    BookFieldLabel.setText(Configuration.getProperty("UI/BookPanel/BookFieldLabel_Text"));
    InformationPanel.setLayout(InformationBorderLayout);
    BookField.setFont(new java.awt.Font("Dialog", 1, 16));
    BookField.setMinimumSize(new Dimension(200, 27));
//    BookField.setNextFocusableComponent(CheckoutButton);
    BookField.setPreferredSize(new Dimension(200, 27));
    BookField.setRequestFocusEnabled(true);
    BookField.setToolTipText(Configuration.getProperty("UI/BookPanel/BookField_ToolTipText"));
    BookField.setText("");
    BookField.setHorizontalAlignment(SwingConstants.LEADING);
    BookField.addKeyListener(new BookPanel_BookField_keyAdapter(this));
    DataPanel.setLayout(DataFlowLayout);
    ResponsePanel.setLayout(ResponseBorderLayout);
    NavigationPanel.setBorder(border1);
    DataPanel.setBorder(border2);
    BooksIcon.setText("");
    BooksIcon.setIcon(Configuration.LoadImage("UI/BookPanel/BooksIcon_Icon"));
    ResponsePanel.setBorder(border3);
    PatronText.setFont(new java.awt.Font("Dialog", 1, 18));
    PatronText.setOpaque(false);
    PatronText.setRequestFocusEnabled(false);
    PatronText.setToolTipText(Configuration.getProperty("UI/BookPanel/PatronText_ToolTipText"));
    PatronText.setEditable(false);
    PatronText.setText(Configuration.getProperty("UI/BookPanel/PatronText_DefaultText"));
    PatronText.setLineWrap(true);
    PatronText.setRows(2);
//    CheckoutText.setLineWrap(true);
    ResponseTextPanel.setLayout(ResponseTextBorderLayout);
    CheckoutButton.setFont(new java.awt.Font("Dialog", 1, 16));
//    CheckoutButton.setNextFocusableComponent(NextButton);
    CheckoutButton.setText(Configuration.getProperty("UI/BookPanel/CheckoutButton_Text"));
    CheckoutButton.setToolTipText(Configuration.getProperty("UI/BookPanel/CheckoutButton_ToolTipText"));
    CheckoutButton.addActionListener(new BookPanel_CheckoutButton_actionAdapter(this));
    CheckOutScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    CheckOutScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    CheckOutScrollPane.setAutoscrolls(true);
    CheckOutScrollPane.setBorder(null);
    CheckoutText.setFont(new java.awt.Font("Dialog", 1, 16));
    CheckoutText.setOpaque(false);
    CheckoutText.setRequestFocusEnabled(false);
    CheckoutText.setEditable(false);
    HTMLEditorKit kit = new HTMLEditorKit();
    kit.getStyleSheet().addRule("body {font-family: Dialog; font-size: 16pt;}");
    CheckoutText.setEditorKit(kit);
    CheckoutText.setContentType("text/html");
//    CheckoutText.setLineWrap(true);
//    CheckoutText.setWrapStyleWord(true);
    StatusText.setFont(new java.awt.Font("Dialog", 1, 18));
    StatusText.setForeground(Color.red);
    StatusText.setMinimumSize(new Dimension(33, 15));
    StatusText.setOpaque(false);
    StatusText.setPreferredSize(new Dimension(0, 24));
    StatusText.setToolTipText(Configuration.getProperty("UI/BookPanel/StatusText_ToolTipText"));
    StatusText.setText(Configuration.getProperty("UI/BookPanel/StatusText_DefaultText"));
    this.add(NavigationPanel,  BorderLayout.SOUTH);
    NavigationPanel.add(ResetButton, BorderLayout.WEST);
    NavigationPanel.add(NextButton,  BorderLayout.EAST);
    this.add(InformationPanel,  BorderLayout.CENTER);
    InformationPanel.add(DataPanel,  BorderLayout.SOUTH);
    DataPanel.add(BookFieldLabel, null);
    DataPanel.add(BookField, null);
    DataPanel.add(CheckoutButton, null);
    InformationPanel.add(ResponsePanel,  BorderLayout.CENTER);
    ResponsePanel.add(BooksIcon,  BorderLayout.EAST);
    ResponsePanel.add(ResponseTextPanel,  BorderLayout.CENTER);
    ResponseTextPanel.add(PatronText, BorderLayout.NORTH);
    ResponseTextPanel.add(CheckOutScrollPane,  BorderLayout.CENTER);
    ResponseTextPanel.add(StatusText,  BorderLayout.SOUTH);
    CheckOutScrollPane.getViewport().add(CheckoutText, null);
    CheckOutPanelFocusTraversalPolicy policy = new CheckOutPanelFocusTraversalPolicy();
    this.setFocusTraversalPolicy(policy);
    this.setFocusCycleRoot(true);
    this.setFocusTraversalKeysEnabled(true);
    this.grabFocus();
  }

  void NextButton_actionPerformed(ActionEvent e) {
    this.stopSecurity();
    handler.printReceipt("Check-out Receipt for " + this.PatronName + " (" +
                         this.PatronID + ")\r\n\r\n" +
                         this.CheckoutText.getText()
                         + "\r\n\r\n" + new Date());
    this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    this.stopSecurity();
    this.firePanelChange(new SelfIssuePanelEvent(this, PatronPanel.class));
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
     this.PlaySound("CheckOutSuccess");
     this.appendCheckoutText(Configuration.getMessage("CheckOutSuccess",
         new String[] {
         ( (response.getTitleIdentifier().
            length() != 0) ?
          escapeHTML(response.getTitleIdentifier()) :
          escapeHTML(response.getItemIdentifier()))}) +
                             " " +
                             ( (response.getDueDate().length() > 0) ?
                              Configuration.
                              getMessage("DueDateMessage",
                                         new String[] {
                                         demangleDate(response.getDueDate())}) :
                              Configuration.getMessage("NoDueDateMessage",
         new String[] {}))
                             );
    lastCheckedOutId = new String(request.getItemIdentifier());
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
      ResetTimer.stop();
      this.pauseSecurity();
      this.BookField.setEditable(true);
      this.BookField.setEnabled(true);
      try {
        this.DataPanel.paint(this.DataPanel.getGraphics());
      } catch (Exception ex) {
        log.warn("Error during redraw", ex);
      }

      request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
      request.setPatronIdentifier(this.PatronID);
      request.setItemIdentifier(strim(this.BookField.getText()));
      request.setRenewalPolicy(new Boolean(allowRenews || trustMode));
      request.setTransactionDate(new Date());
      if (trustMode) {
        request.setNoBlock(new Boolean(useNoBlock));
      }
      if (request.getItemIdentifier().length() < 1 ||
          request.getItemIdentifier().equals(lastCheckedOutId)) {
        throw new RepeatedOrTooShortItemId();
      }
      if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
        throw new InvalidItemBarcode();
      }
      this.StatusText.setText(Configuration.getMessage("CheckoutPendingMessage",
          new String[] {request.getItemIdentifier()}));
      try {
        this.ResponseTextPanel.paint(this.ResponseTextPanel.getGraphics());
      } catch (Exception ex) {
        log.warn("Error during redraw", ex);
      }
      try {
        this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
      } catch (Exception ex) {
        log.warn("Error during redraw", ex);
      }
      try {
        response = (CheckOutResponse) handler.send(request);
      } catch (java.lang.ClassCastException ex) {
        response = null;
      }
      if (response == null) {
        if ((trustMode || allowOffline) &&
            (!retryItemWhenError ||
             request.getItemIdentifier().equals(lastEnteredId))) {
          // Network failing so cache request and fake positive response
          request.setTransactionDate(new Date());
          request.setNoBlock(new Boolean(useNoBlock));
          handler.spool(request);
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

      if (! ( (response.getOk() != null) ? response.getOk().booleanValue() : false)) {
        if (trustMode &&
            (!retryItemWhenError ||
             request.getItemIdentifier().equals(lastEnteredId))) {
          // System checkout failed so report to tracking log and proceed as per success
          handler.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", "", request, response);
        } else {
          throw new CheckoutFailed();
        }
      } else {
        handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTSUCCESS, "", "", request, response);
      }

      try {
        handler.unlockItem();
      }
      catch (TimeoutException ex) {
          throw new UnlockFailed();
      }
      catch (FailureException ex) {
          throw new UnlockFailed();
      }

      reportSuccess(request, response);
   } catch (RepeatedOrTooShortItemId ex) {
     // don't need to do anything for this - just let if fall through
   } catch (InvalidItemBarcode ex) {
     handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Invalid Barcode Entered", request, response);
     this.PlaySound("InvalidItemBarcode");
     finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {});
    } catch (CheckoutConnectionFailed ex) {
      if (trustMode) {
        this.PlaySound("CheckoutRetry");
        finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
      } else {
        handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Network Connection Failure", request, response);
        this.PlaySound("CheckoutNetworkError");
        this.appendCheckoutText(Configuration.getMessage(
            "CheckoutNetworkError", new String[] {
            request.getItemIdentifier()}));
      }
    } catch (CheckoutFailed ex) {
      if (trustMode) {
        this.PlaySound("CheckoutRetry");
        finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
      } else {
        handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Server refused checkout", request, response);
        this.PlaySound("CheckoutFailedError");
        this.appendCheckoutText(Configuration.getMessage(
            "CheckoutFailedError",
            new String[] { (response.getTitleIdentifier().length() !=
                            0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier(), (response.getScreenMessage() != null) ?
            response.getScreenMessage() : ""}));
      }
    } catch (UnlockFailed ex) {
      CheckInResponse checkinr = null;
      try {
        handler.resetSecurityDevice();
        CheckIn checkin = new CheckIn();
        checkin.setItemIdentifier(request.getItemIdentifier());
        checkin.setCancel(new Boolean(true));
        checkinr = (CheckInResponse) handler.send(checkin);
        if (checkinr == null) {
          throw new CheckinConnectionFailed("Null response");
        }
        if (! ( (checkinr.getOk() != null) ? checkinr.getOk().booleanValue() : false)) {
          throw new CheckinConnectionFailed( (checkinr.getScreenMessage() != null) ?
                                            checkinr.getScreenMessage() :
                                            "No message");
        }
        handler.recordEvent(OnlineLogEvent.STATUS_UNLOCKFAILURE,"", "", request, response);
        this.PlaySound("UnlockFailedError");
        this.appendCheckoutText(Configuration.getMessage("UnlockFailedError",
            new String[] { (response.getTitleIdentifier().length() != 0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier()}));
      } catch (Exception ex1) {
        handler.recordEvent(OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE,"", "", request, checkinr);
        if (suppressSecurityFailureMessages) {
          reportSuccess(request, response);
        } else {
          this.PlaySound("UnlockFailedCheckInFailedError");
          this.appendCheckoutText(Configuration.getMessage(
              "UnlockFailedCheckInFailedError",
              new String[] { (response.getTitleIdentifier().length() != 0) ?
              response.getTitleIdentifier() :
              response.getItemIdentifier()}));
        }
        log.fatal("Check back in failure: " + ex1.getMessage() + " - " +
                  ex1.getStackTrace(), ex1);
      }
    } catch (Exception ex) {
      if (trustMode) {
        this.PlaySound("CheckoutRetry");
        finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
      } else {
        handler.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Unexpected checkout error!", request, response);
        this.PlaySound("UnexpectedCheckoutError");
        this.appendCheckoutText(Configuration.getMessage(
            "UnexpectedCheckoutError", new String[] {
            request.getItemIdentifier()
        }));
      }
      log.fatal("Unexpected checkout failure: " + ex.getMessage() + " - " +
                ex.getStackTrace(), ex);
    }
    lastEnteredId = new String(request.getItemIdentifier());
    this.StatusText.setText(finalStatusText);
    this.BookField.setText("");
    this.BookField.requestFocus();

    try {
      this.DataPanel.paint(this.DataPanel.getGraphics());
    } catch (Exception ex) {
      log.warn("Error during redraw", ex);
    }
    try {
      this.ResponseTextPanel.paint(this.ResponseTextPanel.getGraphics());
    } catch (Exception ex) {
      log.warn("Error during redraw", ex);
    }
    try {
      this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
    } catch (Exception ex) {
      log.warn("Error during redraw", ex);
    }

    this.resumeSecurity();
    this.resetSecurity();
    this.BookField.setEditable(true);
    this.BookField.setEnabled(true);
    ResetTimer.start();

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

  void BookField_keyTyped(KeyEvent e) {
    ResetTimer.restart();
    if (e.getKeyChar() == '\u001B') {
      this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
    }
    if (e.getKeyChar() == '\n' || e.getKeyChar() == '^') {
      e.consume();
      this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
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

  private void startSecurity() {
    handler.initSecurityDevice();
    handler.startSecurityDevice(this);
  }

  private void resetSecurity() {
    handler.resetSecurityDevice();
  }
  private void pauseSecurity() {
    handler.pauseSecurityDevice();
  }

  private void resumeSecurity() {
    handler.resumeSecurityDevice();
  }

  private void stopSecurity() {
    handler.stopSecurityDevice();
    handler.deinitSecurityDevice();
  }

  protected void finalize() throws java.lang.Throwable {
    stopSecurity();
    super.finalize();
  }

  public void autoInputId(String serial) {
    this.BookField.setText(serial);
    this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
  }
}


class BookPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckOutPanel adaptee;

  BookPanel_NextButton_actionAdapter(CheckOutPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class BookPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckOutPanel adaptee;

  BookPanel_ResetButton_actionAdapter(CheckOutPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class BookPanel_CheckoutButton_actionAdapter implements java.awt.event.ActionListener {
  private CheckOutPanel adaptee;

  BookPanel_CheckoutButton_actionAdapter(CheckOutPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.CheckoutButton_actionPerformed(e);
  }
}

class BookPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
  private CheckOutPanel adaptee;

  BookPanel_BookField_keyAdapter(CheckOutPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.BookField_keyTyped(e);
  }
}
