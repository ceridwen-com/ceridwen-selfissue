/**
 * <p>Title: Self Issue</p>
 * <p>Description: Self Issue Client</p>
 * <p>Copyright: 2004,</p>
 * <p>Company: ceridwen.com</p>
 * @author Matthew J. Dovey
 * @version 2.1
 */

package com.ceridwen.selfissue.client;

import java.io.StringReader;
import java.io.BufferedReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.event.*;
import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.circulation.security.*;
import org.apache.commons.logging.*;
import com.ceridwen.selfissue.client.log.*;

class CheckoutConnectionFailed extends Exception {
}
class CheckoutFailed extends Exception {
}
class UnlockFailed extends Exception {
}
class InvalidItemBarcode extends Exception {
}

public class BookPanel extends SelfIssuePanel implements SecurityListener {
  private static Log log = LogFactory.getLog(BookPanel.class);

  JPanel NavigationPanel = new JPanel();
  BorderLayout BookBorderLayout = new BorderLayout();
  JButton NextButton = new JButton();
  JButton ResetButton = new JButton();
  BorderLayout NavigationBorderLayout = new BorderLayout();
  JPanel InformationPanel = new JPanel();
  JLabel BookFieldLabel = new JLabel();
  JPanel DataPanel = new JPanel();
  BorderLayout InformationBorderLayout = new BorderLayout();
  JTextField BookField = new JTextField();
  JPanel ResponsePanel = new JPanel();
  BorderLayout ResponseBorderLayout = new BorderLayout();
  Border border1;
  Border border2;
  JLabel BooksIcon = new JLabel();
  Border border3;
  JPanel ResponseTextPanel = new JPanel();
  JTextArea PatronText = new JTextArea();
  BorderLayout ResponseTextBorderLayout = new BorderLayout();
  JButton CheckoutButton = new JButton();
  FlowLayout DataFlowLayout = new FlowLayout();
  String PatronID;
  String PatronName;
  javax.swing.Timer ResetTimer;
  String lastEnteredId = "";
  String lastCheckedOutId = "";
  JScrollPane CheckOutScrollPane = new JScrollPane();
//  JTextArea CheckoutText = new JTextArea();
  JEditorPane CheckoutText = new JEditorPane();
  JLabel StatusText = new JLabel();

  CirculationHandler handler;

  public BookPanel() {
  }

