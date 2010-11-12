package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.logging.RESTLogHandler;

public class RESTLoggingHandlerWrapper extends LoggingHandlerWrapper {

    @Override
    public Handler getLoggingHandler(Node item) {
        RESTLogHandler handler = new RESTLogHandler(
                Configuration.getSubProperty(item, "URL"));
        handler.setLevel(super.getLevel(item));
        handler.setThrottle(1, 1);
        return handler;
    }
}
