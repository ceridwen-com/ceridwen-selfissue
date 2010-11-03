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

import java.io.File;
import java.util.Date;

import com.ceridwen.circulation.SIP.messages.Message;
import com.ceridwen.util.PersistentQueue;
import com.ceridwen.util.Spooler;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */

public class OnlineLogDevice implements OnlineLog {
  private Spooler spool;
  private OnlineLogLogger processor;
  private static final long delay = 10000;

  public OnlineLogDevice(File file, OnlineLogLogger processor, int period) {
    this.processor = processor;
    spool = new Spooler(new PersistentQueue(file), this.processor, delay, period);
  }

  public void recordEvent(int level, String library, String addInfo, Date originalTransactionTime, Message request, Message response) {
    OnlineLogEvent ev = new OnlineLogEvent();
    ev.setLevel(level);
    ev.setOriginalTransactionTime(originalTransactionTime);
    ev.setLibrary(library);
    ev.setRequest(request);
    ev.setResponse(response);
    ev.setAddInfo(addInfo);
    try {
      ev.setSource(java.net.InetAddress.getLocalHost().getHostName());
    } catch (Exception ex) {

    }
    ev.setTimeStamp(new java.util.Date());
    if (!this.processor.process(ev)) {
      spool.add(ev);
    }
  }
  protected void finalize() throws java.lang.Throwable {
    spool.cancelScheduler();
    super.finalize();
  }
}