  public BookPanel(CirculationHandler handler, String PatronID, String PatronName, String message, javax.swing.Timer ResetTimer) {
    try {
      this.handler = handler;
      this.PatronID = PatronID;
      this.PatronName = PatronName;
      this.ResetTimer = ResetTimer;
      jbInit();
      PatronText.setText(Configuration.getMessage("GreetPatron", new String[]{PatronName, ((message == null)?"":message)}));
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
    NextButton.setNextFocusableComponent(BookField);
    NextButton.setToolTipText(Configuration.getProperty("UI/BookPanel/BookPanelNextButton_ToolTipText"));
    NextButton.setText(Configuration.getProperty("UI/BookPanel/BookPanelNextButton_Text"));
    NextButton.addActionListener(new BookPanel_NextButton_actionAdapter(this));
    ResetButton.setFont(new java.awt.Font("Dialog", 1, 16));
    ResetButton.setNextFocusableComponent(BookField);
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
    BookField.setNextFocusableComponent(CheckoutButton);
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
    CheckoutButton.setNextFocusableComponent(NextButton);
    CheckoutButton.setText(Configuration.getProperty("UI/BookPanel/CheckoutButton_Text"));
    CheckoutButton.setToolTipText(Configuration.getProperty("UI/BookPanel/CheckoutButton_ToolTipText"));
    CheckoutButton.addActionListener(new BookPanel_CheckoutButton_actionAdapter(this));
    CheckOutScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    CheckOutScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
    this.grabFocus();
  }

  void NextButton_actionPerformed(ActionEvent e) {
    handler.securityDevice.stop();
    handler.printReceipt("Check-out Receipt for " + this.PatronName + " (" +
                         this.PatronID + ")\r\n\r\n" +
                         this.CheckoutText.getText()
                         + "\r\n\r\n" + new Date());
    this.firePanelChange(new SelfIssuePanelEvent(this));
  }

  void ResetButton_actionPerformed(ActionEvent e) {
    handler.securityDevice.stop();
    this.firePanelChange(new SelfIssuePanelEvent(this));
  }

  String stripHTML(String in) {
    String process;
    process = in.replaceAll("<html>", "");
    process = process.replaceAll("<head>", "");
    process = process.replaceAll("<body>", "");
    process = process.replaceAll("<p>", "");
    process = process.replaceAll("<b>", "");
    process = process.replaceAll("</b>", "");
    process = process.replaceAll("</p>", "");
    process = process.replaceAll("</body>", "");
    process = process.replaceAll("</head>", "");
    process = process.replaceAll("</html>", "");
    process = process.replaceAll("  ", " ");
    return process.trim();
  }

  public static final String escapeHTML(String s){
     StringBuffer sb = new StringBuffer();
     int n = s.length();
     for (int i = 0; i < n; i++) {
        char c = s.charAt(i);
        switch (c) {
           case '<': sb.append("&lt;"); break;
           case '>': sb.append("&gt;"); break;
           case '&': sb.append("&amp;"); break;
           case '"': sb.append("&quot;"); break;
           case 'à': sb.append("&agrave;");break;
           case 'À': sb.append("&Agrave;");break;
           case 'â': sb.append("&acirc;");break;
           case 'Â': sb.append("&Acirc;");break;
           case 'ä': sb.append("&auml;");break;
           case 'Ä': sb.append("&Auml;");break;
           case 'å': sb.append("&aring;");break;
           case 'Å': sb.append("&Aring;");break;
           case 'æ': sb.append("&aelig;");break;
           case 'Æ': sb.append("&AElig;");break;
           case 'ç': sb.append("&ccedil;");break;
           case 'Ç': sb.append("&Ccedil;");break;
           case 'é': sb.append("&eacute;");break;
           case 'É': sb.append("&Eacute;");break;
           case 'è': sb.append("&egrave;");break;
           case 'È': sb.append("&Egrave;");break;
           case 'ê': sb.append("&ecirc;");break;
           case 'Ê': sb.append("&Ecirc;");break;
           case 'ë': sb.append("&euml;");break;
           case 'Ë': sb.append("&Euml;");break;
           case 'ï': sb.append("&iuml;");break;
           case 'Ï': sb.append("&Iuml;");break;
           case 'ô': sb.append("&ocirc;");break;
           case 'Ô': sb.append("&Ocirc;");break;
           case 'ö': sb.append("&ouml;");break;
           case 'Ö': sb.append("&Ouml;");break;
           case 'ø': sb.append("&oslash;");break;
           case 'Ø': sb.append("&Oslash;");break;
           case 'ß': sb.append("&szlig;");break;
           case 'ù': sb.append("&ugrave;");break;
           case 'Ù': sb.append("&Ugrave;");break;
           case 'û': sb.append("&ucirc;");break;
           case 'Û': sb.append("&Ucirc;");break;
           case 'ü': sb.append("&uuml;");break;
           case 'Ü': sb.append("&Uuml;");break;
           case '®': sb.append("&reg;");break;
           case '©': sb.append("&copy;");break;
           case '€': sb.append("&euro;"); break;
           // be carefull with this one (non-breaking white space)
//           case ' ': sb.append("&nbsp;");break;

           default:  sb.append(c); break;
        }
     }
     return sb.toString();
  }


  void appendCheckoutText(String entry) {
    String msg = this.CheckoutText.getText();
    if (msg == null)
      msg = "";
    msg = this.stripHTML(msg) + entry + "<br>";
    this.CheckoutText.setText(msg.replaceAll("\r\n", "<br>"));
  }

  void CheckoutButton_actionPerformed(ActionEvent e) {
    CheckOut request = new CheckOut();
    CheckOutResponse response = null;
    String finalStatusText = "";
    try {
      ResetTimer.stop();
      request.setInstitutionId(Configuration.getProperty("Systems/SIP/InstitutionId"));
      request.setPatronIdentifier(this.PatronID);
      request.setItemIdentifier(this.BookField.getText().trim());
      request.setRenewalPolicy(new Boolean(allowRenews || trustMode));
      request.setTransactionDate(new Date());
      if (trustMode) {
        request.setNoBlock(new Boolean(useNoBlock));
      }
      if (request.getItemIdentifier().length() < 1 ||
          request.getItemIdentifier().equals(lastCheckedOutId)) {
        this.BookField.setText("");
        this.BookField.requestFocus();
        ResetTimer.start();
        return;
      }
      if (!this.validateBarcode(request.getItemIdentifier(), Configuration.getProperty("UI/Validation/ItemBarcodeMask"))) {
        throw new InvalidItemBarcode();
      }
      this.StatusText.setText(Configuration.getMessage("CheckoutPendingMessage",
          new String[] {request.getItemIdentifier()}));
      this.ResponseTextPanel.paint(this.ResponseTextPanel.getGraphics());
      this.ResponsePanel.paint(this.ResponsePanel.getGraphics());
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
          handler.log.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", "", request, response);
        } else {
          throw new CheckoutFailed();
        }
      } else {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTSUCCESS, "", "", request, response);
      }

