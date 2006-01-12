package com.ceridwen.selfissue.client.log;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;
import javax.management.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.util.management.notification.ThreadedNotificationBroadcasterSupport;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class JMXLogger extends OnlineLogLogger implements NotificationEmitter,
    JMXLoggerMBean
{
  private static Log log = LogFactory.getLog(JMXLogger.class);
  private ThreadedNotificationBroadcasterSupport broadcaster = new
      ThreadedNotificationBroadcasterSupport();
  private long seq = 0;

  private void initializeManagement()
  {
    try {
      MBeanServer mbs;
      mbs =
          ManagementFactory.getPlatformMBeanServer();
      StandardMBean mbean = new StandardMBean(this,
                                              JMXLoggerMBean.class);
      mbs.registerMBean(mbean,
                        new ObjectName(
          "com.ceridwen.selfissue.client.log:type=Monitor"));
    } catch (NullPointerException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MalformedObjectNameException ex1) {
      log.fatal("MBean error", ex1);
    } catch (MBeanRegistrationException ex1) {
      log.fatal("MBean error", ex1);
    } catch (InstanceAlreadyExistsException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NotCompliantMBeanException ex1) {
      log.fatal("MBean error", ex1);
    } catch (NoClassDefFoundError ex1) {
      log.fatal("MBean error", ex1);
    }
  }

  public JMXLogger()
  {
    initializeManagement();
  }

  public boolean log(OnlineLogEvent event)
  {
    MessageComponents msg = this.getMessageComponents(event);
    Notification notification = new Notification(this.getTypeName(event.
        getLevel()), this, seq++, (msg.addInfo==null)?msg.type:msg.addInfo);
    Hashtable userdata = new Hashtable();
    userdata.put("Patron Id", msg.patronId);
    userdata.put("Item Id", msg.itemId);
    userdata.put("Type", msg.type);
    notification.setUserData(userdata);
    broadcaster.sendNotification(notification);
    return true;
  }

  public void removeNotificationListener(NotificationListener listener,
                                         NotificationFilter filter,
                                         Object handback) throws
      ListenerNotFoundException
  {
    broadcaster.removeNotificationListener(listener, filter, handback);
  }

  public void addNotificationListener(NotificationListener listener,
                                      NotificationFilter filter,
                                      Object handback) throws
      IllegalArgumentException
  {
    broadcaster.addNotificationListener(listener, filter, handback);
  }

  public void removeNotificationListener(NotificationListener listener) throws
      ListenerNotFoundException
  {
    broadcaster.removeNotificationListener(listener);
  }

  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return new MBeanNotificationInfo[] {
        new MBeanNotificationInfo(new String[] {
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKOUTSUCCESS),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKOUTPENDING),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKOUTFAILURE),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_MANUALCHECKOUT)
    }, "Checkout Notifications", "Checkout Notifications"),
        new MBeanNotificationInfo(new String[] {
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKINSUCCESS),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKINPENDING),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CHECKINFAILURE)
    }, "Checkin Notifications", "Checkin Notifications"),
        new MBeanNotificationInfo(new String[] {
                                  getTypeName(OnlineLogEvent.STATUS_LOCKSUCCESS),
                                  getTypeName(OnlineLogEvent.STATUS_LOCKFAILURE),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_UNLOCKSUCCESS),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_UNLOCKFAILURE),
                                  getTypeName(OnlineLogEvent.
                                              STATUS_CANCELCHECKOUTFAILURE)
    }, "Security Notifications", "Security Notifications"),
        new MBeanNotificationInfo(new String[] {
                                  getTypeName(OnlineLogEvent.
                                              STATUS_NOTIFICATION)
    }, "General Notifications", "General Notifications")};
  }

  public String getTypeName(int type)
  {
    String[] typenames = {
        "Unused0",
        "CheckoutSuccess", "CheckoutFailure", "CheckoutPending",
        "ManualCheckout",
        "Notification", "UnlockFailure", "CancelCheckoutFailure",
        "UnlockSuccess",
        "CheckinSuccess", "CheckinFailure", "CheckinPending", "LockFailure",
        "LockSuccess", "Unused14", "Unused15", "Unused16"};
    int bit = 0;
    while (bit < 16) {
      if ( (type & (1<<bit)) > 0) {
        return typenames[bit + 1];
      } else {
        bit++;
      }
    }
    return typenames[0];
  }

  public boolean isReportCheckoutSuccess()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKOUTSUCCESS) > 0);
  }

  public void setReportCheckoutSuccess(boolean reportCheckoutSuccess)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKOUTSUCCESS;
  }

  public void setReportCheckoutFailure(boolean reportCheckoutFailure)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKOUTFAILURE;
  }

  public boolean isReportCheckoutFailure()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKOUTFAILURE) > 0);
  }

  public void setReportCheckoutPending(boolean reportCheckoutPending)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKOUTPENDING;
  }

  public boolean isReportCheckoutPending()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKOUTPENDING) > 0);
  }

  public void setReportManualCheckout(boolean reportManualCheckout)
  {
    this.eventMask |= OnlineLogEvent.STATUS_MANUALCHECKOUT;
  }

  public boolean isReportManualCheckout()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_MANUALCHECKOUT) > 0);
  }

  public void setReportNotification(boolean reportNotification)
  {
    this.eventMask |= OnlineLogEvent.STATUS_NOTIFICATION;
  }

  public boolean isReportNotification()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_NOTIFICATION) > 0);
  }

  public void setReportUnlockFailure(boolean reportUnlockFailure)
  {
    this.eventMask |= OnlineLogEvent.STATUS_UNLOCKFAILURE;
  }

  public boolean isReportUnlockFailure()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_UNLOCKFAILURE) > 0);
  }

  public void setReportCancelCheckoutFailure(boolean
                                             reportCancelCheckoutFailure)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKOUTFAILURE;
  }

  public boolean isReportCancelCheckoutFailure()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CANCELCHECKOUTFAILURE) > 0);
  }

  public void setReportCheckinSuccess(boolean reportCheckinSuccess)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKINSUCCESS;
  }

  public boolean isReportCheckinSuccess()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKINSUCCESS) > 0);
  }

  public void setReportCheckinFailure(boolean reportCheckinFailure)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKINFAILURE;
  }

  public boolean isReportCheckinFailure()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKINFAILURE) > 0);
  }

  public void setReportCheckinPending(boolean reportCheckinPending)
  {
    this.eventMask |= OnlineLogEvent.STATUS_CHECKINPENDING;
  }

  public boolean isReportCheckinPending()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_CHECKINPENDING) > 0);
  }

  public void setReportLockFailure(boolean reportLockFailure)
  {
    this.eventMask |= OnlineLogEvent.STATUS_LOCKFAILURE;
  }

  public boolean isReportLockFailure()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_LOCKFAILURE) > 0);
  }

  public void setReportLockSuccess(boolean reportLockSuccess)
  {
    this.eventMask |= OnlineLogEvent.STATUS_LOCKSUCCESS;
  }

  public boolean isReportLockSuccess()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_LOCKSUCCESS) > 0);
  }

  public void setReportUnlockSuccess(boolean reportUnlockSuccess)
  {
    this.eventMask |= OnlineLogEvent.STATUS_UNLOCKSUCCESS;
  }

  public boolean isReportUnlockSuccess()
  {
    return ((this.eventMask & OnlineLogEvent.STATUS_UNLOCKSUCCESS) > 0);
  }
}
