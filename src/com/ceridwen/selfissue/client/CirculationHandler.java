package com.ceridwen.selfissue.client;

import java.util.Date;
import com.ceridwen.circulation.SIP.exceptions.*;
import javax.print.*;
import javax.print.attribute.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import org.apache.commons.logging.*;
import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.circulation.SIP.transport.*;
import com.ceridwen.circulation.security.*;
import com.ceridwen.selfissue.client.log.*;
import com.ceridwen.selfissue.client.spooler.*;
import org.w3c.dom.*;
import java.awt.print.*;
import java.util.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */


class PrintItem implements Printable {
    private final AttributedString[] attributedItems;
    public final static String EMPTY_PARAG = "    ";

    public PrintItem(Object[] items) {
        attributedItems = new AttributedString[items.length];

        for (int i=0; i < items.length; i++) {
            this.attributedItems[i] = new AttributedString((String) items[i]);
        }
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {

        if (pageIndex >= 1) return Printable.NO_SUCH_PAGE;

        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        graphics2d.setPaint(Color.black);
        Point2D.Float pen = new Point2D.Float();

        for (int i=0; i < attributedItems.length; i++) {
            printText(graphics2d, pageFormat, pen, attributedItems[i]);
        }

        return Printable.PAGE_EXISTS;
    }

    private void printText(Graphics2D graphics2d, PageFormat pageFormat, Point2D.Float pen, AttributedString text) {

        AttributedCharacterIterator charIterator = text.getIterator();
        LineBreakMeasurer measurer = new LineBreakMeasurer(charIterator, graphics2d.getFontRenderContext());
        float wrappingWidth = (float) pageFormat.getImageableWidth();

        while (measurer.getPosition() < charIterator.getEndIndex()) {
            TextLayout layout = measurer.nextLayout(wrappingWidth);
            pen.y += layout.getAscent();
            float dx = layout.isLeftToRight()? 0 : (wrappingWidth - layout.getAdvance());
            layout.draw(graphics2d, pen.x + dx, pen.y);
            pen.y += layout.getDescent() + layout.getLeading();
        }
    }
}




public class CirculationHandler implements com.ceridwen.util.SpoolerProcessor {

  private static Log logger = LogFactory.getLog(CirculationHandler.class);

  Connection conn;
  public OfflineSpooler spool;
  public OnlineLogManager log ;
  public SecurityDevice securityDevice;

  private void initiateOfflineSpooler() {
    java.io.File spoolDir = null;
    try {
      spoolDir = new java.io.File(Configuration.getProperty(
          "Systems/Spooler/Spool"));

    } catch (Exception ex) {
      logger.fatal("Can't create SpoolerDirectory", ex);
    }
    spool = new OfflineSpoolerDevice(spoolDir, this,
                                     Configuration.getIntProperty(
        "Systems/Spooler/ReplayPeriod") *
                                     60000);
  }

  private void initiateSecurityDevice() {
    try {
      securityDevice = (SecurityDevice) Class.forName(Configuration.getProperty(
          "Systems/Security/@class")).newInstance();
    } catch (Exception ex) {
      logger.fatal("Could not initialise security device", ex);
      securityDevice = new com.ceridwen.selfissue.client.nulldevices.SecurityDevice();
    }
    securityDevice.setRetries(Configuration.getIntProperty(
        "Systems/Security/Retries"));
    securityDevice.setTimeOut(Configuration.getIntProperty(
        "Systems/Security/Timeout"));
    securityDevice.init();
    ShutdownThread.registerSecurityDeviceShutdown(securityDevice);
  }

  private void initiateOnlineLoggers() {
    NodeList loggers = Configuration.getPropertyList("Systems/Loggers/Logger");
    log = new OnlineLogManager();
    for (int i = 0; i < loggers.getLength(); i++) {
      java.io.File onlineLogDir = null;
      try {
        onlineLogDir = new java.io.File(
            Configuration.getSubProperty(loggers.item(i), "Spool"));
      }
      catch (Exception ex) {
        logger.fatal("Can't create OnlineLogDirectory", ex);
      }
      OnlineLogLogger onlineLogger;
      try {
        onlineLogger = (OnlineLogLogger) Class.forName(
            Configuration.getSubProperty(loggers.item(i), "@class")).newInstance();
      }
      catch (Exception ex) {
        logger.debug(ex);
        onlineLogger = new com.ceridwen.selfissue.client.nulldevices.OnlineLogLogger();
      }
      onlineLogger.initialise(loggers.item(i));
      OnlineLog alog = new com.ceridwen.selfissue.client.log.OnlineLogDevice(onlineLogDir,
          onlineLogger,
          Configuration.getIntSubProperty(loggers.item(i), "ReplayPeriod") * 60000);
      log.addOnlineLogger(alog);
    }
  }

  public CirculationHandler() {
    initiateOfflineSpooler();
    initiateSecurityDevice();
    initiateOnlineLoggers();
  }

  public String stripHtml(String data) {
    boolean omit = false;

    StringBuffer processed = new StringBuffer();

    for (int i=0; i<data.length(); i++) {
      if (data.charAt(i) == '<') {
        omit = true;
      } else if (data.charAt(i) == '>') {
        omit = false;
      } else if (!omit) {
        processed.append(data.charAt(i));
      }
    }
    return processed.toString();
  }

