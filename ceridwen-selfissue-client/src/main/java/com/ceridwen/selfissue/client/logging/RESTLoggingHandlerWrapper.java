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

import com.ceridwen.util.logging.RESTLogHandler;

public class RESTLoggingHandlerWrapper extends LoggingHandlerWrapper {

    @Override
  protected Handler getLoggingHandlerInstance(Node config) {
        RESTLogHandler handler = new RESTLogHandler(
            (this.getSSL(config)?"https":"http") + "://" + this.getHost(config) + ":" + 
            this.getPort(config, this.getSSL(config)?443:80) + this.getTarget(config)
        );
        handler.setThrottle(1, 1);
        return handler;
    }
}
