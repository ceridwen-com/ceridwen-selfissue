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
package com.ceridwen.selfissue.client.spooler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Title: RTSI</p>
 * <p>Description: Real Time Self Issue</p>
 * <p>Copyright: </p>
 * <p>Company: </p>
 * @author Matthew J. Dovey
 * @version 2.0
 */
import com.ceridwen.selfissue.client.config.Configuration;
import com.ceridwen.util.collections.Queue;
import com.ceridwen.util.collections.Spooler;
import com.ceridwen.util.collections.SpoolerProcessor;

public class OfflineSpoolerDevice implements OfflineSpooler {
  private Spooler<OfflineSpoolObject> spool;
  private static final long delay = 10000;

  public OfflineSpoolerDevice(String file, SpoolerProcessor<OfflineSpoolObject> processor, int period) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
    @SuppressWarnings("unchecked")
	Queue<OfflineSpoolObject> persistentQueue = (Queue<OfflineSpoolObject>)Class.forName(Configuration.getProperty("UI/Control/PersistentQueueImplementation")).getConstructor(new Class[]{String.class}).newInstance(new Object[]{file});
    spool = new Spooler<OfflineSpoolObject>(persistentQueue, processor, delay, period);
  }

  public void add(OfflineSpoolObject m) throws IOException {
    spool.add(m);
  }
  public int size() {
    return spool.size();
  }
  protected void finalize() throws java.lang.Throwable {
    spool.cancelScheduler();
    super.finalize();
  }
}
