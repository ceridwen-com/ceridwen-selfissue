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

import org.apache.commons.net.smtp.SMTPClient;
import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import java.io.IOException;

public class MailLogger extends OnlineLogLogger {

  @Override
  public void initialise(Node config, OutOfOrderInterface ooo) {
    super.initialise(config, ooo);
  }

  public synchronized boolean sendSMTPMessage(String subject, String message) {
    try {
      SMTPClient smtp = new SMTPClient();
      smtp.connect(host);
      smtp.setSoTimeout(idleTimeout);

      if (smtp.login()) {
        if (!smtp.sendSimpleMessage(source,
                                    target,
                                    "From: " + source + "\r\n" +
                                    "To: " + target + "\r\n" +
                                    "Date: " +
                                    new SimpleDateFormat("E, d MMM yyyy HH:mm:ss Z").
                                    format(new java.util.Date()) + "\r\n" +
                                    "Subject: " + subject + "\r\n\r\n" + message)) {
          return false;
        }
      }
      else {
        smtp.disconnect();
        return false;
      }
      smtp.logout();
      smtp.disconnect();
      return true;
    }
    catch (IOException ex) {
      return false;
    }
  }

  @Override
  public boolean log(OnlineLogEvent event) {
    return this.sendSMTPMessage(this.getSubject(event), this.getMessage(event));
  }

}
