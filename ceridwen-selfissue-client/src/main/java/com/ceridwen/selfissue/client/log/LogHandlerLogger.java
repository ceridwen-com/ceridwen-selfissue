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
package com.ceridwen.selfissue.client.log;

import com.ceridwen.selfissue.client.core.OutOfOrderInterface;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.w3c.dom.Node;

/**
 *
 * @author Matthew
 */
public abstract class LogHandlerLogger extends OnlineLogLogger {
    
    @Override
    public void initialise(Node config, OutOfOrderInterface ooo) {
      super.initialise(config, ooo);
    }
    
    protected abstract Handler getHandler(String source);

    public synchronized boolean sendHandlerMessage(String source, Level level, String msg) {
        Handler handler = getHandler(source);
        handler.setLevel(level);
        LogRecord record = new LogRecord(level, msg);
        handler.publish(record);
        return true;
    }

    @Override
    public boolean log(OnlineLogEvent event) { 
      return this.sendHandlerMessage(source.isBlank()?"Ceridwen SelfIssue Logger":source, event.isActionRequired()?Level.WARNING:Level.INFO, this.getMessage(event));
    }
}
