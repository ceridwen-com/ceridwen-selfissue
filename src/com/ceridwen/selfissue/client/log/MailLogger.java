package com.ceridwen.selfissue.client.log;

import com.ceridwen.circulation.SIP.messages.CheckOutResponse;
import com.ceridwen.circulation.SIP.messages.PatronInformationResponse;
import com.ceridwen.circulation.SIP.messages.CheckOut;
import com.ceridwen.circulation.SIP.messages.PatronInformation;
import org.apache.commons.net.smtp.SMTPClient;
import com.ceridwen.selfissue.client.*;
import java.util.*;
import java.text.SimpleDateFormat;
import org.w3c.dom.Node;
import com.ceridwen.circulation.SIP.messages.CheckInResponse;
import com.ceridwen.circulation.SIP.messages.CheckIn;

public class MailLogger extends OnlineLogLogger {
  protected String relay;
  protected String to;
  protected int connectionTimeout;
  protected int idleTimeout;
  protected String from;

  public void initialise(Node config) {
    super.initialise(config);

    relay = Configuration.getSubProperty(config, "Relay");
    from = Configuration.getSubProperty(config, "From");
    to = Configuration.getSubProperty(config, "To");
    connectionTimeout = Configuration.getIntSubProperty(config, "ConnectionTimeout") * 1000;
    idleTimeout = Configuration.getIntSubProperty(config, "IdleTimeout") * 1000;
  }

  public synchronized boolean sendSMTPMessage(String subject, String message) {
    try {
      SMTPClient smtp = new SMTPClient();
      smtp.connect(relay);
      smtp.setSoTimeout(idleTimeout);

      if (smtp.login()) {
        if (!smtp.sendSimpleMessage(from,
                                    to,
                                    "From: " + from + "\r\n" +
                                    "To: " + to + "\r\n" +
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
    catch (Exception ex) {
      return false;
    }
  }

  public boolean log(OnlineLogEvent event) {
    return this.sendSMTPMessage(this.getSubject(event), this.getMessage(event));
  }

}
