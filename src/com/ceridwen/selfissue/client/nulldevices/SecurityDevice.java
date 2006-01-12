package com.ceridwen.selfissue.client.nulldevices;

import com.ceridwen.circulation.security.FailureException;
import com.ceridwen.circulation.security.SecurityListener;
import com.ceridwen.circulation.security.TimeoutException;

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

  public void reset() {
  }

  public void pause() {
  }

  public void resume() {
  }

}
