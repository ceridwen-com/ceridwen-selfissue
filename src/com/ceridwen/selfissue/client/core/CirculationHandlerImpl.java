/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * <http://www.gnu.org/licenses/>.
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
 *     Matthew J. Dovey - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client.core;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NodeList;

import com.ceridwen.circulation.SIP.exceptions.ConnectionFailure;
import com.ceridwen.circulation.SIP.exceptions.RetriesExceeded;
import com.ceridwen.circulation.SIP.messages.ACSStatus;
import com.ceridwen.circulation.SIP.messages.CheckOut;
import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.messages.SCStatus;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityDevice;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;
import com.ceridwen.selfissue.client.SelfIssueClient;
import com.ceridwen.selfissue.client.ShutdownThread;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.log.OnlineLog;
import com.ceridwen.selfissue.client.log.OnlineLogEvent;
import com.ceridwen.selfissue.client.log.OnlineLogLogger;
import com.ceridwen.selfissue.client.log.OnlineLogManager;
import com.ceridwen.selfissue.client.spooler.OfflineSpoolObject;
import com.ceridwen.selfissue.client.spooler.OfflineSpooler;
import com.ceridwen.selfissue.client.spooler.OfflineSpoolerDevice;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */


public class CirculationHandlerImpl implements com.ceridwen.util.SpoolerProcessor, CirculationHandler {

  private static Log logger = LogFactory.getLog(CirculationHandler.class);

  private Connection conn;
  private OfflineSpooler spool;
  public OnlineLogManager log ;
  public SecurityDevice securityDevice;

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#getSpoolerClass()
 */
public Class<? extends OfflineSpooler> getSpoolerClass() {
    return spool.getClass();
  }

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

  private void configureSecurityDevice() {
    try {
      securityDevice = (SecurityDevice) Class.forName(Configuration.getProperty(
          "Systems/Security/@class")).newInstance();
    } catch (Exception ex) {
      logger.warn("Could not initialise security device - defaulting to null device", ex);
      securityDevice = new com.ceridwen.selfissue.client.nulldevices.SecurityDevice();
    }
    securityDevice.setRetries(Configuration.getIntProperty(
        "Systems/Security/Retries"));
    securityDevice.setTimeOut(Configuration.getIntProperty(
        "Systems/Security/Timeout"));
    ShutdownThread.registerSecurityDeviceShutdown(securityDevice);
  }

  private void initiateOnlineLoggers()
  {
    NodeList loggers = Configuration.getPropertyList("Systems/Loggers/Logger");
    log = new OnlineLogManager();
    for (int i = 0; i < loggers.getLength(); i++) {
      java.io.File onlineLogDir = null;
      try {
        onlineLogDir = new java.io.File(
            Configuration.getSubProperty(loggers.item(i), "Spool"));
        OnlineLogLogger onlineLogger;
        try {
          onlineLogger = (OnlineLogLogger) Class.forName(
              Configuration.getSubProperty(loggers.item(i), "@class")).
              newInstance();
          onlineLogger.initialise(loggers.item(i));
          OnlineLog alog = new com.ceridwen.selfissue.client.log.
              OnlineLogDevice(onlineLogDir,
                              onlineLogger,
                              Configuration.getIntSubProperty(loggers.item(i),
              "ReplayPeriod") * 60000);
          log.addOnlineLogger(alog);
        } catch (Exception ex) {
          logger.error(ex);
        } catch (java.lang.NoClassDefFoundError ex) {
          logger.error(ex);
        }
      } catch (Exception ex) {
        logger.fatal("Can't create OnlineLogDirectory", ex);
      }
    }
  }

