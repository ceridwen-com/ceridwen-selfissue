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
package com.ceridwen.selfissue.client.logging;

import java.util.logging.Handler;

import org.w3c.dom.Node;

import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.logging.SMTPLogHandler;

public class SMTPLoggingHandlerWrapper extends LoggingHandlerWrapper {

    @Override
    public Handler getLoggingHandler(Node item) {
        SMTPLogHandler handler = new SMTPLogHandler(
                Configuration.getSubProperty(item, "smtpServer"),
                Configuration.getSubProperty(item, "sender"),
                Configuration.getSubProperty(item, "recipients"));
        handler.setLevel(super.getLevel(item));
        handler.setThrottle(1, 60);
        return handler;
    }

}
