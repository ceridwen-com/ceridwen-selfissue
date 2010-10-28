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
/**
 * <p>Title: Self Issue</p>
 * <p>Description: Self Issue Client</p>
 * <p>Copyright: 2004,</p>
 * <p>Company: ceridwen.com</p>
 * @author Matthew J. Dovey
 * @version 2.1
 */

package com.ceridwen.circulation.rfid;

import com.ceridwen.selfissue.client.SelfIssueClient;

public abstract class RFIDDevice {
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
 * Initialise the security/RFID device  
 */
  public abstract void init();
/**
 * Start the security/RFID device listening for new items  
 * @param listener listener class to fire detected ids to
 */
  public abstract void start(RFIDDeviceListener listener);
/**
 * Stop the security/RFID device listening for new items  
 */
  public abstract void stop();
/**
 * Reset the security/RFID device  
 */
  public abstract void reset();
/**
 * Pause the security/RFID device listening for new items  
 */
  public abstract void pause();
/**
 * Resume the security/RFID device listening for new items  
 */
  public abstract void resume();
/**
 * De-initialise the security/RFID device  
 */
  public abstract void deinit();
/**
 * Lock the currently detected item  
 * @throws TimeoutException
 * @throws FailureException
 */
  public abstract void lock() throws TimeoutException, FailureException;
/**
 * Unlock the currently detected item  
 * @throws TimeoutException
 * @throws FailureException
 */
  public abstract void unlock() throws TimeoutException, FailureException;
/**
 * Report the locked state of the detected item  
 * @return
 * @throws TimeoutException
 * @throws FailureException
 */
  public abstract boolean isLocked() throws TimeoutException, FailureException;
  
  static {
	    com.ceridwen.util.versioning.ComponentRegistry.registerComponent(RFIDDevice.class);
  }  
}
