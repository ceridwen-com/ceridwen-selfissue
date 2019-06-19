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
package com.ceridwen.selfissue.client.core;

import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.circulation.SIP.transport.Connection;
import com.ceridwen.circulation.SIP.transport.SSLSocketConnection;
import com.ceridwen.circulation.SIP.transport.SocketConnection;
import com.ceridwen.circulation.SIP.transport.TelnetConnection;
import com.ceridwen.selfissue.client.config.Configuration;

public class ConnectionFactory {

    private static Vector<Connection> connections = new Vector<Connection>();

    public static Connection getConnection(boolean autoConnect) throws Exception {
        Connection conn;

        String charset = Configuration.getProperty("Systems/SIP/CharsetEncoding");

        if (StringUtils.isNotBlank(charset)) {
            System.setProperty(Message.PROP_CHARSET, charset);
        }

        String ordering = Configuration.getProperty("Systems/SIP/FieldOrdering");

        if (Message.PROP_VARIABLE_FIELD_ORDERING_ALPHABETICAL.equalsIgnoreCase(ordering)) {
            System.setProperty(Message.PROP_VARIABLE_FIELD_ORDERING, Message.PROP_VARIABLE_FIELD_ORDERING_ALPHABETICAL);
        } else if (Message.PROP_VARIABLE_FIELD_ORDERING_SPECIFICATION.equalsIgnoreCase(ordering)) {
            System.setProperty(Message.PROP_VARIABLE_FIELD_ORDERING, Message.PROP_VARIABLE_FIELD_ORDERING_SPECIFICATION);
        } else {
            System.setProperty(Message.PROP_VARIABLE_FIELD_ORDERING, Message.PROP_VARIABLE_FIELD_ORDERING_DEFAULT);
        }

        if (Configuration.getProperty("Systems/SIP/@mode").equalsIgnoreCase("SSLSocket")) {
            conn = new SSLSocketConnection();
        } else if (Configuration.getProperty("Systems/SIP/@mode").equalsIgnoreCase("Socket")) {
            conn = new SocketConnection();
        } else {
            conn = new TelnetConnection();
            ((TelnetConnection) conn).setUsername(Configuration.getProperty("Systems/SIP/TelnetUsername"));
            ((TelnetConnection) conn).setPassword(Configuration.Decrypt(Configuration.getProperty("Systems/SIP/TelnetPassword")));
            ((TelnetConnection) conn).setLoggedOnText(Configuration.getProperty("Systems/SIP/LoggedOnText"));
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