      try {
        handler.securityDevice.unlock();
      }
      catch (TimeoutException ex) {
          throw new UnlockFailed();
      }
      catch (FailureException ex) {
          throw new UnlockFailed();
      }

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
   } catch (InvalidItemBarcode ex) {
     handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Invalid Barcode Entered", request, response);
     this.PlaySound("InvalidItemBarcode");
     finalStatusText = Configuration.getMessage("InvalidItemBarcode",
                                                new String[] {});
    } catch (CheckoutConnectionFailed ex) {
      if (trustMode) {
        this.PlaySound("CheckoutRetry");
        finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
      } else {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Network Connection Failure", request, response);
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
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Server refused checkout", request, response);
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
        handler.log.recordEvent(OnlineLogEvent.STATUS_UNLOCKFAILURE,"", "", request, response);
        CheckIn checkin = new CheckIn();
        checkin.setItemIdentifier(request.getItemIdentifier());
        checkin.setCancel(new Boolean(true));
        checkinr = (CheckInResponse) handler.send(checkin);
        if (checkinr == null)
          throw new CheckoutConnectionFailed();
        if (! ( (checkinr.getOk() != null) ? checkinr.getOk().booleanValue() : false))
          throw new CheckoutConnectionFailed();
        this.PlaySound("UnlockFailedError");
        this.appendCheckoutText(Configuration.getMessage("UnlockFailedError",
            new String[] { (response.getTitleIdentifier().length() != 0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier()}));
      } catch (Exception ex1) {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE,"", "", request, checkinr);
        this.PlaySound("UnlockFailedCheckInFailedError");
        this.appendCheckoutText(Configuration.getMessage(
            "UnlockFailedCheckInFailedError",
            new String[] { (response.getTitleIdentifier().length() != 0) ?
            response.getTitleIdentifier() :
            response.getItemIdentifier()}));
        log.fatal("Check back in failure: " + ex.getMessage() + " - " +
                  ex.getStackTrace());
      }
    } catch (Exception ex) {
      if (trustMode) {
        this.PlaySound("CheckoutRetry");
        finalStatusText = Configuration.getMessage("CheckoutRetry", new String[] {});
      } else {
        handler.log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTFAILURE, "", "Unexpected checkout error!", request, response);
        this.PlaySound("UnexpectedCheckoutError");
        this.appendCheckoutText(Configuration.getMessage(
            "UnexpectedCheckoutError", new String[] {
            request.getItemIdentifier()
        }));
      }
      log.fatal("Checkout failure: " + ex.getMessage() + " - " +
                ex.getStackTrace());
    }
    lastEnteredId = new String(request.getItemIdentifier());
    this.StatusText.setText(finalStatusText);
    this.BookField.setText("");
    this.BookField.requestFocus();
    ResetTimer.start();
  }

  String demangleDate(String date) {
    if (date == null)
      return "";

    return date;
  }

  void BookField_keyTyped(KeyEvent e) {
    ResetTimer.restart();
    if (e.getKeyChar() == '\u001B')
      this.NextButton_actionPerformed(new ActionEvent(this, 0, ""));
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

  protected void finalize() throws java.lang.Throwable {
    handler.securityDevice.stop();
    super.finalize();
  }

  public void autoInputId(String serial) {
    this.BookField.setText(serial);
    this.CheckoutButton_actionPerformed(new ActionEvent(this, 0, ""));
  }
}


class BookPanel_NextButton_actionAdapter implements java.awt.event.ActionListener {
  BookPanel adaptee;

  BookPanel_NextButton_actionAdapter(BookPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.NextButton_actionPerformed(e);
  }
}

class BookPanel_ResetButton_actionAdapter implements java.awt.event.ActionListener {
  BookPanel adaptee;

  BookPanel_ResetButton_actionAdapter(BookPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ResetButton_actionPerformed(e);
  }
}

class BookPanel_CheckoutButton_actionAdapter implements java.awt.event.ActionListener {
  BookPanel adaptee;

  BookPanel_CheckoutButton_actionAdapter(BookPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.CheckoutButton_actionPerformed(e);
  }
}

class BookPanel_BookField_keyAdapter extends java.awt.event.KeyAdapter {
  BookPanel adaptee;

  BookPanel_BookField_keyAdapter(BookPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void keyTyped(KeyEvent e) {
    adaptee.BookField_keyTyped(e);
  }
}
