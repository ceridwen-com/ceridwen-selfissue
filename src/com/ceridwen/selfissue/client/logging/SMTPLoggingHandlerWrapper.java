package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

public class SMTPLoggingHandlerWrapper extends LoggingHandlerWrapper {

    @Override
    public Handler getLoggingHandler(Node item) {
        com.ceridwen.util.logging.SMTPLogHandler handler = new com.ceridwen.util.logging.SMTPLogHandler(
                Configuration.getSubProperty(item, "smtpServer"),
                Configuration.getSubProperty(item, "sender"),
                Configuration.getSubProperty(item, "recipients"));
        handler.setLevel(super.getLevel(item));
        handler.setThrottle(1, 60);
        return handler;
    }

}
