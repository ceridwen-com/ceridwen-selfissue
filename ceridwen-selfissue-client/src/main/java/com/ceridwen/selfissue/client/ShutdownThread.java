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
package com.ceridwen.selfissue.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ceridwen.circulation.SIP.messages.SCStatus;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.types.enumerations.ProtocolVersion;
import com.ceridwen.circulation.SIP.types.enumerations.StatusCode;
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.selfissue.client.core.ConnectionFactory;
import com.ceridwen.selfissue.client.devices.IDReaderDevice;
import com.ceridwen.selfissue.client.devices.SecurityDevice;
import com.ceridwen.util.versioning.LibraryIdentifier;
import com.ceridwen.util.versioning.LibraryRegistry;

public class ShutdownThread extends Thread {
    private static IDReaderDevice idReaderDevice = null;
    private static SecurityDevice securityDevice = null;

    private static Log log = LogFactory.getLog(ShutdownThread.class);
    private Connection conn;

    public ShutdownThread() {
        super();
        LibraryRegistry registry = new LibraryRegistry();
        LibraryIdentifier selfissueID = new LibraryIdentifier("com.ceridwen.selfissue", "Ceridwen SelfIssue Client");
        System.out.println(registry.getLibraryName(selfissueID) + " " +
                registry.getLibraryVersion(selfissueID) + " (" +
				registry.getLibraryBuildDate(selfissueID) + ")");

        System.out.println("Starting Up Self Issue Terminal...");

    }

    public static void registerSecurityDeviceShutdown(SecurityDevice d) {
        ShutdownThread.securityDevice = d;
    }

    public static void registerIDReaderDeviceShutdown(IDReaderDevice d) {
        ShutdownThread.idReaderDevice = d;
    }

    public static void shutdownSecurityDevice() {
        if (ShutdownThread.securityDevice != null) {
            System.out.println("Shutting Down Security Devices");
            synchronized (ShutdownThread.securityDevice) {
                ShutdownThread.securityDevice.deinit();
                ShutdownThread.securityDevice = null;
            }
        }
    }

    public static void shutdownRFIDDevice() {
        if (ShutdownThread.idReaderDevice != null) {
            System.out.println("Shutting Down Reader Devices...");
            synchronized (ShutdownThread.idReaderDevice) {
                ShutdownThread.idReaderDevice.stop();
                ShutdownThread.idReaderDevice.deinit();
                ShutdownThread.idReaderDevice = null;
            }
        }
    }

    private void sendShutdownStatus() {
        if (Configuration.getBoolProperty("Modes/SendShutdownStatus")) {
            System.out.println("Notifying Library System...");
            try {
                this.conn = ConnectionFactory.getConnection(true);
                SCStatus scstatus = new SCStatus();
                scstatus.setProtocolVersion(ProtocolVersion.VERSION_2_00);
                scstatus.setStatusCode(StatusCode.SHUTTING_DOWN);
                this.conn.send(scstatus);
                ConnectionFactory.releaseConnection(conn);
            } catch (Exception ex) {
            	try {
            		if (this.conn != null) {
            			this.conn.disconnect();
            		}
            	} catch (Exception exint) {

            	}
            }
        }
    }

    @Override
    public void run() {
        ShutdownThread.log.info("Shutting Down Self Issue Terminal");
        System.out.println("Shutting Down Self Issue Terminal...");
        System.out.println("Closing Pending Connections...");
        ConnectionFactory.releaseAll();
        ShutdownThread.shutdownSecurityDevice();
        ShutdownThread.shutdownRFIDDevice();
        this.sendShutdownStatus();
        System.out.println("Self Issue Terminal Shutdown Complete.");
    }
}