  public CirculationHandlerImpl() {
    initiateOfflineSpooler();
    initiateOnlineLoggers();
    configureSecurityDevice();
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

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#printReceipt(java.lang.String)
 */
public void printReceipt(String data) {
    if (!Configuration.getBoolProperty("Systems/Printer/PrintReceipt")) {
      return;
    }

    try {
      PrinterJob pj = PrinterJob.getPrinterJob();
      StringTokenizer tokens = new StringTokenizer(stripHtml(data), "\r\n", false);
      Vector<String> items = new Vector<String>();
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
      logger.error(e);
    }
  }

  private boolean connect() {
    conn = SelfIssueClient.ConfigureConnection();
    SelfIssueClient.EnterCriticalSection();
    boolean result = false;
    try {
      result = conn.connect();
    } catch (Exception ex) {
      logger.error("Unexpected exception on connection", ex);
    }
    if (!result) {
      SelfIssueClient.LeaveCriticalSection();
    }
    return result;
  }

  private void disconnect() {
    if (conn != null) {
      try {
        conn.disconnect();
      } catch (Exception ex) {
        logger.error("Unexpected exception on disconnection", ex);
      }
    }
    conn = null;
    SelfIssueClient.LeaveCriticalSection();
  }

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#spool(com.ceridwen.circulation.SIP.messages.Message)
 */
public void spool(Message msg) {
    this.spool.add(new OfflineSpoolObject(msg));
  }

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#getSpoolSize()
 */
public int getSpoolSize() {
    return this.spool.size();
  }

  public boolean process(Object o) {
    OfflineSpoolObject obj;
    if (o == null) {
      logger.error("Null spool object");
      return true; //Whatever it is in the spool it isn't valid so delete
    }
    try {
      obj = (OfflineSpoolObject) o;
    }
    catch (Exception ex) {
      logger.error("Invalid spool object: " + o);
      return true; //Whatever it is in the spool it isn't valid so delete
    }
    if (obj.getMessage() == null) {
      logger.error("Null spool object message: ");
      return true; //Whatever it is in the spool it isn't valid so delete
    }
    if (obj.isAboutToExpire()) {
      //            logger.error("Item stored in spooler expired: " + request);
      log.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "",
                      "Cached item expired", obj.getAdded(), obj.getMessage(), null);
      return true;
    }
    if (obj.isAboutToStale()) {
  //            logger.warn("Item stored in spooler overdue: " + request);
      log.recordEvent(OnlineLogEvent.STATUS_CHECKOUTPENDING, "",
                      "Aged cached item warning", obj.getAdded(), obj.getMessage(), null);
    }
    return processMessage(obj.getMessage());
  }

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#processMessage(com.ceridwen.circulation.SIP.messages.Message)
 */
public boolean processMessage(Message request) {
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
      return false;
    }

    if (response == null) {
      return false;
    }

    if (response.getClass() == CheckOutResponse.class) {
      CheckOutResponse checkout = (CheckOutResponse) response;
      if (! ( (checkout.getOk() != null) ? checkout.getOk().booleanValue() : false)) {
        this.log.recordEvent(OnlineLogEvent.STATUS_MANUALCHECKOUT, "", "", new Date(), request, response);
      }
    } else {
      logger.error("Unexpected Checkout response: " + request + ", " + response);
    }

    return true;
  }

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#send(com.ceridwen.circulation.SIP.messages.Message)
 */
public Message send(Message request) {
    try {
      return unprotectedSend(request);
    } catch (RetriesExceeded ex) {
      return null;
    }
  }

  private final static Object sync = new Object();

  private Message unprotectedSend(Message request) throws RetriesExceeded {
    if (request == null) {
      throw new RetriesExceeded(); // request is invalid
    }
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

    synchronized (sync) {
      if (this.connect()) {
        SCStatus scstatus = new SCStatus();
        scstatus.setProtocolVersion("2.00");
        scstatus.setStatusCode("0");
        try {
          ACSStatus ascstatus = (ACSStatus) conn.send(scstatus);
          if (ascstatus == null) {
            this.disconnect();
            return null;
          }
          if (! ( (ascstatus.isCheckOutOk() != null) ?
                 ascstatus.isCheckOutOk().booleanValue() : false)) {
            this.disconnect();
            return null;
          }
        } catch (RetriesExceeded ex) {
          logger.error("Repeated retries on status request: " + request);
          this.disconnect();
          throw ex;
        } catch (Exception ex) {
          logger.error("Unexpected error on status request: " + request, ex);
          this.disconnect();
          return null;
        }

        try {
          response = conn.send(request);
        } catch (RetriesExceeded ex) {
          this.disconnect();
          logger.error("Repeated retries on request: " + request);
          throw ex;
        } catch (ConnectionFailure ex) {
          response = null;
          logger.error("Connection Failure on request: " + request);
        } catch (Exception ex) {
          response = null;
          logger.error("Unexpected exception on request: " + request, ex);
        }
        this.disconnect();
      }
    }
    if (response != null) {
      log.recordEvent(OnlineLogEvent.STATUS_NOTIFICATION, "", "", new Date(), request, response);
    }

    return response;
  }

  /* (non-Javadoc)
 * @see com.ceridwen.selfissue.client.core.CirculationHandler#checkStatus(int)
 */
