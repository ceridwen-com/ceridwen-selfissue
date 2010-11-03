/*******************************************************************************
 * Copyright (c) 2010 Matthew J. Dovey (www.ceridwen.com).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at 
 * <http://www.gnu.org/licenses/>
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Matthew J. Dovey (www.ceridwen.com) - initial API and implementation
 ******************************************************************************/
package com.ceridwen.selfissue.client.log;

import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import com.ceridwen.circulation.SIP.messages.Message;

public class OnlineLogManager implements OnlineLog {
  private Vector<OnlineLog> loggers = new Vector<OnlineLog>();


  public void addOnlineLogger(OnlineLog logger) {
    loggers.add(logger);
  }

  public void removeOnlineLogger(OnlineLog logger) {
    loggers.remove(logger);
  }

  public void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request,
                          Message response) {
    Enumeration<OnlineLog> enumerate = loggers.elements();
    while (enumerate.hasMoreElements()) {
      ((OnlineLog)enumerate.nextElement()).recordEvent(level, library, addInfo, originalTransactionTime, request, response);
    }
  }
}
