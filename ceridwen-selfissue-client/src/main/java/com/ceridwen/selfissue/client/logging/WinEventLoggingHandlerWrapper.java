/*
 * Copyright (C) 2024 Ceridwen Limited
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
package com.ceridwen.selfissue.client.logging;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.logging.WinEventLogHandler;
import java.util.logging.Handler;
import org.w3c.dom.Node;

/**
 *
 * @author Matthew.Dovey
 */
public class WinEventLoggingHandlerWrapper extends LoggingHandlerWrapper {
  @Override
  public Handler getLoggingHandler(Node item) {
    WinEventLogHandler handler = new WinEventLogHandler(Configuration.getSubProperty(item, "winEventServer"), "Ceridwen SelfIssue");
    handler.setLevel(super.getLevel(item));
    return handler;
  }  
}