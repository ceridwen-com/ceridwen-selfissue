/**
 * <p>Title: Self Issue</p>
 * <p>Description: Self Issue Client</p>
 * <p>Copyright: 2004,</p>
 * <p>Company: ceridwen.com</p>
 * @author Matthew J. Dovey
 * @version 2.1
 */

package com.ceridwen.circulation.security;

public abstract class SecurityDevice {
  private int timeOut;
  private int retries;
  public int getTimeOut() {
    return timeOut;
  }
  public void setTimeOut(int timeOut) {
    this.timeOut = timeOut;
  }
  public int getRetries() {
    return retries;
  }
  public void setRetries(int retries) {
    this.retries = retries;
  }

  public abstract void init();
  public abstract void start(SecurityListener listener);
  public abstract void stop();
  public abstract void reset();
  public abstract void pause();
  public abstract void resume();
  public abstract void deinit();
  public abstract void lock() throws TimeoutException, FailureException;
  public abstract void unlock() throws TimeoutException, FailureException;
  public abstract boolean isLocked() throws TimeoutException, FailureException;
}
