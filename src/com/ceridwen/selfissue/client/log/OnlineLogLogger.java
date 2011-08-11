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
package com.ceridwen.selfissue.client.log;
import java.text.DateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import com.ceridwen.circulation.SIP.messages.*;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.OutOfOrderInterface;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public abstract class OnlineLogLogger implements com.ceridwen.util.SpoolerProcessor {
  private static Log logger = LogFactory.getLog(OnlineLogLogger.class);

  protected int eventMask;
  protected long overdueAgeOOO;
  protected OutOfOrderInterface ooo;

  public boolean process(Object o) {
    try {
      OnlineLogEvent ev = (OnlineLogEvent)o;
      if ((ev.getLevel() & eventMask) == 0) {
        return true;
      }
      boolean response = log(ev);
      if (!response && overdueAgeOOO > 0) {
          long age = (new Date().getTime() - ev.getTimeStamp().getTime())/(60*60*1000);
          if (age > overdueAgeOOO) {
        	  if (!ooo.getOutOfOrder()) {
        		  logger.fatal("OnlineLogEvent Expired forcing Out Of Order state");
        		  ooo.setOutOfOrder(true);
        	  }        	          	  
          }
      }
      return response;
    } catch (Exception ex) {
      return false;
    }
  }
  
  private int generateEventMask(Node eventMask) {
	  if (eventMask == null) { return 0; }
	  int ret = 0;
	  //@todo
	  if (Configuration.getBoolSubProperty(eventMask, "CancelCheckoutFailure")) { ret += OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckinFailure")) { ret += OnlineLogEvent.STATUS_CHECKINFAILURE; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckinPending")) { ret += OnlineLogEvent.STATUS_CHECKINPENDING; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckinSuccess")) { ret += OnlineLogEvent.STATUS_CHECKINSUCCESS; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckoutFailure")) { ret += OnlineLogEvent.STATUS_CHECKOUTFAILURE; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckoutPending")) { ret += OnlineLogEvent.STATUS_CHECKOUTPENDING; }
	  if (Configuration.getBoolSubProperty(eventMask, "CheckoutSuccess")) { ret += OnlineLogEvent.STATUS_CHECKOUTSUCCESS; }
	  if (Configuration.getBoolSubProperty(eventMask, "LockFailure")) { ret += OnlineLogEvent.STATUS_LOCKFAILURE; }
	  if (Configuration.getBoolSubProperty(eventMask, "LockSuccess")) { ret += OnlineLogEvent.STATUS_LOCKSUCCESS; }
	  if (Configuration.getBoolSubProperty(eventMask, "ManualCheckout")) { ret += OnlineLogEvent.STATUS_MANUALCHECKOUT; }
	  if (Configuration.getBoolSubProperty(eventMask, "Notification")) { ret += OnlineLogEvent.STATUS_NOTIFICATION; }
	  if (Configuration.getBoolSubProperty(eventMask, "UnlockFailure")) { ret += OnlineLogEvent.STATUS_UNLOCKFAILURE; }
	  if (Configuration.getBoolSubProperty(eventMask, "UnlockSuccess")) { ret += OnlineLogEvent.STATUS_UNLOCKSUCCESS; }
	  return ret;
  }

  public void initialise(Node config, OutOfOrderInterface ooo) {
    this.eventMask = this.generateEventMask(Configuration.getSubPropertyNode(config, "EventMask"));
    this.overdueAgeOOO = Configuration.getIntSubProperty(config, "OverdueAgeOutOfOrder");
    this.ooo = ooo;
  }

  public abstract boolean log(OnlineLogEvent event);

  public String getSubjectType(OnlineLogEvent event) {
	  if (event.getLevel() == OnlineLogEvent.STATUS_MANUALCHECKOUT) {
	    return "Manual Intervention Required (action required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTFAILURE) {
		return "Checkout Failure Notification (no action required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTSUCCESS) {
		return "Checkout Success Notification (no action required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_CHECKOUTPENDING) {
		return "Checkout Pending Notification (no action required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_NOTIFICATION) {
	    return  "Notification (no action required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_UNLOCKFAILURE) {
		return "Unlock Failure (action may be required)";
	  }
	  else if (event.getLevel() == OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE) {
		return "Cancel Checkout Failure (action may be required)";
	  }
	  return null;
  }
  
  public String getSubject(OnlineLogEvent event) {
    String subjectType = this.getSubjectType(event);
    return "Self Issue Report: " + ( (subjectType == null) ? "" : subjectType);
  }

protected MessageComponents getMessageComponents(OnlineLogEvent event) {
    String subjectType = null;
	String patronId = null;
    String itemId = null;
    String addInfo = null;
    String type = null;

    subjectType = this.getSubjectType(event);
    
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
    return new MessageComponents(patronId, itemId, addInfo, type, subjectType,  DateFormat.getDateInstance().format(event.getOriginalTransactionTime()), null);
  }

  public String getMessage(OnlineLogEvent event) {
    MessageComponents msg = this.getMessageComponents(event);
    return 
        "Type: " + msg.subjectType + "\r\n" +
        "Action: " + msg.type + "\r\n" +
        "Transaction Time:" + msg.originalTransactionTime + "\r\n" +
        "Patron: " + msg.patronId + "\r\n" +
        "Item: " + msg.itemId + "\r\n" +
        ( (msg.addInfo == null) ? "" : "Additional Information: " + msg.addInfo) + "\r\n";
  }
}
