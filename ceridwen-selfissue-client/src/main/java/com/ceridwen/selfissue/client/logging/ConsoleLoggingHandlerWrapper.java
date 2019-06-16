/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import java.util.logging.ConsoleHandler;

/**
 *
 * @author Matthew.Dovey
 */
public class ConsoleLoggingHandlerWrapper extends LoggingHandlerWrapper {
  @Override
  public Handler getLoggingHandler(Node item) {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(super.getLevel(item));
    return handler;
  }  
}
