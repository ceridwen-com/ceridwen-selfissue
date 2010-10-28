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
package com.ceridwen.selfissue.client.nulldevices;

import com.ceridwen.circulation.rfid.FailureException;
import com.ceridwen.circulation.rfid.RFIDDeviceListener;
import com.ceridwen.circulation.rfid.TimeoutException;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class RFIDDevice extends com.ceridwen.circulation.rfid.RFIDDevice {
  public RFIDDevice() {
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
   * @param listener RFIDDeviceListener
   */
  public void start(RFIDDeviceListener listener) {
  }

  public void reset() {
  }

  public void pause() {
  }

  public void resume() {
  }

}