public String checkStatus(int statusCode)
  {
    try {
      ACSStatus response = (ACSStatus)this.status(statusCode);
      return "DateTimeSync: " + response.getDateTimeSync() + " | " +
          "InstId: " + response.getInstitutionId() + " | " +
          "LibName: " + response.getLibraryName() + " | " +
          "ProtVer: " + response.getProtocolVersion() + " | " +
          "Retries: " + response.getRetriesAllowed() + " | " +
          "SupMsgs: " + response.getSupportedMessages() + " | " +
          "TermLoc: " + response.getTerminalLocation() + " | " +
          "Timeout: " + response.getTimeoutPeriod() + " | " +
          "PrtLine: " + response.getPrintLine() + " | " +
          "SrcnMsg: " + response.getScreenMessage() + " | " +
          "CheckIn: " + response.isCheckInOk() + " | " +
          "CheckOut: " + response.isCheckOutOk() + " | " +
          "Offline: " + response.isOfflineOk() + " | " +
          "Online: " + response.isOnLineStatus() + " | " +
          "Renewal: " + response.isRenewalPolicy() + " | " +
          "StatusUpdate: " + response.isStatusUpdateOk();
    } catch (java.lang.ClassCastException ex) {
      return "Connection error";
    } catch (java.lang.NullPointerException ex) {
      return "Connection error";
    }

  }

  private Message status(int StatusCode) {
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
  
  public void stopSecurityDevice()
  {
    this.securityDevice.stop();
  }

  public void startSecurityDevice(SecurityListener listener)
  {
    this.securityDevice.start(listener);
  }

  public void initSecurityDevice()
  {
    this.securityDevice.init();
  }

  public void deinitSecurityDevice()
  {
    this.securityDevice.deinit();
  }

  public void resetSecurityDevice()
  {
    this.securityDevice.reset();
  }

  public void pauseSecurityDevice()
  {
    this.securityDevice.pause();
  }

  public void resumeSecurityDevice()
  {
    this.securityDevice.resume();
  }

  public void lockItem() throws TimeoutException, FailureException
  {
    this.securityDevice.lock();
  }

  public void unlockItem() throws TimeoutException, FailureException
  {
    this.securityDevice.unlock();
  }

  public Class<? extends SecurityDevice> getSecurityDeviceClass()
  {
    return this.securityDevice.getClass();
  }

  public void recordEvent(int level, String library, String addInfo,
                          Date originalTransactionTime, Message request, Message response)
  {
    this.log.recordEvent(level, library, addInfo, originalTransactionTime, request, response);
  }
}

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

        if (pageIndex >= 1) {
          return Printable.NO_SUCH_PAGE;
        }
        Graphics2D graphics2d = (Graphics2D) graphics;
        graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        graphics2d.setPaint(Color.black);
        Point2D.Float pen = new Point2D.Float();

        for (int i=0; i < attributedItems.length; i++) {
            printText(graphics2d, pageFormat, pen, attributedItems[i]);
        }

        return Printable.PAGE_EXISTS;
    }

    private static void printText(Graphics2D graphics2d, PageFormat pageFormat, Point2D.Float pen, AttributedString text) {

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
