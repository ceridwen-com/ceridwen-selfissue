package com.ceridwen.selfissue.client.nulldevices;

import com.ceridwen.circulation.security.*;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class SecurityDevice extends com.ceridwen.circulation.security.SecurityDevice {
  public SecurityDevice() {
  }


  public void init() {
    return;
  }

  public void deinit() {
    return;
  }

  /**
   * isLocked
   *
   * @throws Exception
   * @return boolean
   * @todo Implement this com.ceridwen.circulation.security.SecurityDevice method
   */
  public boolean isLocked() throws TimeoutException, FailureException {
    return false;
  }

  /**
   * lock
   *
   * @throws Exception
   */
  public void lock() throws TimeoutException, FailureException {
  }

  /**
   * unlock
   *
   * @throws Exception
   */
  public void unlock() throws TimeoutException, FailureException {
  }

  /**
   * stop
   *
   */
  public void stop() {
  }

  /**
   * start
   *
   * @param listener SecurityListener
   */
  public void start(SecurityListener listener) {
  }
}
