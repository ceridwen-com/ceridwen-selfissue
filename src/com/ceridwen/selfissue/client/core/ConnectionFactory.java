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

package com.ceridwen.selfissue.client.core;

import java.util.Vector;

import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.transport.TelnetConnection;
import com.ceridwen.selfissue.client.config.Configuration;

public class ConnectionFactory {
	private static Vector<Connection> connections = new Vector<Connection>();

	public static Connection getConnection(boolean autoConnect) throws Exception {
		Connection conn;

		if (Configuration.getProperty("Systems/SIP/@mode").equalsIgnoreCase("Socket")) {
			conn = new SocketConnection();
		} else {
			conn = new TelnetConnection();
			((TelnetConnection)conn).setUsername(Configuration.getProperty("Systems/SIP/TelnetUsername"));
			((TelnetConnection)conn).setPassword(Configuration.Decrypt(Configuration.getProperty("Systems/SIP/TelnetPassword")));
			((TelnetConnection)conn).setLoggedOnText(Configuration.getProperty("Systems/SIP/LoggedOnText"));
		}

		conn.setHost(Configuration.getProperty("Systems/SIP/Host"));
		conn.setPort(Configuration.getIntProperty("Systems/SIP/Port"));
		conn.setConnectionTimeout(Configuration.getIntProperty("Systems/SIP/ConnectionTimeout") * 1000);
		conn.setIdleTimeout(Configuration.getIntProperty("Systems/SIP/IdleTimeout") * 1000);
		conn.setRetryAttempts(Configuration.getIntProperty("Systems/SIP/RetryAttempts"));
		conn.setRetryWait(Configuration.getIntProperty("Systems/SIP/RetryWait"));
		conn.setAddSequenceAndChecksum(Configuration.getBoolProperty("Systems/SIP/AddSequenceAndChecksum"));
		conn.setStrictChecksumChecking(Configuration.getBoolProperty("Systems/SIP/StrictChecksumChecking"));
		conn.setStrictSequenceChecking(Configuration.getBoolProperty("Systems/SIP/StrictSequenceChecking"));

		if (autoConnect) {
			conn.connect();
		}
		connections.add(conn);        

		return conn;
	}

	public static void releaseConnection(Connection conn) {
		if (conn != null) {
			conn.disconnect();
			connections.remove(conn);
		}
	}

	public static boolean areConnectionsActive() {
		return (!connections.isEmpty());
	}

	public static void releaseAll() {
		while (!connections.isEmpty()) {
			Connection conn = connections.firstElement();
			releaseConnection(conn);
		}
	}

}