  public void printReceipt(String data) {
    if (!Configuration.getBoolProperty("Systems/Printer/PrintReceipt")) {
      return;
    }

    try {
      PrinterJob pj = PrinterJob.getPrinterJob();
      StringTokenizer tokens = new StringTokenizer(stripHtml(data), "\r\n", false);
      Vector items = new Vector();
      while (tokens.hasMoreTokens()) {
        String token = tokens.nextToken();
        if (token.length() > 0) {
          items.add(token);
        }
      }
      pj.setPrintable(new PrintItem(items.toArray()));
      pj.print();
    }
    catch (Exception e) {
      this.logger.error(e);
    }
  }

  private boolean connect() {
    conn = SelfIssueClient.ConfigureConnection();
    return conn.connect();
  }

  private void disconnect() {
    conn.disconnect();
  }

  public boolean process(Object o) {
    Message request;
    if (o == null) {
      logger.error("Null spool object");
      return true; //Whatever it is in the spool it isn't valid so delete
    }

    try {
      request = (Message) o;
    } catch (Exception ex) {
      logger.error("Invalid spool object: " + o);
      return true; //Whatever it is in the spool it isn't valid so delete
    }

    if (request.getClass() == CheckOut.class) {
      if (((CheckOut)request).getPatronIdentifier() == null) {
        logger.error("Null patron identifier in spool: " + request);
        return false;
      }
      if (((CheckOut)request).getItemIdentifier() == null) {
        logger.error("Null item identifier in spool: " + request);
        return false;
      }
    }

    Message response = null;

    try {
      response = unprotectedSend(request);
    } catch (RetriesExceeded ex) {
      logger.fatal("Invalid spool message: " + request);
      return true; //whatever is in the spool isn't valid so delete
    }

    if (response == null) {
      if (request instanceof CheckOut) {
        if (((CheckOut)request).getTransactionDate() != null) {
          Date transactionDate = ((CheckOut)request).getTransactionDate();
          Date currentDate = new Date();
          long age = (currentDate.getTime() - transactionDate.getTime())/(60*60*1000);
          if (age > Configuration.getIntProperty("Systems/Spooler/OverdueAgeWarn")) {
//            logger.warn("Item stored in spooler overdue: " + request);
            log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTPENDING, "", "Aged cached item warning", request, response);
          } else if (age > Configuration.getIntProperty("Systems/Spooler/OverdueAgeExpire")) {
//            logger.error("Item stored in spooler expired: " + request);
            log.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", "Cached item expired", request, response);
            return true;
          }
        }
      }
      return false;
    }

    if (response.getClass() == CheckOutResponse.class) {
      CheckOutResponse checkout = (CheckOutResponse) response;
      if (! ( (checkout.getOk() != null) ? checkout.getOk().booleanValue() : false)) {
        this.log.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", "", request, response);
      }
    } else {
      logger.error("Unexpected Checkout response: " + request + ", " + response);
    }

    return true;
  }

  public Message send(Message request) {
    try {
      return unprotectedSend(request);
    } catch (RetriesExceeded ex) {
      return null;
    }
  }

  private Message unprotectedSend(Message request) throws RetriesExceeded {
    if (request == null)
      throw new RetriesExceeded(); // request is invalid

    if (request.getClass() == CheckOut.class) {
      if (((CheckOut)request).getPatronIdentifier() == null) {
        logger.error("Null patron identifier in checkout request: " + request);
        return null;
      }
      if (((CheckOut)request).getItemIdentifier() == null) {
        logger.error("Null item identifier in checkout request: " + request);
        return null;
      }
    }

    Message response = null;
    SelfIssueClient.EnterCriticalSection();

    if (!this.connect()) {
      SelfIssueClient.LeaveCriticalSection();
      return null;
    }

    synchronized (this.conn) {
      SCStatus scstatus = new SCStatus();
      scstatus.setProtocolVersion("2.00");
      scstatus.setStatusCode("0");
      try {
        ACSStatus ascstatus = (ACSStatus) conn.send(scstatus);
        if (ascstatus == null) {
          this.disconnect();
          SelfIssueClient.LeaveCriticalSection();
          return null;
        }
        if (! ( (ascstatus.isCheckOutOk() != null) ?
               ascstatus.isCheckOutOk().booleanValue() : false)) {
          this.disconnect();
          SelfIssueClient.LeaveCriticalSection();
          return null;
        }
      } catch (RetriesExceeded ex) {
        this.disconnect();
        logger.error("Repeated retries on status request: " + request);
        SelfIssueClient.LeaveCriticalSection();
        throw ex;
      } catch (Exception ex) {
        this.disconnect();
        SelfIssueClient.LeaveCriticalSection();
        return null;
      }

      try {
        response = conn.send(request);
      } catch (RetriesExceeded ex) {
        this.disconnect();
        logger.error("Repeated retries on request: " + request);
        SelfIssueClient.LeaveCriticalSection();
        throw ex;
      } catch (ConnectionFailure ex) {
        response = null;
      }
      this.disconnect();
      SelfIssueClient.LeaveCriticalSection();
    }
    if (response != null) {
      log.recordEvent(OnlineLogEvent.STATUS_NOTIFICATION, "", "", request, response);
    }

    return response;
  }

  public Message checkStatus(int StatusCode) {
    if (!this.connect()) {
      return null;
    }

    SCStatus scstatus = new SCStatus();
    scstatus.setProtocolVersion("2.00");
    scstatus.setStatusCode(Integer.toString(StatusCode));
    try {
      ACSStatus ascstatus = (ACSStatus) conn.send(scstatus);
      this.disconnect();
      return ascstatus;
    } catch (Exception ex) {
      this.disconnect();
      return null;
    }
  }
}
