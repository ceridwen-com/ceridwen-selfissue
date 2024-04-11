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
package com.ceridwen.selfissue.client.log;

import java.text.SimpleDateFormat;

import org.apache.commons.net.smtp.AuthenticatingSMTPClient;
import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class MailLogger extends OnlineLogLogger {

  @Override
  public void initialise(Node config, OutOfOrderInterface ooo) {
    super.initialise(config, ooo);
  }

  public synchronized boolean sendSMTPMessage(String subject, String message) {
    try {
      AuthenticatingSMTPClient smtp = new AuthenticatingSMTPClient("TLS", false);
      smtp.connect(host, port);
      smtp.setSoTimeout(idleTimeout);
      
        if (ssl) {
            if (!smtp.elogin()) {
                throw new IOException("EHLO failed on host " + host);
            }
            if (!smtp.execTLS()) {
                throw new IOException("StartTLS failed on host " + host);
            }
        } else {
            if (!smtp.login()) {
                throw new IOException("HELO failed on host " + host);
            }
        }

        if (username != null && !username.isBlank()) {
            if (!smtp.elogin()) {
                throw new IOException("EHLO failed on host " + host);
            }                
            if (!smtp.auth(AuthenticatingSMTPClient.AUTH_METHOD.PLAIN, username, password)) {
                throw new IOException("Authentication failed on host " + host);
            }
        }  

        if (!smtp.sendSimpleMessage(source,
                                    target,
                                    "From: " + source + "\r\n" +
                                    "To: " + target + "\r\n" +
                                    "Date: " +
                                    new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z").
                                    format(new java.util.Date()) + "\r\n" +
                                    "Subject: " + subject + "\r\n\r\n" + message)) {
            throw new IOException("SMTP send failed on host " + host);
        }

      smtp.logout();
      smtp.disconnect();
      return true;
    }
    catch (IOException | NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException ex) {
      return false;
    }
  }

  @Override
  public boolean log(OnlineLogEvent event) {
    return this.sendSMTPMessage(this.getSubject(event), this.getMessage(event));
  }

}
