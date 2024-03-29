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

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;

import com.ceridwen.circulation.SIP.messages.Message;

public class OnlineLogManager implements OnlineLog {
  private final ArrayList<OnlineLog> loggers = new ArrayList<>();


  public void addOnlineLogger(OnlineLog logger) {
    loggers.add(logger);
  }

  public void removeOnlineLogger(OnlineLog logger) {
    logger.close();
    loggers.remove(logger);
  }

  @Override
  public void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request,
                          Message response) throws IOException {
        for (OnlineLog logger: loggers) {
            logger.recordEvent(level, library, addInfo, originalTransactionTime, request, response);
        }
  }

    @Override
    public void close() {
        for (OnlineLog logger: loggers) {
            removeOnlineLogger(logger);
        }
    }
}
