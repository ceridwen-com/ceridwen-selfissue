package com.ceridwen.selfissue.client.log;

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
public interface JMXLoggerMBean
{
  boolean isReportCheckoutSuccess();

  void setReportCheckoutSuccess(boolean reportCheckoutSuccess);

  void setReportCheckoutFailure(boolean reportCheckoutFailure);

  boolean isReportCheckoutFailure();

  void setReportCheckoutPending(boolean reportCheckoutPending);

  boolean isReportCheckoutPending();

  void setReportManualCheckout(boolean reportManualCheckout);

  boolean isReportManualCheckout();

  void setReportNotification(boolean reportNotification);

  boolean isReportNotification();

  void setReportUnlockFailure(boolean reportUnlockFailure);

  boolean isReportUnlockFailure();

  void setReportCancelCheckoutFailure(boolean
                                      reportCancelCheckoutFailure);

  boolean isReportCancelCheckoutFailure();

  void setReportCheckinSuccess(boolean reportCheckinSuccess);

  boolean isReportCheckinSuccess();

  void setReportCheckinFailure(boolean reportCheckinFailure);

  boolean isReportCheckinFailure();

  void setReportCheckinPending(boolean reportCheckinPending);

  boolean isReportCheckinPending();

  void setReportLockFailure(boolean reportLockFailure);

  boolean isReportLockFailure();

  void setReportLockSuccess(boolean reportLockSuccess);

  boolean isReportLockSuccess();

  void setReportUnlockSuccess(boolean reportUnlockSuccess);

  boolean isReportUnlockSuccess();
}
