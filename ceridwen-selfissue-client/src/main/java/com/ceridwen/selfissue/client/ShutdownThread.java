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
package com.ceridwen.selfissue.client;

import com.ceridwen.circulation.SIP.exceptions.ChecksumError;
import com.ceridwen.circulation.SIP.exceptions.ConnectionFailure;
import com.ceridwen.circulation.SIP.exceptions.InvalidFieldLength;
import com.ceridwen.circulation.SIP.exceptions.MandatoryFieldOmitted;
import com.ceridwen.circulation.SIP.exceptions.MessageNotUnderstood;
import com.ceridwen.circulation.SIP.exceptions.RetriesExceeded;
import com.ceridwen.circulation.SIP.exceptions.SequenceError;
import com.ceridwen.circulation.SIP.messages.Login;
import com.ceridwen.circulation.SIP.messages.LoginResponse;
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
import org.apache.commons.lang3.StringUtils;

public class ShutdownThread extends Thread {
    private static IDReaderDevice idReaderDevice = null;
    private static SecurityDevice securityDevice = null;

    private static Log log = LogFactory.getLog(ShutdownThread.class);
    private Connection conn;

    public ShutdownThread() {
        super();
        LibraryRegistry registry = new LibraryRegistry();
        LibraryIdentifier selfissueID = new LibraryIdentifier("com.ceridwen.selfissue", "com.ceridwen.selfissue:ceridwen-selfissue-client");
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
    
    private boolean doLogin(Connection connection) throws RetriesExceeded, ConnectionFailure, ChecksumError, SequenceError, MessageNotUnderstood, MandatoryFieldOmitted, InvalidFieldLength {
        if (StringUtils.isEmpty(Configuration.getProperty("Systems/SIP/LoginUserId"))) {
            return true;
        }
        if (StringUtils.isEmpty(Configuration.getProperty("Systems/SIP/LoginPassword"))) {
            return true;
        }
        Login login = new Login();
        login.setLoginUserId(Configuration.getProperty("Systems/SIP/LoginUserId"));
        login.setLoginPassword(Configuration.Decrypt(Configuration.getProperty("Systems/SIP/LoginPassword")));
        login.setLocationCode(Configuration.getProperty("Systems/SIP/LocationCode"));
        login.setPWDAlgorithm(Configuration.getProperty("Systems/SIP/PWDAlgorithm"));
        login.setUIDAlgorithm(Configuration.getProperty("Systems/SIP/UIDAlgorithm"));

        LoginResponse response = (LoginResponse) connection.send(login);
        return ((response.isOk() != null) ? response.isOk().booleanValue() : false);
    }
    

    private void sendShutdownStatus() {
        if (Configuration.getBoolProperty("Systems/Modes/SendShutdownStatus")) {
            System.out.println("Notifying Library System...");
            try {
              this.conn = ConnectionFactory.getConnection(true);
              if (this.doLogin(this.conn)) {
                SCStatus scstatus = new SCStatus();
                scstatus.setProtocolVersion(ProtocolVersion.VERSION_2_00);
                scstatus.setStatusCode(StatusCode.SHUTTING_DOWN);
                this.conn.send(scstatus);
                ConnectionFactory.releaseConnection(this.conn);
              }
            } catch (Exception ex) {
            	try {
            		if (this.conn != null) {
            			this.conn.disconnect();
                  ConnectionFactory.releaseConnection(this.conn);
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
