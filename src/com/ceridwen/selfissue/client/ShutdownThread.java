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
package com.ceridwen.selfissue.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.ACSStatus;
import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.messages.SCStatus;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.devices.RFIDDevice;
import com.ceridwen.selfissue.client.config.Configuration;

public class ShutdownThread extends Thread {
  private static RFIDDevice device = null;

  private static Log log = LogFactory.getLog(ShutdownThread.class);
  private Connection conn;

  public ShutdownThread() {
    super();
  }

  public static void registerSecurityDeviceShutdown(RFIDDevice d) {
    device = d;
  }

  public static void shutdownSecurityDevice() {
    synchronized (device) {
      device.stop();
      device.deinit();
      device = null;
    }
  }

  private boolean connect() {
    conn = SelfIssueClient.ConfigureConnection();
    return conn.connect();
  }

  private void disconnect() {
    conn.disconnect();
  }

  private Message sendShutdownStatus() {
    if (!Configuration.getBoolProperty("Modes/SendShutdownStatus")) {
      return null;
    }

    if (!this.connect()) {
      return null;
    }

    SCStatus scstatus = new SCStatus();
    scstatus.setProtocolVersion("2.00");
    scstatus.setStatusCode("2");
    try {
      ACSStatus ascstatus = (ACSStatus) conn.send(scstatus);
      this.disconnect();
      return ascstatus;
    } catch (Exception ex) {
      this.disconnect();
      return null;
    }
  }


  public void run() {
    log.error("Shutting Down Self Issue Terminal");
    shutdownSecurityDevice();
    System.out.println("Shutting Down Self Issue Terminal...");
    this.sendShutdownStatus();
  }
}
