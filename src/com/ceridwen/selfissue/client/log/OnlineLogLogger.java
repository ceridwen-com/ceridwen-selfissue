package com.ceridwen.selfissue.client.log;
import com.ceridwen.selfissue.client.Configuration;
import org.w3c.dom.*;
import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.circulation.SIP.messages.CheckInResponse;
import com.ceridwen.circulation.SIP.messages.CheckOut;
import com.ceridwen.circulation.SIP.messages.PatronInformation;
import com.ceridwen.circulation.SIP.messages.CheckIn;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public abstract class OnlineLogLogger implements com.ceridwen.util.SpoolerProcessor {
  int eventMask;

  public boolean process(Object o) {
    try {
      OnlineLogEvent ev = (OnlineLogEvent)o;
      if ((ev.getLevel() & eventMask) == 0) {
        return true;
      }
      return log(ev);
    } catch (Exception ex) {
      return false;
    }
  }

  public void initialise(Node config) {
    eventMask = Configuration.getIntSubProperty(config, "EventMask");
  }

  public abstract boolean log(OnlineLogEvent event);

  public String getSubject(OnlineLogEvent event) {
    String subjectType = null;
    if (event.getLevel() == OnlineLogEvent.STATUS_MANUALCHECKOUT) {
      subjectType = "Manual Intervention Required (action required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTFAILURE) {
      subjectType = "Checkout Failure Notification (no action required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTSUCCESS) {
      subjectType = "Checkout Success Notification (no action required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTPENDING) {
      subjectType = "Checkout Pending Notification (no action required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_NOTIFICATION) {
      subjectType = "Notification (no action required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_UNLOCKFAILURE) {
      subjectType = "Unlock Failure (action may be required)";
    }
    else if (event.getLevel() == OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE) {
      subjectType = "Cancel Checkout Failure (action may be required)";
    }

    return "Self Issue Report: " + ( (subjectType == null) ? "" : subjectType);
  }

  public String getMessage(OnlineLogEvent event) {
    String patronId = null;
    String itemId = null;
    String addInfo = null;
    String type = null;

    if (event.getResponse() != null) {
      if (event.getResponse() instanceof CheckOutResponse) {
        patronId = ( (CheckOutResponse) event.getResponse()).
                   getPatronIdentifier();
        itemId = ( (CheckOutResponse) event.getResponse()).getItemIdentifier();
        addInfo = ( (CheckOutResponse) event.getResponse()).getScreenMessage();
        type = "Check-out";
      }
      else if (event.getResponse() instanceof PatronInformationResponse) {
        patronId = ( (PatronInformationResponse) event.getResponse()).
                   getPatronIdentifier();
        addInfo = ( (PatronInformationResponse) event.getResponse()).getScreenMessage();
        type = "Patron Information";
      }
      else if (event.getResponse() instanceof CheckInResponse) {
        patronId = ( (CheckInResponse) event.getResponse()).getPatronIdentifier();
        itemId = ( (CheckInResponse) event.getResponse()).getItemIdentifier();
        addInfo = ( (CheckInResponse) event.getResponse()).getScreenMessage();
        type = "Check-in";
      }
    }
    if (patronId == null) {
      if (event.getRequest() instanceof CheckOut) {
        patronId = ( (CheckOut) event.getRequest()).getPatronIdentifier();
        type = "Check-out";
      }
      else if (event.getRequest() instanceof PatronInformation) {
        patronId = ( (PatronInformation) event.getRequest()).getPatronIdentifier();
        type = "Patron Information";
      }
    }
    if (itemId == null) {
      if (event.getRequest() instanceof CheckOut) {
        itemId = ( (CheckOut) event.getRequest()).getItemIdentifier();
        type = "Check-out";
      }
      else if (event.getRequest() instanceof CheckIn) {
        itemId = ( (CheckIn) event.getRequest()).getItemIdentifier();
        type = "Check-in";
      }
    }
    if (addInfo == null) {
      addInfo = event.getAddInfo();
    }
    return "Action: " + ( (type == null) ? "Unknown" : type) + "\r\n" +
        "Patron: " + ( (patronId == null) ? "Unknown" : patronId) + "\r\n" +
        "Item: " + ( (itemId == null) ? "Unknown" : itemId) + "\r\n" +
        ( (addInfo == null) ? "" : "Additional Information: " + addInfo) + "\r\n";
  }
}
