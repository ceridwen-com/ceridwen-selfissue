/* 
 * Copyright (C) 2019 Ceridwen Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ceridwen.selfissue.client.devices;

import org.w3c.dom.Node;

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

  
/**
 * Initialise the security device  
 * @param node configuration parameters
 */
  public abstract void init(Node node);
/**
 * De-initialise the security/RFID device  
 */
  public abstract void deinit();
/**
 * Lock the currently detected item  
 * @throws TimeoutException device timed out waiting for response
 * @throws FailureException device defined error
 */  
  public abstract void lock() throws TimeoutException, FailureException;
/**
 * Unlock the currently detected item  
 * @throws TimeoutException device timed out waiting for response
 * @throws FailureException device defined error
 */
  public abstract void unlock() throws TimeoutException, FailureException;
/**
 * Report the locked state of the detected item  
 * @return is the item security tag set
 * @throws TimeoutException device timed out waiting for response
 * @throws FailureException device defined error
 */
  public abstract boolean isLocked() throws TimeoutException, FailureException;  
}
