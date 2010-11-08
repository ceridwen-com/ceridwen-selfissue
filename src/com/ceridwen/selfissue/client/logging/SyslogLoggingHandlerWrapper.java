package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;

public class SyslogLoggingHandlerWrapper extends LoggingHandlerWrapper {

    @Override
    public Handler getLoggingHandler(Node item) {
        com.ceridwen.util.logging.SyslogLogHandler handler = new com.ceridwen.util.logging.SyslogLogHandler(
                Configuration.getSubProperty(item, "syslogHost"),
                Configuration.getIntSubProperty(item, "syslogPort"));
        handler.setLevel(super.getLevel(item));
        handler.setThrottle(1, 15);
        return handler;
    }

}
